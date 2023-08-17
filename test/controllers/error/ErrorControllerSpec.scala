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

package controllers.error

import base.SpecBase
import config.AppConfig
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.auth.errors._

class ErrorControllerSpec extends SpecBase {

  class Fixture

  "Unauthorised Controller" - {

    "when calling .unauthorised" - {

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder().build()

        running(application) {
          val request = FakeRequest(GET, routes.ErrorController.unauthorised().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[UnauthorisedView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view()(request, messages(application)).toString
        }
      }
    }

    "when calling .notAnOrganisation" - {

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder().build()

        running(application) {
          val request = FakeRequest(GET, routes.ErrorController.notAnOrganisation().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NotAnOrganisationView]
          val config = application.injector.instanceOf[AppConfig]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view()(request, messages(application), config).toString
        }
      }
    }

    "when calling .inactiveEnrolment" - {

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder().build()

        running(application) {
          val request = FakeRequest(GET, routes.ErrorController.inactiveEnrolment().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[InactiveEnrolmentView]
          val config = application.injector.instanceOf[AppConfig]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view()(request, messages(application), config).toString
        }
      }
    }

    "when calling .noEnrolment" - {

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder().build()

        running(application) {
          val request = FakeRequest(GET, routes.ErrorController.noEnrolment().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NoEnrolmentView]
          val config = application.injector.instanceOf[AppConfig]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view()(request, messages(application), config).toString
        }
      }
    }

    "when calling .notOnPrivateBeta" - {

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder().build()

        running(application) {
          val request = FakeRequest(GET, routes.ErrorController.notOnPrivateBeta().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[NotOnPrivateBetaView]
          val config = application.injector.instanceOf[AppConfig]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view()(request, messages(application), config).toString
        }
      }
    }

    "when calling .wrongArc" - {

      "must return OK and the correct view for a GET" in {

        val application = applicationBuilder().build()

        running(application) {
          val request = FakeRequest(GET, routes.ErrorController.wrongArc().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[UnauthorisedView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view()(request, messages(application)).toString
        }
      }
    }
  }
}
