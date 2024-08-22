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

package controllers.sections.consignee

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.routes
import forms.sections.consignee.ConsigneeExciseFormProvider
import mocks.services.MockUserAnswersService
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{ReturnToThePlaceOfDispatch, TemporaryRegisteredConsignee}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.consignee.ConsigneeExciseView

import scala.concurrent.Future

class ConsigneeExciseControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: ConsigneeExciseFormProvider = new ConsigneeExciseFormProvider()
  lazy val view: ConsigneeExciseView = app.injector.instanceOf[ConsigneeExciseView]

  lazy val consigneeExciseRoute: String =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val consigneeExciseSubmit: Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onSubmit(testErn, testArc, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, consigneeExciseRoute)

    implicit val dr: DataRequest[_] = dataRequest(request, optUserAnswers.getOrElse(emptyUserAnswers))

    lazy val form: Form[String] = formProvider(isNorthernIrishTemporaryRegisteredConsignee = true)

    lazy val testController = new ConsigneeExciseController(
      messagesApi,
      fakeAuthAction,
      fakeBetaAllowListAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(consigneeTrader = None)),
      new FakeConsigneeNavigator(testOnwardRoute),
      mockUserAnswersService,
      formProvider,
      messagesControllerComponents,
      view
    )

  }

  val userAnswersWithConsigneeExcise: UserAnswers = emptyUserAnswers.set(ConsigneeExcisePage, testErn).set(DestinationTypePage, ReturnToThePlaceOfDispatch)
  val userAnswersWithDestinationType: UserAnswers = emptyUserAnswers.set(DestinationTypePage, TemporaryRegisteredConsignee)

  "ConsigneeExciseController Controller" - {
    "must return OK and the correct view for a GET" - {
      "when Destination type is TemporaryRegisteredConsignee and Northern Irish" in new Fixture(Some(userAnswersWithDestinationType)) {
        val result: Future[Result] = testController.onPageLoad(testErn, testArc, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, consigneeExciseSubmit, isNorthernIrishTemporaryRegisteredConsignee = true)(dataRequest(request), messages(request)).toString
      }

      "when Destination type is NOT TemporaryRegisteredConsignee and Northern Irish" in new Fixture() {
        val result: Future[Result] = testController.onPageLoad(testErn, testArc, NormalMode)(request)

        override lazy val form: Form[String] = formProvider(isNorthernIrishTemporaryRegisteredConsignee = false)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, consigneeExciseSubmit, isNorthernIrishTemporaryRegisteredConsignee = false)(dataRequest(request), messages(request)).toString
      }


      "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(userAnswersWithConsigneeExcise)) {
        val result: Future[Result] = testController.onPageLoad(testErn, testArc, NormalMode)(request)

        override lazy val form: Form[String] = formProvider(isNorthernIrishTemporaryRegisteredConsignee = false)


        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(testErn), consigneeExciseSubmit, isNorthernIrishTemporaryRegisteredConsignee = false)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(userAnswersWithConsigneeExcise)) {

      val req: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, consigneeExciseSubmit.url).withFormUrlEncodedBody(("value", testErn))

      val result: Future[Result] = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, consigneeExciseSubmit.url).withFormUrlEncodedBody(("value", ""))

      override lazy val form: Form[String] = formProvider(isNorthernIrishTemporaryRegisteredConsignee = false)

      val boundForm: Form[String] = form.bind(Map("value" -> ""))

      val result: Future[Result] = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, consigneeExciseSubmit, isNorthernIrishTemporaryRegisteredConsignee = false)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result: Future[Result] = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, consigneeExciseSubmit.url).withFormUrlEncodedBody(("value", "answer"))

      val result: Future[Result] = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }

}
