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
import views.html.TimeoutView

class TimeoutControllerSpec extends SpecBase {

  "Timeout Controller" - {

    ".onPageLoad()" - {

      "must return OK and the correct view for a GET" in {

        lazy val view = app.injector.instanceOf[TimeoutView]
        val controller = new TimeoutController(messagesApi, messagesControllerComponents, view)(appConfig)

        val result = controller.onPageLoad()(FakeRequest())


        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(FakeRequest(), messages(FakeRequest()), appConfig).toString

      }
    }
  }
}
