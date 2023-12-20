/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.submitChangeDestination

import models.requests.DataRequest
import models.sections.items.ItemGeographicalIndicationType
import models.{GoodsType, Index}
import pages.sections.items._
import play.api.libs.json.{Json, OFormat}
import utils.{JsonOptionFormatter, ModelConstructorHelpers}

case class WineProductModel(
                             wineProductCategory: String,
                             wineGrowingZoneCode: Option[String],
                             thirdCountryOfOrigin: Option[String],
                             otherInformation: Option[String],
                             wineOperations: Option[Seq[String]]
                           )

object WineProductModel extends ModelConstructorHelpers with JsonOptionFormatter {

  private[submitChangeDestination] def wineProductCategory(idx: Index)(implicit request: DataRequest[_]): ItemWineCategory = {
    // TODO: This WILL change with a new design
    if (request.userAnswers.get(ItemImportedWineFromEuChoicePage(idx)).contains(false)) {
      // if imported from outside EU
      ItemWineCategory.ImportedWine
    } else {
      // imported from inside EU
      val geographicalIndicationChoice: ItemGeographicalIndicationType = mandatoryPage(ItemGeographicalIndicationChoicePage(idx))

      geographicalIndicationChoice match {
        case ItemGeographicalIndicationType.NoGeographicalIndication =>
          // if no GI
          val commodityCode = mandatoryPage(ItemCommodityCodePage(idx))
          if (ItemWineCategory.varietalWines.contains(commodityCode)) {
            ItemWineCategory.EuVarietalWineWithoutPdoOrPgi
          } else {
            ItemWineCategory.EuWineWithoutPdoOrPgi
          }

        case _ =>
          // if has PDO, PGI, or GI (umbrella term for PDO/PGI)
          ItemWineCategory.EuWineWithPdoOrPgiOrGi
      }
    }
  }

  def apply(idx: Index)(implicit request: DataRequest[_]): Option[WineProductModel] = {

    val exciseProductCode = mandatoryPage(ItemExciseProductCodePage(idx))

    if (GoodsType.apply(exciseProductCode) == GoodsType.Wine) {
      Some(
        WineProductModel(
          wineProductCategory = wineProductCategory(idx).toString,
          wineGrowingZoneCode = request.userAnswers.get(ItemWineGrowingZonePage(idx)).map(_.toString),
          thirdCountryOfOrigin = request.userAnswers.get(ItemWineOriginPage(idx)).map(_.countryCode),
          otherInformation = request.userAnswers.get(ItemWineMoreInformationPage(idx)).flatten,
          wineOperations = request.userAnswers.get(ItemWineOperationsChoicePage(idx)).map(_.toSeq.map(_.code))
        )
      )
    } else {
      None
    }
  }

  implicit val fmt: OFormat[WineProductModel] = Json.format
}
