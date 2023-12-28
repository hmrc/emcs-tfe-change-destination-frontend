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
import models.{CheckMode, NormalMode, ReviewMode, UserAddress}
import pages._
import pages.sections.firstTransporter._
import play.api.test.FakeRequest

class FirstTransporterNavigatorSpec extends SpecBase {

  val navigator = new FirstTransporterNavigator

  implicit val request = dataRequest(FakeRequest())

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
          routes.DraftMovementController.onPageLoad(testErn, testArc)
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
            .set(FirstTransporterVatPage, "")
            .set(FirstTransporterAddressPage, UserAddress(None, "", "", ""))
          navigator.nextPage(FirstTransporterNamePage, CheckMode, userAnswers) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }
        "when FirstTransporterVatPage is empty" in {
          val userAnswers = emptyUserAnswers
            .set(FirstTransporterNamePage, "")
            .set(FirstTransporterAddressPage, UserAddress(None, "", "", ""))
          navigator.nextPage(FirstTransporterNamePage, CheckMode, userAnswers) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }
        "when FirstTransporterAddressPage is empty" in {
          val userAnswers = emptyUserAnswers
            .set(FirstTransporterNamePage, "")
            .set(FirstTransporterVatPage, "")
          navigator.nextPage(FirstTransporterNamePage, CheckMode, userAnswers) mustBe
            controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        }
      }
      "must redirect to CYA" - {
        "when FirstTransporterNamePage, FirstTransporterVatPage and FirstTransporterAddressPage are non-empty" in {
          val userAnswers = emptyUserAnswers
            .set(FirstTransporterNamePage, "")
            .set(FirstTransporterVatPage, "")
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
