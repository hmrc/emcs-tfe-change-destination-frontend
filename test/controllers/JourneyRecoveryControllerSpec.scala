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

package controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl
import views.html.{JourneyRecoveryContinueView, JourneyRecoveryStartAgainView}

class JourneyRecoveryControllerSpec extends SpecBase {

  "JourneyRecovery Controller" - {
    val request = FakeRequest()
    lazy val continueView = app.injector.instanceOf[JourneyRecoveryContinueView]
    lazy val startAgainView = app.injector.instanceOf[JourneyRecoveryStartAgainView]

    lazy val testController = new JourneyRecoveryController(messagesControllerComponents, continueView, startAgainView)

    "when a relative continue Url is supplied" - {
      "must return OK and the continue view" in {
        val continueUrl = RedirectUrl("/foo")
        val result = testController.onPageLoad(Some(continueUrl))(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual continueView(continueUrl.unsafeValue)(request, messages(request)).toString
      }
    }

    "when an absolute continue Url is supplied" - {
      "must return OK and the start again view" in {
        val continueUrl = RedirectUrl("https://foo.com")
        val result = testController.onPageLoad(Some(continueUrl))(request)


        status(result) mustEqual OK
        contentAsString(result) mustEqual startAgainView()(request, messages(request)).toString
      }
    }

    "when no continue Url is supplied" - {
      "must return OK and the start again view" in {
        val result = testController.onPageLoad(None)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual startAgainView()(request, messages(request)).toString
      }
    }
  }

}
