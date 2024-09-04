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

package controllers.sections.transportArranger

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.transportArranger.TransportArrangerReviewFormProvider
import mocks.services.MockUserAnswersService
import models.UserAnswers
import models.requests.DataRequest
import models.sections.ReviewAnswer
import navigation.FakeNavigators.FakeTransportArrangerNavigator
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import viewmodels.checkAnswers.sections.transportArranger.TransportArrangerCheckAnswersHelper
import views.html.sections.transportArranger.TransportArrangerReviewView

import scala.concurrent.Future

class TransportArrangerReviewControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider = new TransportArrangerReviewFormProvider()
  lazy val form: Form[ReviewAnswer] = formProvider()
  lazy val view: TransportArrangerReviewView = app.injector.instanceOf[TransportArrangerReviewView]
  lazy val helper: TransportArrangerCheckAnswersHelper = app.injector.instanceOf[TransportArrangerCheckAnswersHelper]
  val submitCall: Call = controllers.sections.transportArranger.routes.TransportArrangerReviewController.onSubmit(testErn, testArc)

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
    implicit lazy val dr: DataRequest[AnyContentAsEmpty.type] = dataRequest(request, userAnswers.get)
    implicit lazy val msgs: Messages = messages(dr)

    lazy val controller = new TransportArrangerReviewController(
      messagesApi,
      mockUserAnswersService,
      new FakeTransportArrangerNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      helper
    )
  }

  "TransportArrangerReview Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, helper.summaryList(onReviewPage = true), submitCall).toString()
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(emptyUserAnswers)) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "yes")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, helper.summaryList(onReviewPage = true), submitCall).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
