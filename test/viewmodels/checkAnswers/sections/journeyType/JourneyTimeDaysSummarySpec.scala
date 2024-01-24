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
import fixtures.messages.sections.journeyType.JourneyTimeDaysMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.journeyType.{JourneyTimeDaysPage, JourneyTimeHoursPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Value
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class JourneyTimeDaysSummarySpec extends SpecBase with Matchers {

  ".row" - {

    Seq(JourneyTimeDaysMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "must output no row" - {

          "when there is no 'local' user answer for JourneyTimeDaysPage and there is hours in 801" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(journeyTime = "1 hours"))

            JourneyTimeDaysSummary.row(onReviewPage = false) mustBe None
          }

          "when there is no 'local' user answer for JourneyTimeDaysPage and days have been entered" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(JourneyTimeHoursPage, 2), movementDetails = maxGetMovementResponse.copy(journeyTime = "1 hours"))

            JourneyTimeDaysSummary.row(onReviewPage = false) mustBe None
          }
        }

        "when there's an answer" - {

          "show the 801 answer when the user is on the review page (with no action links)" in {
            implicit lazy val request = dataRequest(FakeRequest(), movementDetails = maxGetMovementResponse.copy(journeyTime = "1 days"))

            JourneyTimeDaysSummary.row(onReviewPage = true) mustBe Some(
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.cyaValue(1))),
                actions = Seq()
              )
            )
          }

          s"when onReviewPage is set to false" - {

            "must show the 801 answer" - {

              "when no 'hour' answer has been provided and there is no local answer for 'days'" in {
                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(journeyTime = "1 days"))

                JourneyTimeDaysSummary.row(onReviewPage = false) mustBe Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text(messagesForLanguage.cyaValue(1))),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.journeyType.routes.JourneyTimeDaysController.onPageLoad(testErn, testArc, CheckMode).url,
                        id = "journeyTimeDays"
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                    )
                  )
                )
              }
            }

            "must show the new answer" - {

              "when the new answer has been provided on the days page (and the 801 is hours)" in {

                implicit lazy val request = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers.set(JourneyTimeDaysPage, 2),
                  movementDetails = maxGetMovementResponse.copy(journeyTime = "1 hours"))

                JourneyTimeDaysSummary.row(onReviewPage = false) mustBe Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text(messagesForLanguage.cyaValue(2))),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.journeyType.routes.JourneyTimeDaysController.onPageLoad(testErn, testArc, CheckMode).url,
                        id = "journeyTimeDays"
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                    )
                  )
                )
              }

              "when the new answer has been provided on the days page (and the 801 is days - overwriting the existing answer)" in {

                implicit lazy val request = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers.set(JourneyTimeDaysPage, 2),
                  movementDetails = maxGetMovementResponse.copy(journeyTime = "1 days"))

                JourneyTimeDaysSummary.row(onReviewPage = false) mustBe Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text(messagesForLanguage.cyaValue(2))),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.journeyType.routes.JourneyTimeDaysController.onPageLoad(testErn, testArc, CheckMode).url,
                        id = "journeyTimeDays"
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
  }
}
