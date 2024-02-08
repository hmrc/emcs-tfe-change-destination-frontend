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
import controllers.actions.predraft.FakePreDraftRetrievalAction
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.info.ChangeDestinationTypeFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.UserAnswers
import models.requests.DataRequest
import navigation.FakeNavigators.FakeInfoNavigator
import pages.sections.info.ChangeDestinationTypePage
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import viewmodels.checkAnswers.sections.info.ChangeDestinationTypeHelper
import views.html.sections.info.ChangeDestinationTypeView

import scala.concurrent.Future

class ChangeDestinationTypeControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService {

  lazy val formProvider: ChangeDestinationTypeFormProvider = new ChangeDestinationTypeFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: ChangeDestinationTypeView = app.injector.instanceOf[ChangeDestinationTypeView]
  lazy val summaryListHelper: ChangeDestinationTypeHelper = app.injector.instanceOf[ChangeDestinationTypeHelper]
  lazy val submitRoute: Call = controllers.sections.info.routes.ChangeDestinationTypeController.onSubmit(testErn, testArc)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
    implicit val dr: DataRequest[_] = dataRequest(request)
    implicit val msgs: Messages = messages(request)

    lazy val controller = new ChangeDestinationTypeController(
      messagesApi,
      mockUserAnswersService,
      new FakeInfoNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeMovementAction(maxGetMovementResponse),
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeBetaAllowListAction,
      new FakePreDraftRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      preDraftDataRequiredAction,
      mockPreDraftService,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      summaryListHelper
    )
  }

  "ChangeDestinationType Controller" - {


    "must return OK and the correct view for a GET" in new Fixture {

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, summaryListHelper.summaryList(), submitRoute).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      emptyUserAnswers.set(ChangeDestinationTypePage, true)
    )) {

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), summaryListHelper.summaryList(), submitRoute).toString
    }


    "must redirect to the next page when valid data is submitted" - {

      "when the answer is true (yes) - don't save draft in UserAnswers" in new Fixture {
        val expectedAnswers = emptyUserAnswers.set(ChangeDestinationTypePage, true)

        MockPreDraftService.set(expectedAnswers).returns(Future.successful(true))

        val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "true")))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "when the answer is false (no) - save draft in UserAnswers" in new Fixture {
        val expectedAnswers = emptyUserAnswers.set(ChangeDestinationTypePage, false)

        MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

        MockPreDraftService.clear(testErn, testArc).returns(Future.successful(true))

        val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "false")))

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }


    "must return a Bad Request and errors when invalid data is submitted" in new Fixture {

      val boundForm = form.bind(Map("value" -> ""))
      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, summaryListHelper.summaryList(), submitRoute).toString
    }

    "must redirect to Index for a GET if no existing data is found" in new Fixture(None) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad(testErn, testArc).url
    }

    "must redirect to Index for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad(testErn, testArc).url
    }

  }
}
