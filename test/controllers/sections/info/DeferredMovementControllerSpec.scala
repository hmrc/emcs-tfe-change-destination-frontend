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

package controllers.sections.info

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.actions.predraft.FakePreDraftRetrievalAction
import forms.sections.info.DeferredMovementFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeInfoNavigator
import pages.sections.info.DeferredMovementPage
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.info.DeferredMovementView

import scala.concurrent.Future

class DeferredMovementControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService {

  lazy val formProvider: DeferredMovementFormProvider = new DeferredMovementFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: DeferredMovementView = app.injector.instanceOf[DeferredMovementView]

  class Fixture(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new DeferredMovementController(
      messagesApi,
      mockPreDraftService,
      new FakeInfoNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakePreDraftRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      preDraftDataRequiredAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      mockUserAnswersService,
      Helpers.stubMessagesControllerComponents(),
      view,
      fakeUserAllowListAction
    )
  }

  lazy val deferredMovementPreDraftSubmitRoute = controllers.sections.info.routes.DeferredMovementController.onPreDraftSubmit(testErn, NormalMode)
  lazy val deferredMovementSubmitRoute = controllers.sections.info.routes.DeferredMovementController.onSubmit(testErn, testDraftId)

  "DeferredMovement Controller" - {

    "pre-draft" - {

      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {
        val result = controller.onPreDraftPageLoad(testErn, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, deferredMovementPreDraftSubmitRoute)(dataRequest(request), messages(request)).toString
      }

      "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
        MockPreDraftService.set(emptyUserAnswers.set(DeferredMovementPage(), true)).returns(Future.successful(true))

        val result = controller.onPreDraftSubmit(testErn, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
        val boundForm = form.bind(Map("value" -> ""))

        val result = controller.onPreDraftSubmit(testErn, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, deferredMovementPreDraftSubmitRoute)(dataRequest(request), messages(request)).toString
      }
    }

    "post-draft" - {

      "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {
        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, deferredMovementSubmitRoute)(dataRequest(request), messages(request)).toString
      }

      "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
        MockUserAnswersService.set(emptyUserAnswers.set(DeferredMovementPage(), true)).returns(Future.successful(emptyUserAnswers))

        val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", "true")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
        val boundForm = form.bind(Map("value" -> ""))

        val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, deferredMovementSubmitRoute)(dataRequest(request), messages(request)).toString
      }
    }
  }
}
