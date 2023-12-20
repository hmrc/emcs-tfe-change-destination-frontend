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

package viewmodels.helpers

import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import models.{CheckMode, GoodsType, Index}
import pages.sections.items._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import queries.ItemsPackagingCount
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.items._
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import javax.inject.Inject

class ItemCheckAnswersHelper @Inject()(
                                        itemExciseProductCodeSummary: ItemExciseProductCodeSummary,
                                        itemCommodityCodeSummary: ItemCommodityCodeSummary,
                                        itemWineOperationsChoiceSummary: ItemWineOperationsChoiceSummary,
                                        itemWineMoreInformationSummary: ItemWineMoreInformationSummary,
                                        itemBulkPackagingSealTypeSummary: ItemBulkPackagingSealTypeSummary
                                      ) {

  private val headingLevel = 3

  def constructItemDetailsCard(idx: Index, cnCodeInformation: CnCodeInformation)
                              (implicit request: DataRequest[_], messages: Messages): SummaryList = {
    SummaryListViewModel(
      card = Some(CardViewModel(messages("itemCheckAnswers.itemDetailsCardTitle"), headingLevel = headingLevel, actions = None)),
      rows = Seq(
        Some(itemExciseProductCodeSummary.row(idx, cnCodeInformation, CheckMode)),
        itemCommodityCodeSummary.row(idx, cnCodeInformation, CheckMode),
        ItemBrandNameSummary.row(idx),
        ItemCommercialDescriptionSummary.row(idx),
        ItemAlcoholStrengthSummary.row(idx),
        ItemDegreesPlatoSummary.row(idx),
        ItemMaturationPeriodAgeSummary.row(idx),
        ItemDensitySummary.row(idx),
        ItemFiscalMarksChoiceSummary.row(idx),
        ItemFiscalMarksSummary.row(idx),
        ItemGeographicalIndicationChoiceSummary.row(idx),
        ItemGeographicalIndicationSummary.row(idx),
        ItemSmallIndependentProducerSummary.row(idx),
        ItemProducerSizeSummary.row(idx)
      ).flatten
    )
  }

  def constructQuantityCard(idx: Index, cnCodeInformation: CnCodeInformation)
                           (implicit request: DataRequest[_], messages: Messages): SummaryList =
    SummaryListViewModel(
      card = Some(CardViewModel(messages("itemCheckAnswers.quantityCardTitle"), headingLevel = headingLevel, actions = None)),
      rows = Seq(
        ItemQuantitySummary.row(idx, cnCodeInformation.unitOfMeasure),
        ItemNetMassSummary.row(idx),
        ItemGrossMassSummary.row(idx)
      ).flatten
    )

  def constructWineDetailsCard(idx: Index)
                              (implicit request: DataRequest[_], messages: Messages): SummaryList =
    SummaryListViewModel(
      card = Some(CardViewModel(messages("itemCheckAnswers.wineDetailsCardTitle"), headingLevel = headingLevel, actions = None)),
      rows = Seq(
        itemWineOperationsChoiceSummary.row(idx),
        ItemImportedWineChoiceSummary.row(idx),
        ItemWineGrowingZoneSummary.row(idx),
        ItemWineOriginSummary.row(idx),
        Some(itemWineMoreInformationSummary.row(idx))
      ).flatten
    )

  def constructPackagingCard(idx: Index, cnCodeInformation: CnCodeInformation)(implicit request: DataRequest[_], messages: Messages): SummaryList = {
    val (rows, cardActions): (Seq[SummaryListRow], Option[Actions]) = if (request.userAnswers.get(ItemBulkPackagingChoicePage(idx)).contains(true)) {
      // bulk
      (
        bulkPackagingSummaryListRows(idx, cnCodeInformation),
        None
      )
    } else {
      // not bulk
      (
        notBulkPackagingSummaryListRows(idx, cnCodeInformation),
        Some(Actions(items = Seq(ActionItemViewModel(
            "site.change",
            href = controllers.sections.items.routes.ItemsPackagingAddToListController.onPageLoad(request.ern, request.arc, idx).url,
            s"changeItemPackaging${idx.displayIndex}"
          ).withVisuallyHiddenText(messages(s"${ItemsPackagingAddToListPage(idx)}.change.hidden")))))
        )
    }

    SummaryListViewModel(
      card = Some(CardViewModel(messages("itemCheckAnswers.packagingCardTitle"), headingLevel = headingLevel, actions = cardActions)),
      rows = rows
    )
  }

  private[helpers] def bulkPackagingSummaryListRows(idx: Index, cnCodeInformation: CnCodeInformation)
                                          (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] =
    Seq(
      ItemBulkPackagingChoiceSummary.row(idx, GoodsType.apply(cnCodeInformation.exciseProductCode)),
      ItemBulkPackagingSelectSummary.row(idx),
      ItemBulkPackagingSealChoiceSummary.row(idx)
    ).flatten ++ itemBulkPackagingSealTypeSummary.rows(idx)

  private[helpers] def notBulkPackagingSummaryListRows(idx: Index, cnCodeInformation: CnCodeInformation)
                                          (implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {
    def packageTypeRow(packageIndex: Index): Option[SummaryListRow] = {
      (request.userAnswers.get(ItemSelectPackagingPage(idx, packageIndex)), request.userAnswers.get(ItemPackagingQuantityPage(idx, packageIndex))) match {
        case (Some(packaging), Some(quantity)) => Some(SummaryListRowViewModel(
          key = KeyViewModel(messages(s"${ItemCheckAnswersPage(idx)}.packageTypeKey", packageIndex.displayIndex)),
          value = ValueViewModel(HtmlFormat.escape(messages(s"${ItemCheckAnswersPage(idx)}.packageTypeValue", quantity, packaging.description)).toString())
        ))
        case _ => None
      }
    }

    Seq(
      ItemBulkPackagingChoiceSummary.row(idx, GoodsType.apply(cnCodeInformation.exciseProductCode))
    ).flatten ++ (request.userAnswers.get(ItemsPackagingCount(idx)) match {
      case Some(value) => (0 until value).map(packageIdx => packageTypeRow(Index(packageIdx)))
      case None => Nil
    }).flatten
  }
}
