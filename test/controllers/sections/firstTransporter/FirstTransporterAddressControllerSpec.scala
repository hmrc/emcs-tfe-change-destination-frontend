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

package controllers.sections.firstTransporter

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.routes
import fixtures.UserAddressFixtures
import forms.AddressFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeFirstTransporterNavigator
import pages.sections.firstTransporter.FirstTransporterAddressPage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AddressView

import scala.concurrent.Future

class FirstTransporterAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {

  lazy val formProvider: AddressFormProvider = new AddressFormProvider()
  lazy val form: Form[UserAddress] = formProvider()
  lazy val view: AddressView = app.injector.instanceOf[AddressView]

  lazy val firstTransporterAddressRoute: String =
    controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val firstTransporterAddressOnSubmit: Call =
    controllers.sections.firstTransporter.routes.FirstTransporterAddressController.onSubmit(testErn, testArc, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, firstTransporterAddressRoute)

    lazy val testController = new FirstTransporterAddressController(
      messagesApi,
      mockUserAnswersService,
      new FakeFirstTransporterNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(firstTransporterTrader = None)),
      fakeBetaAllowListAction,
      new AddressFormProvider(),
      messagesControllerComponents,
      view
    )
  }

  "FirstTransporterAddress Controller" - {
    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        addressPage = FirstTransporterAddressPage,
        call = firstTransporterAddressOnSubmit,
        headingKey = Some("firstTransporterAddress")
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest(POST, firstTransporterAddressRoute).withFormUrlEncodedBody(
        ("property", userAddressModelMax.property.value),
        ("street", userAddressModelMax.street),
        ("town", userAddressModelMax.town),
        ("postcode", userAddressModelMax.postcode)
      )

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
      val req = FakeRequest(POST, firstTransporterAddressRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        addressPage = FirstTransporterAddressPage,
        call = firstTransporterAddressOnSubmit,
        headingKey = Some("firstTransporterAddress")
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, firstTransporterAddressRoute).withFormUrlEncodedBody(("value", "answer"))
      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
