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
import models.{CheckMode, NormalMode, ReviewMode, UserAddress, VatNumberModel}
import pages._
import pages.sections.firstTransporter._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class FirstTransporterNavigatorSpec extends SpecBase {

  val navigator = new FirstTransporterNavigator

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  "in Normal mode" - {

    "must go from a page that doesn't exist in the route map to First Transporter CYA" in {

      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
        controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(testErn, testArc)
    }

    "for the FirstTransporterNamePage (CAM-FT01)" - {

      "must go to CAM-FT02" in {
        val userAnswers = emptyUserAnswers.set(FirstTransporterNamePage, "transporter name here")

        navigator.nextPage(FirstTransporterNamePage, NormalMode, userAnswers) mustBe
          controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
      }

    }

    "for the FirstTransporterVATPage (CAM-FT02)" - {

      "must go to CAM-FT03" in {
        navigator.nextPage(FirstTransporterVatPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.arc, NormalMode)
      }

    }

    "for the FirstTransporterAddressPage (CAM-FT03)" - {

      "must go to CAM-FT04" in {
        navigator.nextPage(FirstTransporterAddressPage, NormalMode, emptyUserAnswers) mustBe
          controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.arc)
      }

    }

    "for the FirstTransporterCheckAnswersPage" - {

      "must go to tasklist" in {
        navigator.nextPage(FirstTransporterCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
          routes.TaskListController.onPageLoad(testErn, testArc)
      }

    }

    "for the FirstTransporterReviewPage" - {

      "must go to FirstTransporterNamePage (CAM-FT01)" - {

        "when the user answers yes (change answers)" in {
          navigator.nextPage(FirstTransporterReviewPage, NormalMode, emptyUserAnswers.set(FirstTransporterReviewPage, ChangeAnswers)) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterNameController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "must go to the task list" - {

        "when the user answers no (keep answers)" in {
          navigator.nextPage(FirstTransporterReviewPage, NormalMode, emptyUserAnswers.set(FirstTransporterReviewPage, KeepAnswers)) mustBe
            routes.TaskListController.onPageLoad(testErn, testArc)
        }
      }

      "must go back to the FirstTransporterReviewPage (COD-07)" - {

        "when the user has no answer" in {
          navigator.nextPage(FirstTransporterReviewPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterReviewController.onPageLoad(testErn, testArc)
        }
      }
    }

  }

  "in Check mode" - {
    "must go from a page that doesn't exist in the route map to CYA" in {

      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
        controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(emptyUserAnswers.ern, emptyUserAnswers.arc)
    }

    "for the FirstTransporterNamePage" - {
      "must redirect to FirstTransporterVatController" - {
        "when FirstTransporterNamePage is empty" in {
          val userAnswers = emptyUserAnswers
            .set(FirstTransporterVatPage, VatNumberModel(hasVatNumber = false, None))
            .set(FirstTransporterAddressPage, UserAddress(None, "", "", ""))
          navigator.nextPage(FirstTransporterNamePage, CheckMode, userAnswers)(dataRequest(FakeRequest(), movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None))) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }
        "when FirstTransporterVatPage is empty" in {
          val userAnswers = emptyUserAnswers
            .set(FirstTransporterNamePage, "")
            .set(FirstTransporterAddressPage, UserAddress(None, "", "", ""))
          navigator.nextPage(FirstTransporterNamePage, CheckMode, userAnswers)(dataRequest(FakeRequest(), movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None))) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }
        "when FirstTransporterAddressPage is empty" in {
          val userAnswers = emptyUserAnswers
            .set(FirstTransporterNamePage, "")
            .set(FirstTransporterVatPage, VatNumberModel(hasVatNumber = false, None))
          navigator.nextPage(FirstTransporterNamePage, CheckMode, userAnswers)(dataRequest(FakeRequest(), movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None))) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }
      }
      "must redirect to CYA" - {
        "when FirstTransporterNamePage, FirstTransporterVatPage and FirstTransporterAddressPage are non-empty" in {
          val userAnswers = emptyUserAnswers
            .set(FirstTransporterNamePage, "")
            .set(FirstTransporterVatPage, VatNumberModel(hasVatNumber = false, None))
            .set(FirstTransporterAddressPage, UserAddress(None, "", "", ""))
          navigator.nextPage(FirstTransporterNamePage, CheckMode, userAnswers) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
        }
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
