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

package controllers.sections.movement

import base.SpecBase
import controllers.actions.predraft.FakePreDraftRetrievalAction
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.movement.MovementReviewAnswersFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import mocks.viewmodels.MockMovementReviewAnswersHelper
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.{FakeInfoNavigator, FakeMovementNavigator}
import pages.sections.movement.MovementReviewAnswersPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.all.FluentSummaryList
import viewmodels.govuk.summarylist._
import views.html.sections.movement.MovementReviewAnswersView

import scala.concurrent.Future

class MovementReviewAnswersControllerSpec extends SpecBase with MockUserAnswersService with MockMovementReviewAnswersHelper with MockPreDraftService {

  lazy val formProvider = new MovementReviewAnswersFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[MovementReviewAnswersView]

  val list: SummaryList = SummaryListViewModel(rows = Seq.empty)
    .withCssClass("govuk-!-margin-bottom-9")

  val onSubmitCall = controllers.sections.movement.routes.MovementReviewAnswersController.onSubmit(testErn, testArc, NormalMode)

  class Test(val userAnswers: Option[UserAnswers]) {

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new MovementReviewAnswersController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      userAllowList = fakeUserAllowListAction,
      navigator = new FakeMovementNavigator(testOnwardRoute),
      auth = fakeAuthAction,
      getData = new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requireData = dataRequiredAction,
      withMovement = new FakeMovementAction(maxGetMovementResponse.copy(eadEsad = maxGetMovementResponse.eadEsad.copy(invoiceDate = Some("2023-01-01")))),
      formProvider = formProvider,
      controllerComponents = Helpers.stubMessagesControllerComponents(),
      view = view,
      movementReviewAnswersHelper = mockMovementReviewAnswersHelper
    )
  }

  "MovementReviewAnswers Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {

      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      MockMovementReviewAnswersHelper.summaryList().returns(list)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, list, onSubmitCall)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(MovementReviewAnswersPage, true)
    )) {

      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      MockMovementReviewAnswersHelper.summaryList().returns(list)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), list, onSubmitCall)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(emptyUserAnswers)) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {

      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      MockMovementReviewAnswersHelper.summaryList().returns(list)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, list, onSubmitCall)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
