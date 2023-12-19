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
import pages._
import pages.sections.consignor._

class ConsignorNavigatorSpec extends SpecBase {

  val navigator = new ConsignorNavigator

  "ConsignorNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Consignor CYA" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onPageLoad(testErn, testDraftId)
      }

      "for the ConsignorAddress page" - {

        "must go to the CheckYourAnswersConsignor page" in {

          navigator.nextPage(ConsignorAddressPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onPageLoad(testErn, testDraftId)
        }
      }

      "for the CheckYourAnswersConsignor page" - {

        "must go to the tasklist" in {
          navigator.nextPage(CheckAnswersConsignorPage, NormalMode, emptyUserAnswers) mustBe
            routes.DraftMovementController.onPageLoad(testErn, testDraftId)
        }
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswersConsignor" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onPageLoad(testErn, testDraftId)
      }

      "must go from the ConsignorAddressPage to CheckYourAnswersConsignor" in {

        navigator.nextPage(ConsignorAddressPage, CheckMode, emptyUserAnswers) mustBe
          controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onPageLoad(testErn, testDraftId)
      }
    }

    "in review mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
