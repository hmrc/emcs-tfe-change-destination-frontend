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

package pages.sections.items

import models.GoodsType._
import models.requests.DataRequest
import models.sections.items.ItemGeographicalIndicationType.NoGeographicalIndication
import models.{GoodsType, Index}
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import utils.JsonOptionFormatter
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case class ItemsSectionItem(idx: Index) extends Section[JsObject] with JsonOptionFormatter {

  override val path: JsPath = ItemsSectionItems.path \ idx.position

  override def status(implicit request: DataRequest[_]): TaskListStatus =
    request.userAnswers.get(ItemExciseProductCodePage(idx)) match {
      case None => NotStarted
      case Some(epc) =>
        implicit val goodsType: GoodsType = GoodsType(epc)

        if (itemPagesWithoutPackagingComplete(epc) && packagingPagesComplete) {
          Completed
        } else {
          InProgress
        }
    }

  def itemPagesWithoutPackagingComplete(epc: String)(implicit goodsType: GoodsType, request: DataRequest[_]): Boolean =
    (commonMandatoryAnswers ++
      degreesPlatoAnswer ++
      independentProducerAnswers ++
      maturationAgeAnswer ++
      wineCountryOfOriginAnswers ++
      wineMoreInformationAnswers ++
      geographicalIndicationsAnswers ++
      alcoholStrengthAnswer ++
      itemDensityAnswer(epc) ++
      fiscalMarksAnswers
      ).forall(_.isDefined)

  private[items] def packagingPagesComplete(implicit goodsType: GoodsType, request: DataRequest[_]): Boolean =
    (request.userAnswers.get(ItemExciseProductCodePage(idx)), request.userAnswers.get(ItemBulkPackagingChoicePage(idx))) match {
      case (Some(_), Some(false)) =>
        ItemsPackagingSection(idx).isCompleted
      case (Some(_), Some(true)) =>
        bulkPackagingPagesComplete
      case _ =>
        false
    }

  def bulkPackagingPagesComplete(implicit goodsType: GoodsType, request: DataRequest[_]): Boolean =
    (wineBulkOperationAnswer ++
      wineBulkGrowingZoneAnswer ++
      bulkCommercialSeals :+
      request.userAnswers.get(ItemBulkPackagingSelectPage(idx))
      ).forall(_.isDefined)

  private def commonMandatoryAnswers(implicit request: DataRequest[_]): Seq[Option[_]] =
    Seq(
      request.userAnswers.get(ItemExciseProductCodePage(idx)),
      request.userAnswers.get(ItemCommodityCodePage(idx)),
      request.userAnswers.get(ItemBrandNamePage(idx)),
      request.userAnswers.get(ItemCommercialDescriptionPage(idx)),
      request.userAnswers.get(ItemQuantityPage(idx)),
      request.userAnswers.get(ItemNetGrossMassPage(idx)),
      request.userAnswers.get(ItemBulkPackagingChoicePage(idx))
    )

  private[items] def itemDensityAnswer(epc: String)(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType == Energy && !Seq("E470", "E500", "E600", "E930").contains(epc)) {
      Seq(request.userAnswers.get(ItemDensityPage(idx)))
    }

  private[items] def fiscalMarksAnswers(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType == Tobacco) {
      request.userAnswers.get(ItemFiscalMarksChoicePage(idx)) match {
        case fiscalMarksChoice@Some(true) => Seq(fiscalMarksChoice, request.userAnswers.get(ItemFiscalMarksPage(idx)))
        case fiscalMarksChoice => Seq(fiscalMarksChoice)
      }
    }

  private[items] def wineMoreInformationAnswers(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType == Wine) {
      request.userAnswers.get(ItemWineMoreInformationChoicePage(idx)) match {
        case wineInfoChoice@Some(true) => Seq(wineInfoChoice, request.userAnswers.get(ItemWineMoreInformationPage(idx)))
        case wineInfoChoice => Seq(wineInfoChoice)
      }
    }

  private[items] def wineCountryOfOriginAnswers(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType == Wine) {
      request.userAnswers.get(ItemImportedWineFromEuChoicePage(idx)) match {
        case wineImportedChoice@Some(false) => Seq(wineImportedChoice, request.userAnswers.get(ItemWineOriginPage(idx)))
        case wineImportedChoice => Seq(wineImportedChoice)
      }
    }

  private[items] def geographicalIndicationsAnswers(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType.isAlcohol && goodsType != Beer) {
      request.userAnswers.get(ItemGeographicalIndicationChoicePage(idx)) match {
        case geographicalChoice@Some(NoGeographicalIndication) => Seq(geographicalChoice)
        case geographicalChoice => Seq(geographicalChoice, request.userAnswers.get(ItemGeographicalIndicationPage(idx)))
      }
    }

  private[items] def maturationAgeAnswer(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType == Spirits)(Seq(request.userAnswers.get(ItemMaturationPeriodAgePage(idx))))

  private[items] def alcoholStrengthAnswer(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType.isAlcohol)(Seq(request.userAnswers.get(ItemAlcoholStrengthPage(idx))))

  private[items] def independentProducerAnswers(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType.isAlcohol && request.userAnswers.get(ItemAlcoholStrengthPage(idx)).exists(_ < 8.5)) {
      request.userAnswers.get(ItemSmallIndependentProducerPage(idx)) match {
        case smallProducer@Some(true) => Seq(smallProducer, request.userAnswers.get(ItemProducerSizePage(idx)))
        case smallProducer => Seq(smallProducer)
      }
    }

  private[items] def degreesPlatoAnswer(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(request.isNorthernIrelandErn && goodsType == Beer)(Seq(request.userAnswers.get(ItemDegreesPlatoPage(idx))))

  private[items] def bulkCommercialSeals(implicit request: DataRequest[_]): Seq[Option[_]] =
    request.userAnswers.get(ItemBulkPackagingSealChoicePage(idx)) match {
      case sealChoice@Some(true) => Seq(sealChoice, request.userAnswers.get(ItemBulkPackagingSealTypePage(idx)))
      case sealChoice => Seq(sealChoice)
    }

  private[items] def wineBulkGrowingZoneAnswer(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType == Wine)((request.userAnswers.get(ItemQuantityPage(idx)), request.userAnswers.get(ItemImportedWineFromEuChoicePage(idx))) match {
      case (Some(quantity), Some(true)) if quantity > 60 => Seq(request.userAnswers.get(ItemWineGrowingZonePage(idx)))
      case _ => Seq()
    })

  private[items] def wineBulkOperationAnswer(implicit goodsType: GoodsType, request: DataRequest[_]): Seq[Option[_]] =
    mandatoryIf(goodsType == Wine)(request.userAnswers.get(ItemQuantityPage(idx)) match {
      case Some(quantity) if quantity > 60 =>
        Seq(request.userAnswers.get(ItemWineOperationsChoicePage(idx)))
      case _ => Seq()
    })

  private[items] def mandatoryIf(condition: Boolean)(f: => Seq[Option[_]]): Seq[Option[_]] = if (condition) f else Seq()

  // $COVERAGE-OFF$
  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    ItemsSection.canBeCompletedForTraderAndDestinationType
}
