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
import controllers.sections.destination.routes
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.destination._

class DestinationNavigatorSpec extends SpecBase {
  val navigator = new DestinationNavigator

  "DestinationNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Destination CYA" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.DestinationCheckAnswersController.onPageLoad(testErn, testDraftId)
      }

      "for the DestinationWarehouseExcisePage" - {

        "must go to Destination Consignee details page" in {

          navigator.nextPage(DestinationWarehouseExcisePage, NormalMode, emptyUserAnswers) mustBe
            routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the DestinationWarehouseVatPage" - {

        "must go to test Only page" in {

          navigator.nextPage(DestinationWarehouseVatPage, NormalMode, emptyUserAnswers) mustBe
            routes.DestinationDetailsChoiceController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the DestinationDetailsChoicePage" - {

        "must go to CAM-DES03 when user selects yes" in {

          val userAnswers = emptyUserAnswers.set(DestinationDetailsChoicePage, true)

          navigator.nextPage(DestinationDetailsChoicePage, NormalMode, userAnswers) mustBe
            routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testDraftId, NormalMode)
        }

        "must go to DestinationCheckAnswersPage when user selects no" in {

          val userAnswers = emptyUserAnswers.set(DestinationDetailsChoicePage, false)

          navigator.nextPage(DestinationDetailsChoicePage, NormalMode, userAnswers) mustBe
            routes.DestinationCheckAnswersController.onPageLoad(testErn, testDraftId)
        }

        "must go to Journey Recovery if no answer is present" in {

          navigator.nextPage(DestinationDetailsChoicePage, NormalMode, emptyUserAnswers) mustBe
            controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "for the DestinationConsigneeDetailsPage" - {

        "must go to Destination Business Name page (CAM-DES04) if answer is no" in {

          val userAnswers = emptyUserAnswers.set(DestinationConsigneeDetailsPage, false)

          navigator.nextPage(DestinationConsigneeDetailsPage, NormalMode, userAnswers) mustBe
            routes.DestinationBusinessNameController.onPageLoad(testErn, testDraftId, NormalMode)

        }

        "must go to Destination Check Answers page (CAM-DES06) if answer is yes" in {

          val userAnswers = emptyUserAnswers.set(DestinationConsigneeDetailsPage, true)

          navigator.nextPage(DestinationConsigneeDetailsPage, NormalMode, userAnswers) mustBe
            routes.DestinationCheckAnswersController.onPageLoad(testErn, testDraftId)
        }

        "must go to Journey Recovery Controller if no answer is present" in {

          navigator.nextPage(DestinationConsigneeDetailsPage, NormalMode, emptyUserAnswers) mustBe
            controllers.routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "for the DestinationBusinessNamePage" - {

        "must go to Destination Address page" in {

          navigator.nextPage(DestinationBusinessNamePage, NormalMode, emptyUserAnswers) mustBe
            routes.DestinationAddressController.onPageLoad(testErn, testDraftId, NormalMode)
        }
      }

      "for the DestinationAddressPage" - {

        "must go to DestinationCheckAnswersPage page" in {

          navigator.nextPage(DestinationAddressPage, NormalMode, emptyUserAnswers) mustBe
            routes.DestinationCheckAnswersController.onPageLoad(testErn, testDraftId)
        }
      }

      "for the DestinationCheckAnswersPage" - {

        "must go to tasklist page" in {

          navigator.nextPage(DestinationCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId)
        }
      }
    }

    "in Check mode" - {

      "must go to CheckYourAnswersDestinationController" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          routes.DestinationCheckAnswersController.onPageLoad(testErn, testDraftId)
      }
    }

    "in Review mode" - {

      "must go to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
