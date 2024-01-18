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
import fixtures.messages.sections.journeyType.JourneyTimeHoursMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.journeyType.JourneyTimeHoursPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Value
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class JourneyTimeHoursSummarySpec extends SpecBase with Matchers {

  ".row" - {

    Seq(JourneyTimeHoursMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer for hours in the user answers" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(journeyTime = "1 days"))

            JourneyTimeHoursSummary.row(false) mustBe None
          }
        }

        "when there's an answer" - {

            "when onReviewPage is set to false" - {

              "must output the expected row" in {

                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(JourneyTimeHoursPage, 1))

                JourneyTimeHoursSummary.row(false) mustBe Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text(messagesForLanguage.cyaValue(1))),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.journeyType.routes.JourneyTimeHoursController.onPageLoad(testErn, testArc, CheckMode).url,
                        id = "journeyTimeHours"
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                    )
                  )
                )
              }
            }
          "when onReviewPage is set to true" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(JourneyTimeHoursPage, 1))

              JourneyTimeHoursSummary.row(true) mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.cyaValue(1))),
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
