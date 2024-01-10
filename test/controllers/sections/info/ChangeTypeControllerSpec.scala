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

package controllers.sections.info

import base.SpecBase
import controllers.actions.FakeMovementAction
import controllers.actions.predraft.FakePreDraftRetrievalAction
import forms.sections.info.ChangeTypeFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.requests.DataRequest
import models.sections.info.ChangeType
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeNavigator
import pages.sections.info.ChangeTypePage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.info.ChangeTypeView

import scala.concurrent.Future

class ChangeTypeControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService {

  lazy val formProvider = new ChangeTypeFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[ChangeTypeView]

  lazy val submitAction = routes.ChangeTypeController.onSubmit(testErn, testArc, NormalMode)

  lazy val request = FakeRequest()
  implicit lazy val dr: DataRequest[AnyContentAsEmpty.type] = dataRequest(request)

  class Test(val userAnswers: Option[UserAnswers]) {

    lazy val controller = new ChangeTypeController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      userAllowList = fakeUserAllowListAction,
      navigator = new FakeNavigator(testOnwardRoute),
      auth = fakeAuthAction,
      preDraftService = mockPreDraftService,
      getPreDraftData = new FakePreDraftRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requirePreDraftData = preDraftDataRequiredAction,
      withMovement = new FakeMovementAction(maxGetMovementResponse),
      formProvider = formProvider,
      controllerComponents = Helpers.stubMessagesControllerComponents(),
      view = view
    )
  }

  "ChangeType Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, submitAction)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(ChangeTypePage, ChangeType.values.head)
    )) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(ChangeType.values.head), submitAction)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(emptyUserAnswers)) {

      val answersAfterSubmission = emptyUserAnswers.set(ChangeTypePage, ChangeType.values.head)

      MockPreDraftService.set(answersAfterSubmission).returns(Future.successful(true))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", ChangeType.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, submitAction)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Index for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad(testErn, testArc).url
    }

    "must redirect to Index for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", ChangeType.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad(testErn, testArc).url
    }
  }
}
