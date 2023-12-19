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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.auth.errors._

class ErrorControllerSpec extends SpecBase {

  "Unauthorised Controller" - {
    lazy val unauthorisedView = app.injector.instanceOf[UnauthorisedView]
    lazy val notAnOrganisationView = app.injector.instanceOf[NotAnOrganisationView]
    lazy val noEnrolmentView = app.injector.instanceOf[NoEnrolmentView]
    lazy val inactiveEnrolmentView = app.injector.instanceOf[InactiveEnrolmentView]
    lazy val notOnPrivateBetaView = app.injector.instanceOf[NotOnPrivateBetaView]

    val request = FakeRequest()

    lazy val testController = new ErrorController(
      messagesControllerComponents,
      unauthorisedView,
      notAnOrganisationView,
      noEnrolmentView,
      inactiveEnrolmentView,
      notOnPrivateBetaView
    )(appConfig)

    "when calling .unauthorised" - {
      "must return OK and the correct view for a GET" in {
        val result = testController.unauthorised()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual unauthorisedView()(request, messages(request)).toString
      }
    }

    "when calling .notAnOrganisation" - {
      "must return OK and the correct view for a GET" in {
        val result = testController.notAnOrganisation()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual notAnOrganisationView()(request, messages(request), appConfig).toString
      }
    }

    "when calling .inactiveEnrolment" - {
      "must return OK and the correct view for a GET" in {
        val result = testController.inactiveEnrolment()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual inactiveEnrolmentView()(request, messages(request), appConfig).toString
      }
    }

    "when calling .noEnrolment" - {
      "must return OK and the correct view for a GET" in {
        val result = testController.noEnrolment()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual noEnrolmentView()(request, messages(request), appConfig).toString
      }
    }

    "when calling .notOnPrivateBeta" - {
      "must return OK and the correct view for a GET" in {
        val result = testController.notOnPrivateBeta()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual notOnPrivateBetaView()(request, messages(request), appConfig).toString
      }
    }

    "when calling .wrongArc" - {
      "must return OK and the correct view for a GET" in {
        val result = testController.wrongArc()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual unauthorisedView()(request, messages(request)).toString
      }
    }
  }
}
