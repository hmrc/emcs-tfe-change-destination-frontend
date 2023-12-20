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
import controllers.sections.sad.{routes => sadRoutes}
import models.sections.sad.SadAddToListModel
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.sad.{ImportNumberPage, SadAddToListPage}

class SadNavigatorSpec extends SpecBase {
  val navigator = new SadNavigator

  "SadNavigator" - {
    "in Normal mode" - {
      "must go from a page that doesn't exist in the route map to Sad CYA" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

      "for ImportNumberPage" - {
        "must redirect to AddToList" in {
          navigator.nextPage(ImportNumberPage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            controllers.sections.sad.routes.SadAddToListController.onPageLoad(testErn, testDraftId)
        }
      }

      "for SadAddToListPage" - {
        "when Some(Yes)" - {
          "must redirect to Import" in {
            navigator.nextPage(SadAddToListPage, NormalMode, emptyUserAnswers.set(SadAddToListPage, SadAddToListModel.Yes)) mustBe
              controllers.sections.sad.routes.ImportNumberController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
            navigator.nextPage(SadAddToListPage, NormalMode, emptyUserAnswers.set(SadAddToListPage, SadAddToListModel.Yes).set(ImportNumberPage(testIndex1), "")) mustBe
              controllers.sections.sad.routes.ImportNumberController.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)
          }
        }
        "when Some(NoMoreToCome)" - {
          "must redirect to tasklist" in {
            navigator.nextPage(SadAddToListPage, NormalMode, emptyUserAnswers.set(SadAddToListPage, SadAddToListModel.NoMoreToCome)) mustBe
              routes.DraftMovementController.onPageLoad(testErn, testDraftId)
          }
        }
        "when None" - {
          "must redirect to journey recovery" in {
            navigator.nextPage(SadAddToListPage, NormalMode, emptyUserAnswers) mustBe
              routes.JourneyRecoveryController.onPageLoad()
          }
        }
      }

    }

    "in Check mode" - {
      "must go to CheckYourAnswersSadController" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          sadRoutes.SadAddToListController.onPageLoad(testErn, testDraftId)
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
