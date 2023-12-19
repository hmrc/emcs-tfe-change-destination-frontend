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
import models.NormalMode
import pages.Page
import pages.sections.importInformation.{CheckAnswersImportPage, ImportCustomsOfficeCodePage}

class ImportInformationNavigatorSpec extends SpecBase {

  val navigator = new ImportInformationNavigator

  "ImportInformationNavigator" - {
    "in Normal mode" - {
      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.importInformation.routes.CheckYourAnswersImportController.onPageLoad(testErn,testDraftId)
      }
    }

    "for the ImportCustomsOfficeCodePage" - {
      "must go to CheckYourAnswers page" in {

        val userAnswers = emptyUserAnswers.set(ImportCustomsOfficeCodePage, "AB123456")

        navigator.nextPage(ImportCustomsOfficeCodePage, NormalMode, userAnswers) mustBe
          controllers.sections.importInformation.routes.CheckYourAnswersImportController.onPageLoad(testErn,testDraftId)
      }
    }

    "for the CYA page" - {
      "must go to the tasklist page" in {

        navigator.nextPage(CheckAnswersImportPage, NormalMode, emptyUserAnswers) mustBe
          controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId)
      }
    }

  }
}
