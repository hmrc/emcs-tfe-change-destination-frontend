/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.sections.consignee.ConsigneeExportEoriFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.ConsigneeExportEoriPage
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.consignee.ConsigneeExportEoriView

import scala.concurrent.Future

class ConsigneeExportEoriControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider = new ConsigneeExportEoriFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[ConsigneeExportEoriView]

  lazy val consigneeExportEoriRoute: String =
    controllers.sections.consignee.routes.ConsigneeExportEoriController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val consigneeExportEoriRouteSubmit: Call =
    controllers.sections.consignee.routes.ConsigneeExportEoriController.onSubmit(testErn, testArc, NormalMode)

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, consigneeExportEoriRoute)

    lazy val controller = new ConsigneeExportEoriController(
      messagesApi,
      mockUserAnswersService,
      new FakeConsigneeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(consigneeTrader = None)),
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ConsigneeExportEori Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, consigneeExportEoriRouteSubmit)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(ConsigneeExportEoriPage, "answer")
    )) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), consigneeExportEoriRouteSubmit)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(emptyUserAnswers)) {

      val expectedSavedAnswers = emptyUserAnswers.set(ConsigneeExportEoriPage, testEoriNumber)

      MockUserAnswersService.set(expectedSavedAnswers).returns(Future.successful(expectedSavedAnswers))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", testEoriNumber)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, consigneeExportEoriRouteSubmit)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
