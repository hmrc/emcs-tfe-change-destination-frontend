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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.auth.SignedOutView

class SignedOutControllerSpec extends SpecBase {

  "SignedOut Controller" - {
    val request = FakeRequest()
    lazy val view = app.injector.instanceOf[SignedOutView]
    lazy val testController = new SignedOutController(messagesControllerComponents, appConfig, view)

    "must return OK and the correct view for a none saved sign out" in {
      val result = testController.signOutNotSaved(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(guidance = "signedOut.guidance.notSaved")(request, messages(request)).toString
      await(result).session(request).get(REFERER_SESSION_KEY) mustBe None
    }

    "must return OK and the correct view for a saved signed out" in {
      val result = testController.signOutSaved(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(guidance = "signedOut.guidance.saved")(request, messages(request)).toString
      await(result).session(request).get(REFERER_SESSION_KEY) mustBe None
    }

    "must return OK and the correct view for a feedback signed out" in {
      val result = testController.signOutWithSurvey()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some("http://localhost:9514/feedback/emcstfe/beta")
    }
  }

}
