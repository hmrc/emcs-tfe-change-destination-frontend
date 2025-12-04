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
import featureswitch.core.config.FeatureSwitching
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.auth.SignedOutView

import java.net.URLEncoder

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

      "when not triggered because of a timeout" - {

        "must return SEE_OTHER to sign-out with a redirect to feedbackSurvey" in {

          val result = testController.signOut()(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(s"http://localhost:8308/bas-gateway/sign-out-without-state?continue=${URLEncoder.encode(config.feedbackFrontendSurveyUrl, "UTF-8")}")
        }
      }

      "when triggered because of a timeout" - {

        "when triggered from a page that can be saved" - {

          "must return SEE_OTHER to sign-out with a redirect to timeout, data saved" in {

            val request = FakeRequest("GET", "/trader/1234567890/movement/1234/consignee-information")

            val result = testController.signOut(true)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe
              Some(s"http://localhost:8308/bas-gateway/sign-out-without-state?continue=${URLEncoder.encode(appConfig.host + routes.SignedOutController.signedOutSaved().url, "UTF-8")}")
          }
        }

        "when triggered from a page that can be saved" - {

          "must return SEE_OTHER to sign-out with a redirect to timeout, data not saved" in {

            val request = FakeRequest("GET", "/trader/1234567890/info")

            val result = testController.signOut(true)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe
              Some(s"http://localhost:8308/bas-gateway/sign-out-without-state?continue=${URLEncoder.encode(appConfig.host + routes.SignedOutController.signedOutNotSaved().url, "UTF-8")}")
          }
        }
      }
    }
  }
}
