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

import controllers.sections.items.routes
import models.requests.{CnCodeInformationItem, DataRequest}
import models.sections.items.ItemsAddToListItemModel
import models.{Index, NormalMode}
import pages.sections.items.{ItemBulkPackagingChoicePage, ItemCommodityCodePage, ItemExciseProductCodePage, ItemsSectionItem}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import queries.{ItemsCount, ItemsPackagingCount}
import services.GetCnCodeInformationService
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import viewmodels.checkAnswers.sections.items.{ItemBrandNameSummary, ItemCommercialDescriptionSummary, ItemPackagingSummary, ItemQuantitySummary}
import viewmodels.govuk.TagFluency
import viewmodels.govuk.summarylist._
import viewmodels.taskList.{Completed, InProgress}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class ItemsAddToListHelper @Inject()(tag: views.html.components.tag,
                                     span: views.html.components.span,
                                     cnCodeInformationService: GetCnCodeInformationService,
                                     itemPackagingSummary: ItemPackagingSummary) extends TagFluency with Logging {

  private val headingLevel = 2


  def allItemsSummary(implicit request: DataRequest[_], messages: Messages, headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Seq[SummaryList]] =
    getAddedItems match {
      case Nil => Future.successful(Nil)
      case items => itemsWithUnitOfMeasure(items).map(_.map(summaryList))
    }

  private def itemsWithUnitOfMeasure(items: Seq[ItemsAddToListItemModel])
                                    (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Seq[ItemsAddToListItemModel]] =
    cnCodeInformationService.getCnCodeInformation(CnCodeInformationItem(items).distinct).map { response =>
      items.map { item =>
        item.copy(unitOfMeasure = response.collectFirst {
          case (cnItem, cnInfo) if cnItem.productCode == item.exciseProductCode && item.commodityCode.contains(cnItem.cnCode) =>
            cnInfo.unitOfMeasure
        })
      }
    }

  private def getAddedItems(implicit request: DataRequest[_]): Seq[ItemsAddToListItemModel] =
    request.userAnswers.get(ItemsCount).fold[Seq[ItemsAddToListItemModel]](Seq.empty) { count =>
      (0 until count).map(Index(_)).flatMap { itemIdx =>
        (request.userAnswers.get(ItemExciseProductCodePage(itemIdx)), request.userAnswers.get(ItemCommodityCodePage(itemIdx))) match {
          case (Some(epc), Some(commodityCode)) =>
            Some(ItemsAddToListItemModel(epc, commodityCode, itemIdx, ItemsSectionItem(itemIdx).status))
          case _ =>
            logger.warn(s"[getAddedItems] Could not retrieve Excise Product Code and/or Commodity Code for item at idx: '$itemIdx'")
            None
        }
      }
    }

  private def summaryList(item: ItemsAddToListItemModel)(implicit request: DataRequest[_], messages: Messages): SummaryList = {
    SummaryListViewModel(
      rows = Seq(
        ItemBrandNameSummary.row(item.idx, showChangeLinks = false),
        ItemCommercialDescriptionSummary.row(item.idx, showChangeLinks = false),
        item.unitOfMeasure.flatMap(ItemQuantitySummary.row(item.idx, _, showChangeLinks = false)),
        itemPackagingSummary.row(item.idx)
      ).flatten
    ).withCard(
      Card(
        title = Some(cardTitle(item)),
        actions = Some(Actions(items = Seq(
          changeLink(item),
          continueEditingLink(item),
          Some(removeLink(item))
        ).flatten))
      )
    )
  }

  private def cardTitle(item: ItemsAddToListItemModel)(implicit messages: Messages): CardTitle =
    item.status match {
      case InProgress => CardTitle(
        content = HtmlContent(HtmlFormat.fill(Seq(
          span(messages("itemsAddToList.itemCardTitle", item.idx.displayIndex), Some("govuk-!-margin-right-2")),
          tag(
            message = messages("taskListStatus.incomplete"),
            colour = "red"
          )
        ))),
        headingLevel = Some(headingLevel)
      )
      case _ => CardTitle(
        content = HtmlContent(span(messages("itemsAddToList.itemCardTitle", item.idx.displayIndex))),
        headingLevel = Some(headingLevel)
      )
    }

  private def changeLink(item: ItemsAddToListItemModel)(implicit request: DataRequest[_], messages: Messages): Option[ActionItem] = {
    item.status match {
      case Completed =>
        Some(ActionItemViewModel(
          content = Text(messages("site.change")),
          href = routes.ItemCheckAnswersController.onPageLoad(request.ern, request.arc, item.idx).url,
          id = s"changeItem-${item.idx.displayIndex}"
        ).withVisuallyHiddenText(messages("itemsAddToList.itemCardTitle", item.idx.displayIndex)))
      case _ =>
        None
    }
  }

  private def removeLink(item: ItemsAddToListItemModel)(implicit request: DataRequest[_], messages: Messages): ActionItem =
    ActionItemViewModel(
      content = Text(messages("site.remove")),
      href = routes.ItemRemoveItemController.onPageLoad(request.ern, request.arc, item.idx).url,
      id = s"removeItem-${item.idx.displayIndex}"
    ).withVisuallyHiddenText(messages("itemsAddToList.itemCardTitle", item.idx.displayIndex))

  private def continueEditingLink(item: ItemsAddToListItemModel)(implicit request: DataRequest[_], messages: Messages): Option[ActionItem] =
    item.status match {
      case InProgress =>
        val continueEditingLink =
          if (ItemsSectionItem(item.idx).itemPagesWithoutPackagingComplete(item.exciseProductCode)(item.goodsType, request)) {
            if (request.userAnswers.get(ItemBulkPackagingChoicePage(item.idx)).contains(true)) {
              routes.ItemBulkPackagingChoiceController.onPageLoad(request.ern, request.arc, item.idx, NormalMode)
            } else {
              if (request.userAnswers.get(ItemsPackagingCount(item.idx)).exists(_ > 1)) {
                routes.ItemsPackagingAddToListController.onPageLoad(request.ern, request.arc, item.idx)
              } else {
                routes.ItemSelectPackagingController.onPageLoad(request.ern, request.arc, item.idx, Index(0), NormalMode)
              }
            }
          } else {
            routes.ItemExciseProductCodeController.onPageLoad(request.ern, request.arc, item.idx, NormalMode)
          }

        Some(ActionItemViewModel(
          content = Text(messages("site.continueEditing")),
          href = continueEditingLink.url,
          id = s"editItem-${item.idx.displayIndex}"
        ).withVisuallyHiddenText(messages("itemsAddToList.itemCardTitle", item.idx.displayIndex)))
      case _ => None
    }
}
