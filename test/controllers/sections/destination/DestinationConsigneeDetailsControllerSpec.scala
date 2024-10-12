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

package controllers.sections.destination

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import fixtures.UserAddressFixtures
import forms.sections.destination.DestinationConsigneeDetailsFormProvider
import mocks.services.MockUserAnswersService
import models.{CheckMode, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.destination._
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.destination.DestinationConsigneeDetailsView

import scala.concurrent.Future

class DestinationConsigneeDetailsControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures {

  lazy val formProvider: DestinationConsigneeDetailsFormProvider = new DestinationConsigneeDetailsFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: DestinationConsigneeDetailsView = app.injector.instanceOf[DestinationConsigneeDetailsView]

  lazy val destinationConsigneeDetailsRoute: String =
    routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val destinationConsigneeDetailsRouteCheckMode: String =
    routes.DestinationConsigneeDetailsController.onPageLoad(testErn, testArc, CheckMode).url

  class Test(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val testController = new DestinationConsigneeDetailsController(
      messagesApi,
      mockUserAnswersService,
      new FakeDestinationNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      formProvider,
      messagesControllerComponents,
      view
    )

    val request = FakeRequest(GET, destinationConsigneeDetailsRoute)

  }


  "DestinationConsigneeDetails Controller" - {

    "must return OK and the correct view for a GET" in new Test() {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(emptyUserAnswers
      .set(DestinationConsigneeDetailsPage, true)
    )) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
      val req = FakeRequest(POST, destinationConsigneeDetailsRoute).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test() {
      val req = FakeRequest(POST, destinationConsigneeDetailsRoute).withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val req = FakeRequest(POST, destinationConsigneeDetailsRoute).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    s"must redirect without changing any answers when this page is submitted with the same answer it already has" - {
      "when for a user with WarehouseExcise answered" in new Test(Some(emptyUserAnswers
        .set(DestinationWarehouseExcisePage, "excise")
        .set(DestinationConsigneeDetailsPage, false)
        .set(DestinationAddressPage, userAddressModelMax.copy(businessName = Some("business name")))
      )) {

        val req = FakeRequest(POST, destinationConsigneeDetailsRouteCheckMode).withFormUrlEncodedBody(("value", "false"))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
      }

      "when for a user with WarehouseVat answered" in new Test(Some(emptyUserAnswers
        .set(DestinationWarehouseVatPage, "vat")
        .set(DestinationDetailsChoicePage, true)
        .set(DestinationConsigneeDetailsPage, true)
        .set(DestinationAddressPage, userAddressModelMax.copy(businessName = Some("business name")))
      )) {
        val req = FakeRequest(POST, destinationConsigneeDetailsRouteCheckMode).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
      }
    }

    s"must cleanse further Destination section answers answering Yes to using consignee details" - {

      "when for a user with WarehouseExcise answered" in new Test(Some(emptyUserAnswers
        .set(DestinationWarehouseExcisePage, "excise")
        .set(DestinationConsigneeDetailsPage, false)
        .set(DestinationAddressPage, userAddressModelMax.copy(businessName = Some("business name")))
      )) {

        val expectedAnswers = emptyUserAnswers
          .set(DestinationWarehouseExcisePage, "excise")
          .set(DestinationConsigneeDetailsPage, true)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        val req = FakeRequest(POST, destinationConsigneeDetailsRouteCheckMode).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
      }

      "when for a user with WarehouseVat answered" in new Test(Some(emptyUserAnswers
        .set(DestinationWarehouseVatPage, "vat")
        .set(DestinationDetailsChoicePage, true)
        .set(DestinationConsigneeDetailsPage, false)
        .set(DestinationAddressPage, userAddressModelMax.copy(businessName = Some("business name")))
      )) {

        val expectedAnswers = emptyUserAnswers
          .set(DestinationWarehouseVatPage, "vat")
          .set(DestinationDetailsChoicePage, true)
          .set(DestinationConsigneeDetailsPage, true)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        val req = FakeRequest(POST, destinationConsigneeDetailsRouteCheckMode).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
      }
    }
  }
}
