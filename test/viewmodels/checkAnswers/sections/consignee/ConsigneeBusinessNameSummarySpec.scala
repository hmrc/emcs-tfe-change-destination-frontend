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

package viewmodels.checkAnswers.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeBusinessNameMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeBusinessNamePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ConsigneeBusinessNameSummarySpec extends SpecBase with Matchers {

  "ConsigneeBusinessNameSummary" - {

    Seq(ConsigneeBusinessNameMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer in the user answers or 801" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(consigneeTrader = None))

            ConsigneeBusinessNameSummary.row mustBe None
          }
        }

        "when there's no answer in the user answers (defaulting to 801)" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            ConsigneeBusinessNameSummary.row mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text("ConsigneeTraderName")),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeConsigneeBusinessName"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }
        }

        "when there's an answer" - {


          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeBusinessNamePage, "testName"))

            ConsigneeBusinessNameSummary.row mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text("testName")),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeConsigneeBusinessName"
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
