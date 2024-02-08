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
import forms.sections.destination.DestinationWarehouseVatFormProvider
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario._
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.destination.{DestinationDetailsChoicePage, DestinationWarehouseVatPage}
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.destination.DestinationWarehouseVatView

import scala.concurrent.Future

class DestinationWarehouseVatControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: DestinationWarehouseVatFormProvider = new DestinationWarehouseVatFormProvider()
  lazy val form: Form[String] = formProvider()
  lazy val view: DestinationWarehouseVatView = app.injector.instanceOf[DestinationWarehouseVatView]

  lazy val destinationWarehouseVatRoute: String =
    controllers.sections.destination.routes.DestinationWarehouseVatController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val destinationWarehouseVatOnSubmit: Call =
    controllers.sections.destination.routes.DestinationWarehouseVatController.onSubmit(testErn, testArc, NormalMode)
  lazy val destinationWarehouseSkipQuestion: Call =
    controllers.sections.destination.routes.DestinationWarehouseVatController.skipThisQuestion(testErn, testArc, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val testController = new DestinationWarehouseVatController(
      messagesApi,
      mockUserAnswersService,
      new FakeDestinationNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      fakeBetaAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )

    val request = FakeRequest(GET, destinationWarehouseVatRoute)

  }

  "DestinationWarehouseVat Controller" - {
    "for a GET onPageLoad" - {
      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers
        .set(DestinationTypePage, RegisteredConsignee)
      )) {
        val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form,
          action = destinationWarehouseVatOnSubmit,
          movementScenario = RegisteredConsignee,
          skipQuestionCall = destinationWarehouseSkipQuestion
        )(dataRequest(request), messages(request)).toString
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(emptyUserAnswers
        .set(DestinationWarehouseVatPage, "answer")
        .set(DestinationTypePage, RegisteredConsignee)
      )) {
        val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

        val expectedView = view(
          form = form.fill("answer"),
          action = destinationWarehouseVatOnSubmit,
          movementScenario = RegisteredConsignee,
          skipQuestionCall = destinationWarehouseSkipQuestion
        )(dataRequest(request), messages(request)).toString

        status(result) mustEqual OK
        contentAsString(result) mustEqual expectedView
      }

      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {
        val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "for a POST onSubmit" - {
      "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers
        .set(DestinationTypePage, RegisteredConsignee)
      )) {
        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val req = FakeRequest(POST, destinationWarehouseVatRoute).withFormUrlEncodedBody(("value", "answer"))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers
        .set(DestinationTypePage, RegisteredConsignee)
      )) {
        val req = FakeRequest(POST, destinationWarehouseVatRoute).withFormUrlEncodedBody(("value", "12345678901234567890"))

        val boundForm = form.bind(Map("value" -> "12345678901234567890"))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, destinationWarehouseVatOnSubmit,
          RegisteredConsignee, destinationWarehouseSkipQuestion
        )(dataRequest(request), messages(request)).toString
      }


      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {
        val req = FakeRequest(POST, destinationWarehouseVatRoute).withFormUrlEncodedBody(("value", "answer"))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
    "for a GET skipThisQuestion" - {

      "must redirect to the next page called and not cleanse any answers when no DetinationWarehouseVat" in new Fixture(Some(emptyUserAnswers
        .set(DestinationTypePage, RegisteredConsignee)
        .set(DestinationDetailsChoicePage, true)
      )) {

        val expectedAnswers = emptyUserAnswers
          .set(DestinationTypePage, RegisteredConsignee)
          .set(DestinationDetailsChoicePage, true)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))
        val req = FakeRequest(GET, destinationWarehouseSkipQuestion.url)

        val result = testController.skipThisQuestion(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page and cleanse ONLY the DesinationWarehouseVatPage answer" in new Fixture(Some(emptyUserAnswers
        .set(DestinationTypePage, RegisteredConsignee)
        .set(DestinationWarehouseVatPage, "vat")
        .set(DestinationDetailsChoicePage, true)
      )) {

        val expectedAnswers = emptyUserAnswers
          .set(DestinationTypePage, RegisteredConsignee)
          .set(DestinationDetailsChoicePage, true)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        val req = FakeRequest(GET, destinationWarehouseSkipQuestion.url)

        val result = testController.skipThisQuestion(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(GET, destinationWarehouseSkipQuestion.url)

      val result = testController.skipThisQuestion(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }

}

