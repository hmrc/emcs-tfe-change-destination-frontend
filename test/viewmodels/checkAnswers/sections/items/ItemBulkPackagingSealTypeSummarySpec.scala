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

import base.SpecBase
import controllers.sections.items.routes
import fixtures.messages.sections.items.ItemPackagingSealTypeMessages
import models.CheckMode
import models.requests.DataRequest
import models.sections.items.ItemPackagingSealTypeModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.must.Matchers
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class ItemBulkPackagingSealTypeSummarySpec extends SpecBase with Matchers {

  val link: link = app.injector.instanceOf[link]
  val itemBulkPackagingSealTypeSummary: ItemBulkPackagingSealTypeSummary = new ItemBulkPackagingSealTypeSummary(link)

  "ItemBulkPackagingSealTypeSummary" - {

    Seq(ItemPackagingSealTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output None" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            itemBulkPackagingSealTypeSummary.rows(testIndex1) mustBe Nil
          }
        }

        "when there's an answer" - {

          val sealTypeRow: SummaryListRow = SummaryListRowViewModel(
            key = messagesForLanguage.cyaLabelSealType,
            value = ValueViewModel(Text("test type")),
            actions = Seq(ActionItemViewModel(
              content = messagesForLanguage.change,
              routes.ItemBulkPackagingSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
              id = s"changeItemBulkPackagingSealType${testIndex1.displayIndex}"
            ).withVisuallyHiddenText(messagesForLanguage.cyaSealTypeHiddenChange))
          )

          val sealInformationRow: SummaryListRow = SummaryListRowViewModel(
            key = messagesForLanguage.cyaLabelSealInformation,
            value = ValueViewModel(Text("test info")),
            actions = Seq(ActionItemViewModel(
              content = messagesForLanguage.change,
              routes.ItemBulkPackagingSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
              id = s"changeItemBulkPackagingSealInformation${testIndex1.displayIndex}"
            ).withVisuallyHiddenText(messagesForLanguage.cyaSealInformationHiddenChange))
          )

          val sealInformationRowEmpty: SummaryListRow = SummaryListRowViewModel(
            key = messagesForLanguage.cyaLabelSealInformation,
            value = ValueViewModel(HtmlContent(link(
              routes.ItemBulkPackagingSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
              s"itemPackagingSealType.sealInformation.addMoreInfo"
            )))
          )

          "when there is an answer to more info" - {

            val userAnswers = emptyUserAnswers
              .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("test type", Some("test info")))

            "must output the expected row" in {

              implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

              itemBulkPackagingSealTypeSummary.rows(testIndex1) mustBe Seq(sealTypeRow, sealInformationRow)
            }
          }

          "when there is not an answer to more info" - {

            val userAnswers = emptyUserAnswers
              .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("test type", None))

            "must output the expected row" in {

              implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

              val result = itemBulkPackagingSealTypeSummary.rows(testIndex1)

              result mustBe Seq(sealTypeRow, sealInformationRowEmpty)

              implicit val doc: Document = Jsoup.parse(result.last.value.content.asHtml.toString())

              doc.text() mustBe messagesForLanguage.cyaSealInformationAddMoreInfo
            }
          }
        }
      }
    }
  }
}
