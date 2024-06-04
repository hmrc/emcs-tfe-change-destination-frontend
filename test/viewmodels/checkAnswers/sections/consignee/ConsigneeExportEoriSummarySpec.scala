/*
 * Copyright 2024 HM Revenue & Customs
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

package viewmodels.checkAnswers.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExportEoriMesssages
import models.CheckMode
import models.sections.info.ChangeType.ChangeConsignee
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeExportEoriPage
import pages.sections.info.ChangeTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ConsigneeExportEoriSummarySpec extends SpecBase with Matchers {

  "ConsigneeExportEoriSummary" - {

    Seq(ConsigneeExportEoriMesssages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ChangeTypePage, ChangeConsignee))

            ConsigneeExportEoriSummary.row() mustBe None
          }
        }

        "when there's an answer from the IE801" - {

          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ConsigneeExportEoriSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text("ConsigneeTraderEori")),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.consignee.routes.ConsigneeExportEoriController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeConsigneeExportEori"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
              .set(ChangeTypePage, ChangeConsignee)
              .set(ConsigneeExportEoriPage, testEoriNumber)
            )

            ConsigneeExportEoriSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(testEoriNumber)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.consignee.routes.ConsigneeExportEoriController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeConsigneeExportEori"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }
        }
      }
    }
  }
}
