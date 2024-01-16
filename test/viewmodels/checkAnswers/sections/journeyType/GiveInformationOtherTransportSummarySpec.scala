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

package viewmodels.checkAnswers.sections.journeyType

import base.SpecBase
import fixtures.messages.sections.journeyType.GiveInformationOtherTransportMessages
import models.CheckMode
import models.response.emcsTfe.TransportModeModel
import org.scalatest.matchers.must.Matchers
import pages.sections.journeyType.GiveInformationOtherTransportPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Value
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class GiveInformationOtherTransportSummarySpec extends SpecBase with Matchers {

  ".row" - {

    Seq(GiveInformationOtherTransportMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer in the user answers or in 801" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(transportMode = TransportModeModel("ModeCode", None)))

            GiveInformationOtherTransportSummary.row(false) mustBe None
          }
        }

        "when there's no answer in the user answers (defaulting to 801)" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            GiveInformationOtherTransportSummary.row(false) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text("TransportModeComplementaryInformation")),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.journeyType.routes.GiveInformationOtherTransportController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = GiveInformationOtherTransportPage
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }
        }

        "when there's an answer" - {

          "when the onReviewPage is set to false" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(GiveInformationOtherTransportPage, "some text"))

              GiveInformationOtherTransportSummary.row(false) mustBe
                Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text("some text")),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.journeyType.routes.GiveInformationOtherTransportController.onPageLoad(testErn, testArc, CheckMode).url,
                        id = GiveInformationOtherTransportPage
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                    )
                  )
                )
            }
          }
          "when the onReviewPage is set to true" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(GiveInformationOtherTransportPage, "some text"))

              GiveInformationOtherTransportSummary.row(true) mustBe
                Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text("some text")),
                    actions = Seq()
                  )
                )
            }
          }
        }
      }
    }
  }
}
