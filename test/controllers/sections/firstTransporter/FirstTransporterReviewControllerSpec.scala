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

package controllers.sections.firstTransporter

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.firstTransporter.FirstTransporterReviewFormProvider
import mocks.services.MockUserAnswersService
import models.UserAnswers
import models.requests.DataRequest
import models.sections.ReviewAnswer
import models.sections.ReviewAnswer.KeepAnswers
import navigation.FakeNavigators.FakeFirstTransporterNavigator
import pages.sections.firstTransporter.FirstTransporterReviewPage
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import viewmodels.checkAnswers.sections.firstTransporter.FirstTransporterCheckAnswersHelper
import views.html.sections.firstTransporter.FirstTransporterReviewView

import scala.concurrent.Future

class FirstTransporterReviewControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider = new FirstTransporterReviewFormProvider()
  lazy val form: Form[ReviewAnswer] = formProvider()
  lazy val view: FirstTransporterReviewView = app.injector.instanceOf[FirstTransporterReviewView]
  lazy val helper: FirstTransporterCheckAnswersHelper = app.injector.instanceOf[FirstTransporterCheckAnswersHelper]
  val submitCall: Call = controllers.sections.firstTransporter.routes.FirstTransporterReviewController.onSubmit(testErn, testArc)

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
    implicit lazy val dr: DataRequest[AnyContentAsEmpty.type] = dataRequest(request, userAnswers.get)
    implicit lazy val msgs: Messages = messages(dr)

    lazy val controller = new FirstTransporterReviewController(
      messagesApi,
      mockUserAnswersService,
      fakeBetaAllowListAction,
      new FakeFirstTransporterNavigator(testOnwardRoute),
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

  "FirstTransporterReview Controller" - {

    "must return OK and the correct view for a GET" in new Test {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, helper.summaryList(onReviewPage = true), submitCall).toString()
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(FirstTransporterReviewPage, KeepAnswers)
    )) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(KeepAnswers), helper.summaryList(onReviewPage = true), submitCall).toString()
    }

    "must redirect to the next page when valid data is submitted" in new Test {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "yes")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test {
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
