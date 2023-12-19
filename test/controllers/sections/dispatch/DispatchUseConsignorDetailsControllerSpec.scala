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

package controllers.sections.dispatch

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.sections.dispatch.DispatchUseConsignorDetailsFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDispatchNavigator
import pages.sections.dispatch.DispatchUseConsignorDetailsPage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.dispatch.DispatchUseConsignorDetailsView

import scala.concurrent.Future

class DispatchUseConsignorDetailsControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: DispatchUseConsignorDetailsFormProvider = new DispatchUseConsignorDetailsFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: DispatchUseConsignorDetailsView = app.injector.instanceOf[DispatchUseConsignorDetailsView]

  lazy val dispatchUseConsignorDetailsRoute: String =
    routes.DispatchUseConsignorDetailsController.onPageLoad(testErn, testDraftId, NormalMode).url
  lazy val dispatchUseConsignorDetailsSubmitAction: Call =
    routes.DispatchUseConsignorDetailsController.onSubmit(testErn, testDraftId, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, dispatchUseConsignorDetailsRoute)

    lazy val testController = new DispatchUseConsignorDetailsController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeDispatchNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "DispatchUseConsignorDetails Controller" - {
    "onPageLoad" - {
      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {
        val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, dispatchUseConsignorDetailsSubmitAction)(dataRequest(request), messages(request)).toString
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
        Some(emptyUserAnswers.set(DispatchUseConsignorDetailsPage, true))
      ) {
        val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)


        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), dispatchUseConsignorDetailsSubmitAction)(dataRequest(request), messages(request)).toString
      }
    }

    "onSubmit" - {
      "must redirect to the next page when valid data is submitted - data is new" in new Fixture(Some(emptyUserAnswers)) {
        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val req = FakeRequest(POST, dispatchUseConsignorDetailsRoute).withFormUrlEncodedBody(("value", "true"))
        val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page when valid data is submitted - data has changed" in new Fixture(
        Some(emptyUserAnswers.set(DispatchUseConsignorDetailsPage, true))
      ) {
        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val req = FakeRequest(POST, dispatchUseConsignorDetailsRoute).withFormUrlEncodedBody(("value", "false"))
        val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page when valid data is submitted - data has not changed" in new Fixture(
        Some(emptyUserAnswers.set(DispatchUseConsignorDetailsPage, true))
      ) {
        val req = FakeRequest(POST, dispatchUseConsignorDetailsRoute).withFormUrlEncodedBody(("value", "true"))
        val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
        val req = FakeRequest(POST, dispatchUseConsignorDetailsRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form.bind(Map("value" -> ""))
        val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, dispatchUseConsignorDetailsSubmitAction)(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to Journey Recovery" - {
      "for a GET if no existing data is found" in new Fixture(None) {
        val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }

      "for a POST if no existing data is found" in new Fixture(None) {
        val req = FakeRequest(POST, dispatchUseConsignorDetailsRoute).withFormUrlEncodedBody(("value", "true"))
        val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
