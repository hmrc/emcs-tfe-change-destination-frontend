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
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.dispatch._

class DispatchNavigatorSpec extends SpecBase {
  val navigator = new DispatchNavigator

  "DispatchNavigator" - {

    "in Normal mode" - {
      "must go from a page that doesn't exist in the route map to Dispatch CYA" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(testErn, testDraftId)
      }

      "for the DispatchWarehouseExcisePage" - {

        "must go to DispatchConsignorDetails page" in {
          navigator.nextPage(DispatchWarehouseExcisePage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.dispatch.routes.DispatchUseConsignorDetailsController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the DispatchUseConsignorDetailsPage" - {

        "when using consignor details" - {

          "must go to DispatchCheckYourAnswers page" in {

            val userAnswers = emptyUserAnswers.set(DispatchUseConsignorDetailsPage, true)

            navigator.nextPage(DispatchUseConsignorDetailsPage, NormalMode, userAnswers) mustBe
              controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.arc)
          }
        }

        "when NOT using consignor details" - {

          "must go to DispatchBusinessName page" in {

            val userAnswers = emptyUserAnswers.set(DispatchUseConsignorDetailsPage, false)

            navigator.nextPage(DispatchUseConsignorDetailsPage, NormalMode, userAnswers) mustBe
              controllers.sections.dispatch.routes.DispatchBusinessNameController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.arc, NormalMode)
          }
        }
      }

      "for the DispatchBusinessNamePage" - {

        "must go to DispatchAddress page" in {

          val userAnswers = emptyUserAnswers.set(DispatchBusinessNamePage, "TestBusinessName")

          navigator.nextPage(DispatchBusinessNamePage, NormalMode, userAnswers) mustBe
            controllers.sections.dispatch.routes.DispatchAddressController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.arc, NormalMode)
        }
      }

      "for the DispatchAddressPage" - {

        "must go to CYA page" in {

          navigator.nextPage(DispatchAddressPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(testErn, testDraftId)
        }
      }

      "for the CYA page" - {

        "must go to the tasklist page" in {

          navigator.nextPage(DispatchCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            routes.DraftMovementController.onPageLoad(testErn, testDraftId)
        }
      }
    }

    "in Check mode" - {

      "for the DispatchBusinessNamePage" - {

        "must go to DispatchAddress page" in {

          val userAnswers = emptyUserAnswers.set(DispatchBusinessNamePage, "TestBusinessName")

          navigator.nextPage(DispatchBusinessNamePage, CheckMode, userAnswers) mustBe
            controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.arc)
        }
      }

      "for the DispatchAddressPage" - {

        "must go to CYA page" in {

          navigator.nextPage(DispatchAddressPage, CheckMode, emptyUserAnswers) mustBe
            controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(testErn, testDraftId)
        }
      }
    }

    "in Check mode" - {
      "must go to CheckYourAnswersDispatchController" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(testErn, testDraftId)
      }
    }

    "in Review mode" - {
      "must go to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
