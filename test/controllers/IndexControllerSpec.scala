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

package controllers

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.ContinueDraftFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.UserAnswers
import models.sections.info.ChangeType.ExportOffice
import pages.DeclarationPage
import pages.sections.info.ChangeTypePage
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ContinueDraftView

import java.time.LocalDateTime
import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockPreDraftService with MockUserAnswersService {

  lazy val formProvider: ContinueDraftFormProvider = app.injector.instanceOf[ContinueDraftFormProvider]

  lazy val form: Form[Boolean] = formProvider()

  lazy val view: ContinueDraftView = app.injector.instanceOf[ContinueDraftView]

  lazy val submitCall: Call = controllers.routes.IndexController.onSubmit(testErn, testArc)

  class Test(userAnswers: Option[UserAnswers]) {

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new IndexController(
      messagesApi,
      mockPreDraftService,
      mockUserAnswersService,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      new FakeMovementAction(maxGetMovementResponse),
      fakeAuthAction,
      fakeUserAllowListAction,
      messagesControllerComponents,
      formProvider,
      view
    )
  }

  "Index Controller" - {

    ".onPageLoad" - {

      "must reinitialise the draft" - {

        "when the user has previously submitted a change of destination" in new Test(Some(
          emptyUserAnswers.set(DeclarationPage, LocalDateTime.now())
        )) {

          MockPreDraftService.set(emptyUserAnswers).returns(Future.successful(true))
          MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))

          val result = controller.onPageLoad(testErn, testArc)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustBe controllers.sections.info.routes.InfoIndexController.onPageLoad(testErn, testArc).url
        }

        "the users answers are empty" in new Test(Some(emptyUserAnswers)) {

          MockPreDraftService.set(emptyUserAnswers).returns(Future.successful(true))
          MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))

          val result = controller.onPageLoad(testErn, testArc)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustBe controllers.sections.info.routes.InfoIndexController.onPageLoad(testErn, testArc).url
        }
      }

      "must show the continue draft page" - {

        "when the user answers are not empty" in new Test(Some(
          emptyUserAnswers.set(ChangeTypePage, ExportOffice)
        )) {

          val result = controller.onPageLoad(testErn, testArc)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, submitCall)(optionalDataRequest(request), messages(request)).toString
        }
      }
    }

    ".onSubmit" - {

      "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
        val boundForm = form.bind(Map("value" -> ""))

        val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, submitCall)(optionalDataRequest(request), messages(request)).toString
      }

      "must redirect to the Task List page when the user clicks 'Continue draft'" in new Test(Some(emptyUserAnswers)) {

        val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "true")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.TaskListController.onPageLoad(testErn, testArc).url
      }

      "must redirect to the Info index and reinitialise the user answers when the user clicks 'Start a new CoD'" in new Test(Some(emptyUserAnswers)) {

        MockPreDraftService.set(emptyUserAnswers).returns(Future.successful(true))
        MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))

        val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "false")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.info.routes.InfoIndexController.onPageLoad(testErn, testArc).url
      }
    }
  }
}
