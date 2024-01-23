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
import models.{CheckMode, NormalMode, ReviewMode}
import pages.Page
import pages.sections.exportInformation.{ExportCustomsOfficePage, ExportInformationCheckAnswersPage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class ExportInformationNavigatorSpec extends SpecBase {

  val navigator = new ExportInformationNavigator

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  "in Normal mode" - {

    "must go from a page that doesn't exist in the route map to Export Information CYA" in {

      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
        controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onPageLoad(testErn, testArc)
    }

    "for the ExportCustomsOfficePage" - {

      "must go to ExportInformationCheckAnswersPage" in {

        val userAnswers = emptyUserAnswers.set(ExportCustomsOfficePage, "AB123456")

        navigator.nextPage(ExportCustomsOfficePage, NormalMode, userAnswers) mustBe
          controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onPageLoad(testErn, testArc)
      }
    }

    "for the ExportInformationCheckAnswersPage" - {

      "must go to the next section" in {

        navigator.nextPage(ExportInformationCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
          routes.TaskListController.onPageLoad(testErn, testArc)
      }
    }
  }

  "in Check mode" - {
    "must go to ExportInformationCheckAnswersPage" in {
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
        controllers.sections.exportInformation.routes.ExportInformationCheckAnswersController.onPageLoad(testErn, testArc)
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
