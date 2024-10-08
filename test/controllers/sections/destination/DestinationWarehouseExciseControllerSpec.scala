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
import forms.sections.destination.DestinationWarehouseExciseFormProvider
import mocks.services.MockUserAnswersService
import models.sections.info.ChangeType
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario._
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.destination.DestinationWarehouseExcisePage
import pages.sections.info.{ChangeTypePage, DestinationTypePage}
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.destination.DestinationWarehouseExciseView

import scala.concurrent.Future

class DestinationWarehouseExciseControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: DestinationWarehouseExciseFormProvider = new DestinationWarehouseExciseFormProvider()
  lazy val form: Form[String] = formProvider(MovementScenario.UkTaxWarehouse.NI, ChangeType.Destination)(dataRequest(FakeRequest()))
  lazy val view: DestinationWarehouseExciseView = app.injector.instanceOf[DestinationWarehouseExciseView]

  lazy val destinationWarehouseExciseRoute: String =
    controllers.sections.destination.routes.DestinationWarehouseExciseController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val destinationWarehouseExciseOnSubmit: Call =
    controllers.sections.destination.routes.DestinationWarehouseExciseController.onSubmit(testErn, testArc, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val testController = new DestinationWarehouseExciseController(
      messagesApi,
      mockUserAnswersService,
      new FakeDestinationNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(deliveryPlaceTrader = None)),
      formProvider,
      messagesControllerComponents,
      view
    )

    val request = FakeRequest(GET, destinationWarehouseExciseRoute)
  }

  "DestinationWarehouseExcise Controller" - {

    "must return OK and the correct view for a GET" in new Fixture(Some(
      emptyUserAnswers
        .set(DestinationTypePage, DirectDelivery)
        .set(ChangeTypePage, ChangeType.Destination)
    )) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        onSubmitCall = controllers.sections.destination.routes.DestinationWarehouseExciseController.onSubmit(testErn, testArc, NormalMode)
      )(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      emptyUserAnswers
        .set(DestinationWarehouseExcisePage, "answer")
        .set(DestinationTypePage, DirectDelivery)
        .set(ChangeTypePage, ChangeType.Destination)
    )) {

      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"),
        onSubmitCall = controllers.sections.destination.routes.DestinationWarehouseExciseController.onSubmit(testErn, testArc, NormalMode)
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(
      emptyUserAnswers
        .set(DestinationTypePage, DirectDelivery)
        .set(ChangeTypePage, ChangeType.Destination)
    )) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest(POST, destinationWarehouseExciseRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(
      emptyUserAnswers
        .set(DestinationTypePage, DirectDelivery)
        .set(ChangeTypePage, ChangeType.Destination)
    )) {

      val req = FakeRequest(POST, destinationWarehouseExciseRoute).withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm,
        onSubmitCall = controllers.sections.destination.routes.DestinationWarehouseExciseController.onSubmit(testErn, testArc, NormalMode)
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, destinationWarehouseExciseRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
