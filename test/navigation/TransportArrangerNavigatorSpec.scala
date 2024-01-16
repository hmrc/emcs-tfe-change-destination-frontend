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
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.transportArranger.TransportArranger.{Consignee, Consignor, GoodsOwner, Other}
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.transportArranger._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class TransportArrangerNavigatorSpec extends SpecBase {

  val navigator = new TransportArrangerNavigator

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  "TransportArrangerNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to TransportArranger CYA" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testArc)
      }

      "for the TransportArrangerPage" - {

        "must go to TransportArrangerName page" - {

          "when the answer is `Goods Owner`" in {
            val userAnswers = emptyUserAnswers.set(TransportArrangerPage, GoodsOwner)

            navigator.nextPage(TransportArrangerPage, NormalMode, userAnswers) mustBe
              controllers.sections.transportArranger.routes.TransportArrangerNameController.onPageLoad(testErn, testArc, NormalMode)
          }

          "when the answer is `Other`" in {
            val userAnswers = emptyUserAnswers.set(TransportArrangerPage, Other)

            navigator.nextPage(TransportArrangerPage, NormalMode, userAnswers) mustBe
              controllers.sections.transportArranger.routes.TransportArrangerNameController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "must go to CAM-TA05" - {

          "when the answer is `Consignee`" in {
            val userAnswers = emptyUserAnswers.set(TransportArrangerPage, Consignee)

            navigator.nextPage(TransportArrangerPage, NormalMode, userAnswers) mustBe
              controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testArc)
          }

          "when the answer is `Consignor`" in {
            val userAnswers = emptyUserAnswers.set(TransportArrangerPage, Consignor)

            navigator.nextPage(TransportArrangerPage, NormalMode, userAnswers) mustBe
              controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testArc)
          }
        }

      }

      "for the TransportArrangerNamePage" - {

        "must go to TransportArrangerVatPage" in {
          val userAnswers = emptyUserAnswers.set(TransportArrangerNamePage, "some name here")

          navigator.nextPage(TransportArrangerNamePage, NormalMode, userAnswers) mustBe
            controllers.sections.transportArranger.routes.TransportArrangerVatController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the TransportArrangerVatPage" - {

        "must go to TransportArrangerAddressPage" in {

          navigator.nextPage(TransportArrangerVatPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.transportArranger.routes.TransportArrangerAddressController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the TransportArrangerAddressPage" - {

        "must go to TransportArrangerCheckAnswersPage" in {

          navigator.nextPage(TransportArrangerAddressPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testArc)
        }
      }

      "for the TransportArrangerCheckAnswersPage" - {

        "must go to the next Section" in {

          navigator.nextPage(TransportArrangerCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            routes.DraftMovementController.onPageLoad(testErn, testArc)
        }
      }

      "for the TransportArrangerReviewPage" - {

        "must go to the CAM-TA01 when the user answers yes (change answers)" in {

          navigator.nextPage(TransportArrangerReviewPage, NormalMode, emptyUserAnswers.set(TransportArrangerReviewPage, ChangeAnswers)) mustBe
            controllers.sections.transportArranger.routes.TransportArrangerController.onPageLoad(testErn, testArc, NormalMode)
        }

        "must go to the task list page when the user answers no (keep answers)" in {

          navigator.nextPage(TransportArrangerReviewPage, NormalMode, emptyUserAnswers.set(TransportArrangerReviewPage, KeepAnswers)) mustBe
            routes.DraftMovementController.onPageLoad(testErn, testArc)
        }

        "must go back to the review page when the user doesn't have an answer" in {

          navigator.nextPage(TransportArrangerReviewPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.transportArranger.routes.TransportArrangerReviewController.onPageLoad(testErn, testArc)
        }
      }
    }

    "in Check mode" - {

      "for the TransportArrangerPage" - {

        "if the TransportArrange is GoodsOwner or Other" - {

          "if any of the manual entry fields are empty" - {

            "must go to the TransportArrangerName page" in {

              val userAnswers = emptyUserAnswers
                .set(TransportArrangerPage, GoodsOwner)
                .set(TransportArrangerVatPage, testVatNumber)

              navigator.nextPage(TransportArrangerPage, CheckMode, userAnswers)(dataRequest(FakeRequest(), movementDetails = maxGetMovementResponse.copy(transportArrangerTrader = None))) mustBe
                controllers.sections.transportArranger.routes.TransportArrangerNameController.onPageLoad(testErn, testArc, NormalMode)
            }
          }

          "if all of the manual entry fields are populated" - {

            "must go to the CheckAnswers page" in {

              val userAnswers = emptyUserAnswers
                .set(TransportArrangerPage, Other)
                .set(TransportArrangerNamePage, "Jeff")
                .set(TransportArrangerVatPage, testVatNumber)
                .set(TransportArrangerAddressPage, testUserAddress)

              navigator.nextPage(TransportArrangerPage, CheckMode, userAnswers) mustBe
                controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testArc)
            }
          }
        }

        "if the answer is Consignee" - {

          "must go to CheckAnswers" in {

            val userAnswers = emptyUserAnswers.set(TransportArrangerPage, Consignee)

            navigator.nextPage(TransportArrangerPage, CheckMode, userAnswers) mustBe
              controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testArc)
          }
        }

        "if the answer is Consignor" - {

          "must go to CheckAnswers" in {
            val userAnswers = emptyUserAnswers.set(TransportArrangerPage, Consignor)

            navigator.nextPage(TransportArrangerPage, CheckMode, userAnswers) mustBe
              controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testArc)
          }
        }
      }

      "for the TransportArrangerNamePage" - {

        "must go to TransportArrangerCheckAnswersPage" in {
          val userAnswers = emptyUserAnswers.set(TransportArrangerNamePage, "some name here")

          navigator.nextPage(TransportArrangerNamePage, CheckMode, userAnswers) mustBe
            controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testArc)
        }
      }

      "for the TransportArrangerVatPage" - {

        "must go to TransportArrangerCheckAnswersPage" in {

          navigator.nextPage(TransportArrangerVatPage, CheckMode, emptyUserAnswers) mustBe
            controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testArc)
        }
      }

      "for the TransportArrangerAddressPage" - {

        "must go to TransportArrangerCheckAnswersPage" in {

          navigator.nextPage(TransportArrangerAddressPage, CheckMode, emptyUserAnswers) mustBe
            controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onPageLoad(testErn, testArc)
        }
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
