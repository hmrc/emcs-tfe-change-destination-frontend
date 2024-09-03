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
import controllers.routes
import fixtures.UserAddressFixtures
import forms.sections.destination.DestinationDetailsChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario.RegisteredConsignee
import models.{CheckMode, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.destination._
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.JsonOptionFormatter
import views.html.sections.destination.DestinationDetailsChoiceView

import scala.concurrent.Future

class DestinationDetailsChoiceControllerSpec extends SpecBase with MockUserAnswersService with UserAddressFixtures with JsonOptionFormatter {

  lazy val formProvider: DestinationDetailsChoiceFormProvider = new DestinationDetailsChoiceFormProvider()
  lazy val form: Form[Boolean] = formProvider(RegisteredConsignee)(messages(FakeRequest()))
  lazy val view: DestinationDetailsChoiceView = app.injector.instanceOf[DestinationDetailsChoiceView]

  lazy val destinationDetailsChoiceRoute: String =
    controllers.sections.destination.routes.DestinationDetailsChoiceController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val destinationDetailsChoiceRouteCheckMode: String =
    controllers.sections.destination.routes.DestinationDetailsChoiceController.onPageLoad(testErn, testArc, CheckMode).url
  lazy val destinationDetailsChoiceSubmit: Call =
    controllers.sections.destination.routes.DestinationDetailsChoiceController.onSubmit(testErn, testArc, NormalMode)

  class Setup(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val request = FakeRequest(GET, destinationDetailsChoiceRoute)

    lazy val testController = new DestinationDetailsChoiceController(
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
  }

  "DestinationDetailsChoice Controller" - {

    "must return OK and the correct view for a GET" in new Setup(Some(emptyUserAnswers
      .set(DestinationTypePage, RegisteredConsignee)
    )) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      val expectedView = view(form, destinationDetailsChoiceSubmit, RegisteredConsignee)(dataRequest(request), messages(request)).toString

      status(result) mustEqual OK
      contentAsString(result) mustEqual expectedView
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Setup(Some(emptyUserAnswers
      .set(DestinationDetailsChoicePage, true)
      .set(DestinationTypePage, RegisteredConsignee)
    )) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      val expectedView = view(form.fill(true), destinationDetailsChoiceSubmit, RegisteredConsignee)(dataRequest(request), messages(request)).toString

      status(result) mustEqual OK
      contentAsString(result) mustEqual expectedView
    }


    "must redirect to the next page when valid data is submitted" in new Setup(Some(emptyUserAnswers
      .set(DestinationTypePage, RegisteredConsignee)
    )) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest(POST, destinationDetailsChoiceRoute).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    s"must redirect without changing answers" - {
      "when for a user with WarehouseExcise answered and the new answer is the same" in new Setup(Some(emptyUserAnswers
        .set(DestinationTypePage, RegisteredConsignee)
        .set(DestinationWarehouseVatPage, "vat")
        .set(DestinationDetailsChoicePage, true)
        .set(DestinationConsigneeDetailsPage, false)
        .set(DestinationBusinessNamePage, "business name")
        .set(DestinationAddressPage, userAddressModelMax)
      )) {

        val req = FakeRequest(POST, destinationDetailsChoiceRouteCheckMode)
          .withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    s"must cleanse further Destination section answers answering No to using giving destination details" - {
      "when for a user with WarehouseExcise answered" in new Setup(Some(emptyUserAnswers
        .set(DestinationTypePage, RegisteredConsignee)
        .set(DestinationWarehouseVatPage, "vat")
        .set(DestinationDetailsChoicePage, true)
        .set(DestinationConsigneeDetailsPage, false)
        .set(DestinationBusinessNamePage, "business name")
        .set(DestinationAddressPage, userAddressModelMax)
      )) {

        val expectedAnswers = emptyUserAnswers
          .set(DestinationTypePage, RegisteredConsignee)
          .set(DestinationWarehouseVatPage, "vat")
          .set(DestinationDetailsChoicePage, false)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        val req = FakeRequest(POST, destinationDetailsChoiceRouteCheckMode)
          .withFormUrlEncodedBody(("value", "false"))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Setup(Some(emptyUserAnswers
      .set(DestinationTypePage, RegisteredConsignee)
    )) {
      val req = FakeRequest(POST, destinationDetailsChoiceRoute).withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      val expectedView = view(boundForm, destinationDetailsChoiceSubmit, RegisteredConsignee)(dataRequest(request), messages(request)).toString

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual expectedView
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {
      val req = FakeRequest(POST, destinationDetailsChoiceRoute).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
