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
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.auth.errors._

class ErrorControllerSpec extends SpecBase {

  lazy val notAnOrganisationView: NotAnOrganisationView = app.injector.instanceOf[NotAnOrganisationView]
  lazy val inactiveEnrolmentView: InactiveEnrolmentView = app.injector.instanceOf[InactiveEnrolmentView]
  lazy val noEnrolmentView: NoEnrolmentView = app.injector.instanceOf[NoEnrolmentView]
  lazy val notOnPrivateBetaView: NotOnPrivateBetaView = app.injector.instanceOf[NotOnPrivateBetaView]
  lazy val unauthorisedView: UnauthorisedView = app.injector.instanceOf[UnauthorisedView]

  val controller = new ErrorController(
    messagesControllerComponents,
    unauthorisedView,
    notAnOrganisationView,
    noEnrolmentView,
    inactiveEnrolmentView,
    notOnPrivateBetaView
  )(appConfig)

  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "Unauthorised Controller" - {

    "when calling .unauthorised" - {

      "must return OK and the correct view for a GET" in {

        val result = controller.unauthorised()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual unauthorisedView()(request, messages(request)).toString
      }
    }

    "when calling .notAnOrganisation" - {

      "must return OK and the correct view for a GET" in {

        val result = controller.notAnOrganisation()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual notAnOrganisationView()(request, messages(request), appConfig).toString
      }
    }

    "when calling .inactiveEnrolment" - {

      "must return OK and the correct view for a GET" in {

        val result = controller.inactiveEnrolment()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual inactiveEnrolmentView()(request, messages(request), appConfig).toString
      }
    }

    "when calling .noEnrolment" - {

      "must return OK and the correct view for a GET" in {

        val result = controller.noEnrolment()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual noEnrolmentView()(request, messages(request), appConfig).toString
      }
    }

    "when calling .notOnPrivateBeta" - {

      "must return OK and the correct view for a GET" in {

        val result = controller.notOnPrivateBeta()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual notOnPrivateBetaView()(request, messages(request), appConfig).toString
      }
    }

    "when calling .wrongArc" - {

      "must return OK and the correct view for a GET" in {

        val result = controller.wrongArc()(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual unauthorisedView()(request, messages(request)).toString
      }
    }
  }
}
