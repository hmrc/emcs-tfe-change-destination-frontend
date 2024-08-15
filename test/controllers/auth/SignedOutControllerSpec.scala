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

package controllers.auth

import base.SpecBase
import config.AppConfig
import featureswitch.core.config.{FeatureSwitching, RedirectToFeedbackSurvey}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.auth.SignedOutView

class SignedOutControllerSpec extends SpecBase with FeatureSwitching {

  override lazy val config = app.injector.instanceOf[AppConfig]
  lazy val request = FakeRequest()
  lazy val view = app.injector.instanceOf[SignedOutView]
  lazy val testController = new SignedOutController(messagesControllerComponents, appConfig, view)

  "SignedOut Controller" - {

    ".signedOutNotSaved" - {
      "must return OK and the correct view for a none saved sign out" in {
        val result = testController.signedOutNotSaved(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(guidance = "signedOut.guidance.notSaved")(request, messages(request)).toString
      }
    }

    ".signedOutSaved" - {
      "must return OK and the correct view for a saved signed out" in {
        val result = testController.signedOutSaved(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(guidance = "signedOut.guidance.saved")(request, messages(request)).toString
      }
    }

    ".signOut" - {

      "when feedbackSurvey is disabled" - {
        "when called from a page within the draft flow which is savable" - {
          "must return SEE_OTHER to sign-out with a redirect to signedOutSaved" in {

            disable(RedirectToFeedbackSurvey)
            val result = testController.signOut()(FakeRequest("GET", s"/emcs/create-movement/trader/$testErn/movement/$testArc/page"))

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some("http://localhost:8308/gg/sign-out?continue=http%3A%2F%2Flocalhost%3A8319%2Femcs%2Fchange-destination%2Faccount%2Fsigned-out-saved")
          }
        }

        "when called from a page within the draft flow which is NOT savable" - {
          "must return SEE_OTHER to sign-out with a redirect to signedOutNotSaved" in {

            disable(RedirectToFeedbackSurvey)
            val result = testController.signOut()(FakeRequest("GET", s"/emcs/create-movement/trader/$testErn/info"))

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some("http://localhost:8308/gg/sign-out?continue=http%3A%2F%2Flocalhost%3A8319%2Femcs%2Fchange-destination%2Faccount%2Fsigned-out")
          }
        }
      }

      "when feedbackSurvey is enabled" - {
        "must return SEE_OTHER to sign-out with a redirect to feedbackSurvey" in {

          enable(RedirectToFeedbackSurvey)
          val result = testController.signOut()(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some("http://localhost:8308/gg/sign-out?continue=http%3A%2F%2Flocalhost%3A9514%2Ffeedback%2Femcstfe%2Fbeta")
        }
      }
    }
  }

}
