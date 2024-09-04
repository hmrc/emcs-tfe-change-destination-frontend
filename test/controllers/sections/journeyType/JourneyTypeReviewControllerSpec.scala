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

package controllers.sections.journeyType

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.journeyType.JourneyTypeReviewFormProvider
import mocks.services.MockUserAnswersService
import models.UserAnswers
import models.sections.ReviewAnswer
import navigation.FakeNavigators.FakeJourneyTypeNavigator
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import viewmodels.helpers.CheckYourAnswersJourneyTypeHelper
import views.html.sections.journeyType.JourneyTypeReviewView

import scala.concurrent.Future

class JourneyTypeReviewControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider = new JourneyTypeReviewFormProvider()
  lazy val form: Form[ReviewAnswer] = formProvider()
  lazy val view: JourneyTypeReviewView = app.injector.instanceOf[JourneyTypeReviewView]
  lazy val helper: CheckYourAnswersJourneyTypeHelper = app.injector.instanceOf[CheckYourAnswersJourneyTypeHelper]
  val submitCall: Call = controllers.sections.journeyType.routes.JourneyTypeReviewController.onSubmit(testErn, testArc)

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new JourneyTypeReviewController(
      messagesApi,
      mockUserAnswersService,
      new FakeJourneyTypeNavigator(testOnwardRoute),
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

  "JourneyTypeReview Controller" - {

    "must return OK and the correct view for a GET" in new Test {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, helper.summaryList(onReviewPage = true)(dataRequest(request), messages(request)), submitCall)(dataRequest(request), messages(request)).toString()
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
      contentAsString(result) mustEqual view(boundForm, helper.summaryList(onReviewPage = true)(dataRequest(request), messages(request)), submitCall)(dataRequest(request), messages(request)).toString
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
