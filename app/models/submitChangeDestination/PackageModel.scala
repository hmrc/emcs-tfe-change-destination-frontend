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

import models.Index
import models.requests.DataRequest
import models.sections.items.ItemPackagingSealTypeModel
import pages.sections.items._
import play.api.libs.json.{Json, OFormat}
import queries.ItemsPackagingCount
import utils.ModelConstructorHelpers

case class PackageModel(
                         kindOfPackages: String,
                         numberOfPackages: Option[Int],
                         shippingMarks: Option[String],
                         commercialSealIdentification: Option[String],
                         sealInformation: Option[String]
                       )

object PackageModel extends ModelConstructorHelpers {

  def applyBulkPackaging(idx: Index)(implicit request: DataRequest[_]): Seq[PackageModel] = {
    val sealType: Option[ItemPackagingSealTypeModel] = request.userAnswers.get(ItemBulkPackagingSealTypePage(idx))
    Seq(
      PackageModel(
        kindOfPackages = mandatoryPage(ItemBulkPackagingSelectPage(idx)).packagingType.toString,
        numberOfPackages = None,
        shippingMarks = None,
        commercialSealIdentification = sealType.map(_.sealType),
        sealInformation = sealType.flatMap(_.optSealInformation)
      )
    )
  }

  def applyIndividualPackaging(idx: Index)(implicit request: DataRequest[_]): Seq[PackageModel] = {
    request.userAnswers.get(ItemsPackagingCount(idx)) match {
      case Some(0) | None => Seq()
      case Some(value) =>
        (0 until value)
          .map(Index(_))
          .map {
            packagingIdx =>
              val sealType: Option[ItemPackagingSealTypeModel] = request.userAnswers.get(ItemPackagingSealTypePage(idx, packagingIdx))
              PackageModel(
                kindOfPackages = mandatoryPage(ItemSelectPackagingPage(idx, packagingIdx)).packagingType,
                numberOfPackages = Some(mandatoryPage(ItemPackagingQuantityPage(idx, packagingIdx)).toInt),
                shippingMarks = request.userAnswers.get(ItemPackagingShippingMarksPage(idx, packagingIdx)),
                commercialSealIdentification = sealType.map(_.sealType),
                sealInformation = sealType.flatMap(_.optSealInformation)
              )
          }
    }
  }

  implicit val fmt: OFormat[PackageModel] = Json.format
}
