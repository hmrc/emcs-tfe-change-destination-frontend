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

package controllers.sections.transportArranger

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.actions.FakeMovementAction
import controllers.routes
import fixtures.UserAddressFixtures
import forms.AddressFormProvider
import mocks.services.MockUserAnswersService
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import models.{NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeTransportArrangerNavigator
import pages.sections.transportArranger.{TransportArrangerAddressPage, TransportArrangerPage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.AddressView

import scala.concurrent.Future

class TransportArrangerAddressControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {

  lazy val formProvider: AddressFormProvider = new AddressFormProvider()
  lazy val form: Form[UserAddress] = formProvider()
  lazy val view: AddressView = app.injector.instanceOf[AddressView]

  lazy val transportArrangerAddressOnSubmit: Call =
    controllers.sections.transportArranger.routes.TransportArrangerAddressController.onSubmit(testErn, testArc, NormalMode)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportArrangerAddressController(
      messagesApi,
      mockUserAnswersService,
      new FakeTransportArrangerNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      fakeUserAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "TransportArrangerAddress Controller" - {

    "when TransportArranger is GoodsOwner" - {

      "must return OK and the correct view for a GET" in new Fixture(
        Some(emptyUserAnswers.set(TransportArrangerPage, GoodsOwner))
      ) {
        val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          addressPage = TransportArrangerAddressPage,
          call = transportArrangerAddressOnSubmit,
          headingKey = Some(s"$TransportArrangerAddressPage.$GoodsOwner")
        )(dataRequest(request, userAnswers.get), messages(request)).toString
      }
    }

    "when TransportArranger is Other and already answered" - {

      "must return OK and the correct view for a GET" in new Fixture(Some(
        emptyUserAnswers
          .set(TransportArrangerPage, Other)
          .set(TransportArrangerAddressPage, userAddressModelMax)
      )) {
        val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(userAddressModelMax),
          addressPage = TransportArrangerAddressPage,
          call = transportArrangerAddressOnSubmit,
          headingKey = Some(s"$TransportArrangerAddressPage.$Other")
        )(dataRequest(request, userAnswers.get), messages(request)).toString
      }

      "must redirect to the next page when valid data is submitted" in new Fixture() {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(
          ("property", userAddressModelMax.property.value),
          ("street", userAddressModelMax.street),
          ("town", userAddressModelMax.town),
          ("postcode", userAddressModelMax.postcode)
        ))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(
        Some(emptyUserAnswers.set(TransportArrangerPage, GoodsOwner))
      ) {

        val boundForm = form.bind(Map("value" -> ""))
        val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          addressPage = TransportArrangerAddressPage,
          call = transportArrangerAddressOnSubmit,
          headingKey = Some(s"$TransportArrangerAddressPage.$GoodsOwner")
        )(dataRequest(request, userAnswers.get), messages(request)).toString
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
        val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
        val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
