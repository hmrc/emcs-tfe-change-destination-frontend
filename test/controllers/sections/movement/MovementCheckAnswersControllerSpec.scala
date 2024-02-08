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
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockMovementCheckAnswersHelper
import models.UserAnswers
import navigation.FakeNavigators.FakeMovementNavigator
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.summarylist._
import views.html.sections.movement.MovementCheckAnswersView

class MovementCheckAnswersControllerSpec extends SpecBase with MockUserAnswersService with MockMovementCheckAnswersHelper {

  lazy val view: MovementCheckAnswersView = app.injector.instanceOf[MovementCheckAnswersView]

  val list: SummaryList = SummaryListViewModel(Seq.empty)

  class Test(userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new MovementCheckAnswersController(
      messagesApi,
      mockUserAnswersService,
      fakeBetaAllowListAction,
      new FakeMovementNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      Helpers.stubMessagesControllerComponents(),
      view,
      mockMovementReviewAnswersHelper
    )
  }

  "MovementCheckAnswersController" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {

      MockMovementCheckAnswersHelper.summaryList().returns(list)

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        list = list,
        onSubmitCall = controllers.sections.movement.routes.MovementCheckAnswersController.onSubmit(testErn, testArc)
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page for a POST" in new Test(Some(emptyUserAnswers)) {

      val result = controller.onSubmit(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe testOnwardRoute.url
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
