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
import controllers.sections.guarantor.routes
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger._
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.guarantor._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class GuarantorNavigatorSpec extends SpecBase {

  val navigator = new GuarantorNavigator

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  "GuarantorNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Guarantor CYA" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc)
      }

      "for GuarantorReviewPage" - {

        "when the GuarantorRequiredPage is false and the GuarantorReviewPage is ChangeAnswers" - {
          "must goto CAM-G01 (Guarantor Arranger)" in {
            val userAnswers = emptyUserAnswers
              .set(GuarantorRequiredPage, false)
              .set(GuarantorReviewPage, ChangeAnswers)

            navigator.nextPage(GuarantorReviewPage, NormalMode, userAnswers) mustBe
              routes.GuarantorArrangerController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "when the GuarantorRequiredPage is false and the GuarantorReviewPage is KeepAnswers" - {
          "must goto CAM-G06 (Check Answers)" in {
            val userAnswers = emptyUserAnswers
              .set(GuarantorRequiredPage, false)
              .set(GuarantorReviewPage, KeepAnswers)

            navigator.nextPage(GuarantorReviewPage, NormalMode, userAnswers) mustBe
              routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc)
          }
        }

        "when the GuarantorRequiredPage is true and the GuarantorReviewPage is ChangeAnswers" - {
          "must goto CAM-G06 (Check Answers)" in {
            val userAnswers = emptyUserAnswers
              .set(GuarantorRequiredPage, true)
              .set(GuarantorReviewPage, ChangeAnswers)

            navigator.nextPage(GuarantorReviewPage, NormalMode, userAnswers) mustBe
              routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc)
          }
        }

        "when the GuarantorRequiredPage is true and the GuarantorReviewPage is KeepAnswers" - {
          "must goto CAM-G06 (Check Answers)" in {
            val userAnswers = emptyUserAnswers
              .set(GuarantorRequiredPage, true)
              .set(GuarantorReviewPage, KeepAnswers)

            navigator.nextPage(GuarantorReviewPage, NormalMode, userAnswers) mustBe
              routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc)
          }
        }
      }

      "for GuarantorRequiredPage" - {

        "must go to Guarantor CYA when 'No' Guarantor is required" in {

          val userAnswers = emptyUserAnswers.set(GuarantorRequiredPage, false)

          navigator.nextPage(GuarantorRequiredPage, NormalMode, userAnswers) mustBe
            routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc)
        }

        "must go to Guarantor Arranger when 'Yes' a Guarantor is required" in {

          val userAnswers = emptyUserAnswers.set(GuarantorRequiredPage, true)

          navigator.nextPage(GuarantorRequiredPage, NormalMode, userAnswers) mustBe
            routes.GuarantorArrangerController.onPageLoad(testErn, testArc, NormalMode)
        }

        "must go to Guarantor Arranger when an answer doesn't exist (we know it's required)" in {

          navigator.nextPage(GuarantorRequiredPage, NormalMode, emptyUserAnswers) mustBe
            routes.GuarantorArrangerController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for GuarantorArrangerPage" - {

        GuarantorArranger.displayValues.foreach {
          case value@(GoodsOwner | Transporter) =>
            "must goto CAM-G03" - {
              s"when the arranger value is $value aka ${value.getClass.getSimpleName}" in {
                val userAnswers = emptyUserAnswers.set(GuarantorArrangerPage, value)

                navigator.nextPage(GuarantorArrangerPage, NormalMode, userAnswers) mustBe
                  routes.GuarantorNameController.onPageLoad(testErn, testArc, NormalMode)
              }
            }
          case value =>
            "must goto CAM-G06" - {
              s"when the arranger value is $value aka ${value.getClass.getSimpleName}" in {
                val userAnswers = emptyUserAnswers.set(GuarantorArrangerPage, value)

                navigator.nextPage(GuarantorArrangerPage, NormalMode, userAnswers) mustBe
                  routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc)
              }
            }
        }
      }

      "for GuarantorNamePage" - {
        "must goto CAM-G04" in {
          navigator.nextPage(GuarantorNamePage, NormalMode, emptyUserAnswers) mustBe
            routes.GuarantorVatController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for GuarantorVATPage" - {
        "must goto CAM-G05" in {
          navigator.nextPage(GuarantorVatPage, NormalMode, emptyUserAnswers) mustBe
            routes.GuarantorAddressController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for GuarantorAddressPage" - {
        "must goto CAM-G06" in {
          navigator.nextPage(GuarantorAddressPage, NormalMode, emptyUserAnswers) mustBe
            routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc)
        }
      }

      "for GuarantorCheckAnswersPage" - {
        "must goto the tasklist page" in {
          navigator.nextPage(GuarantorCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            controllers.routes.TaskListController.onPageLoad(testErn, testArc)
        }
      }
    }

    "in Check mode" - {
      "must go to Guarantor CYA" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc)
      }
    }

    "in Review mode" - {
      "must go to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
      }
    }
  }
}
