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
import play.api.test.FakeRequest

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  implicit val request = dataRequest(FakeRequest())

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn, testArc)
      }

      "for the CheckYourAnswers page" - {

        "must go to the Confirmation page" in {

          navigator.nextPage(CheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            routes.ConfirmationController.onPageLoad(testErn, testArc)
        }
      }
      "for the Declaration page" - {

        "must go to the Confirmation page" in {

          navigator.nextPage(DeclarationPage, NormalMode, emptyUserAnswers) mustBe
            routes.ConfirmationController.onPageLoad(testErn, testArc)
        }
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
      }
    }

    "in review mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
      }
    }
  }
}
