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

package viewmodels.checkAnswers.sections.items

import models.requests.DataRequest
import models.response.referenceData.ItemPackaging
import models.{GoodsType, Index}
import pages.sections.items._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import queries.ItemsPackagingCount
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.list

import javax.inject.Inject

class ItemPackagingSummary @Inject()(
                                      tag: views.html.components.tag,
                                      list: list
                                    ) {

  def row(itemIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    request.userAnswers.get(ItemBulkPackagingChoicePage(itemIdx)).flatMap {
      case true =>
        constructBulkPackagingSummary(itemIdx)
      case _ =>
        getPackagesForItem(itemIdx) match {
          case Nil => None
          case packages =>
            Some(SummaryListRowViewModel(
              key = "itemsAddToList.packagesCyaLabel",
              value = ValueViewModel(HtmlContent(list(constructPackagingValues(itemIdx, packages))))
            ))
        }
    }

  private[items] def getPackagesForItem(itemIdx: Index)(implicit request: DataRequest[_]): Seq[(ItemPackaging, Option[String])] =
    request.userAnswers.get(ItemsPackagingCount(itemIdx)).fold[Seq[(ItemPackaging, Option[String])]](Seq.empty) { count =>
      (0 until count).map(Index(_)).map { packageIdx =>
        request.userAnswers.get(ItemSelectPackagingPage(itemIdx, packageIdx)) ->
          request.userAnswers.get(ItemPackagingQuantityPage(itemIdx, packageIdx))
      }.collect {
        case (Some(packaging), quantity) => packaging -> quantity
      }
    }

  private[items] def constructPackagingValues(itemIdx: Index, packages: Seq[(ItemPackaging, Option[String])])
                                             (implicit request: DataRequest[_], messages: Messages): Seq[Html] = {
    if (packages.exists(_._2.isEmpty)) {
      // if any quantities are missing, show incomplete tag
      Seq(HtmlFormat.fill(
        Seq(incompleteTag(setMarginLeft = false))
      ))
    } else {
      // otherwise, turn Some(quantity) into quantity and render list of packaging types
      packages
        .collect {
          case (itemPackaging, Some(quantity)) => (itemPackaging, quantity)
        }
        .zipWithIndex.map {
          case ((itemPackaging, quantity), packageIdx) =>
            HtmlFormat.fill(
              Html(messages("itemsAddToList.packagesCyaValue", quantity, itemPackaging.description)) +:
                (if (ItemsPackagingSectionItems(itemIdx, Index(packageIdx)).isCompleted) Seq() else Seq(incompleteTag()))
            )
        }
    }
  }

  private[items] def constructBulkPackagingSummary(itemIdx: Index)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    for {
      epc <- request.userAnswers.get(ItemExciseProductCodePage(itemIdx))
      bulkType <- request.userAnswers.get(ItemBulkPackagingSelectPage(itemIdx))
    } yield {
      implicit val goodsType: GoodsType = GoodsType.apply(epc)
      SummaryListRowViewModel(
        key = "itemsAddToList.packagesCyaLabel",
        value = ValueViewModel(HtmlContent(
          HtmlFormat.fill(Seq(
            Some(Html(bulkType.description)),
            if (ItemsSectionItem(itemIdx).bulkPackagingPagesComplete) None else Some(incompleteTag())
          ).flatten)
        ))
      )
    }
  }

  private def incompleteTag(setMarginLeft: Boolean = true)(implicit messages: Messages): Html = tag(
    message = messages("taskListStatus.incomplete"),
    colour = "red",
    extraClasses = ("float-none" +: (if (setMarginLeft) Seq("govuk-!-margin-left-2") else Seq())).mkString(" ")
  )
}
