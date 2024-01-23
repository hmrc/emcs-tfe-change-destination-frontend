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

package navigation

import base.SpecBase
import controllers.routes
import models._
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.journeyType.HowMovementTransported.{AirTransport, Other}
import pages._
import pages.sections.journeyType._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class JourneyTypeNavigatorSpec extends SpecBase {

  val navigator = new JourneyTypeNavigator

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  "JourneyTypeNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to JourneyType CYA" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(testErn, testArc)
      }

      "for the HowMovementTransported page" - {

        "must go to the GiveInformationOtherTransport page" - {

          "when the option selected is 'Other'" in {
            val userAnswers = emptyUserAnswers
              .set(HowMovementTransportedPage, Other)

            navigator.nextPage(HowMovementTransportedPage, NormalMode, userAnswers) mustBe
              controllers.sections.journeyType.routes.GiveInformationOtherTransportController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to the correct journey time page" - {

          "when the option selected is not `Other`" - {

            "when the user previously entered a days value" in {
              val userAnswers = emptyUserAnswers
                .set(HowMovementTransportedPage, AirTransport)
                .set(JourneyTimeDaysPage, 1)

              navigator.nextPage(HowMovementTransportedPage, NormalMode, userAnswers)(dataRequest(FakeRequest(), movementDetails = maxGetMovementResponse.copy(journeyTime = "10 days"))) mustBe
                controllers.sections.journeyType.routes.JourneyTimeDaysController.onPageLoad(testErn, testArc, NormalMode)
            }

            "when the user previously entered a hours value" in {
              val userAnswers = emptyUserAnswers
                .set(HowMovementTransportedPage, AirTransport)
                .set(JourneyTimeHoursPage, 1)

              navigator.nextPage(HowMovementTransportedPage, NormalMode, userAnswers) mustBe
                controllers.sections.journeyType.routes.JourneyTimeHoursController.onPageLoad(testErn, testArc, NormalMode)
            }
          }
        }
      }

      "for the GiveInformationOtherTransport page" - {

        "must go to the Journey Type Days page" - {
          val userAnswers = emptyUserAnswers
            .set(HowMovementTransportedPage, Other)
            .set(GiveInformationOtherTransportPage, "some information text")

          navigator.nextPage(GiveInformationOtherTransportPage, NormalMode, userAnswers) mustBe
            controllers.sections.journeyType.routes.JourneyTimeDaysController.onPageLoad(testErn, testArc, NormalMode)

        }
      }

      "for the JourneyTimeDaysPage page" - {

        "must go to the Journey Type CYA page" - {
          val userAnswers = emptyUserAnswers
            .set(HowMovementTransportedPage, AirTransport)

          navigator.nextPage(JourneyTimeDaysPage, NormalMode, userAnswers) mustBe
            controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(testErn, testArc)

        }
      }

      "for the JourneyTimeHoursPage page" - {

        "must go to the Journey Type CYA page" - {
          val userAnswers = emptyUserAnswers
            .set(HowMovementTransportedPage, AirTransport)

          navigator.nextPage(JourneyTimeHoursPage, NormalMode, userAnswers) mustBe
            controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(testErn, testArc)

        }
      }

      "for the CheckYourAnswers page" - {

        "must go to the construction page" in {
          navigator.nextPage(CheckYourAnswersJourneyTypePage, NormalMode, emptyUserAnswers) mustBe
            routes.TaskListController.onPageLoad(testErn, testArc)
        }
      }

      "for the JourneyTypeReviewPage" - {

        "must go to HowMovementTransportedController" - {

          "when the user answers yes (change answers)" in {
            navigator.nextPage(JourneyTypeReviewPage, NormalMode, emptyUserAnswers.set(JourneyTypeReviewPage, ChangeAnswers)) mustBe
              controllers.sections.journeyType.routes.HowMovementTransportedController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to the task list" - {

          "when the user answers no" in {
            navigator.nextPage(JourneyTypeReviewPage, NormalMode, emptyUserAnswers.set(JourneyTypeReviewPage, KeepAnswers)) mustBe
              routes.TaskListController.onPageLoad(testErn, testArc)
          }
        }

        "must go back to the JourneyTypeReviewPage" - {

          "when the user has no answer" in {
            navigator.nextPage(JourneyTypeReviewPage, NormalMode, emptyUserAnswers) mustBe
              controllers.sections.journeyType.routes.JourneyTypeReviewController.onPageLoad(testErn, testArc)
          }
        }
      }

    }

    "in Check mode" - {

      "for the HowMovementTransported page" - {

        "must go to the GiveInformationOtherTransport page" - {

          "when the option selected is 'Other'" in {
            val userAnswers = emptyUserAnswers
              .set(HowMovementTransportedPage, Other)

            navigator.nextPage(HowMovementTransportedPage, CheckMode, userAnswers) mustBe
              controllers.sections.journeyType.routes.GiveInformationOtherTransportController.onPageLoad(testErn, testArc, CheckMode)
          }
        }

        "must go to the Journey Type CYA page" - {

          "when the option selected is not `Other`" in {
            val userAnswers = emptyUserAnswers
              .set(HowMovementTransportedPage, AirTransport)

            navigator.nextPage(HowMovementTransportedPage, CheckMode, userAnswers) mustBe
              controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(testErn, testArc)
          }
        }
      }

      "must go to CheckYourAnswersJourneyTypeController" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          controllers.sections.journeyType.routes.CheckYourAnswersJourneyTypeController.onPageLoad(testErn, testArc)
      }
    }

    "in Review mode" - {
      "must go to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
      }
    }


  }
}
