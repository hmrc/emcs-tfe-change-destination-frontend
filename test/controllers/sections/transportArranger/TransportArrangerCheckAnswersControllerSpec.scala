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

package controllers.sections.transportArranger

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.actions.FakeMovementAction
import controllers.routes
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockTransportArrangerCheckAnswersHelper
import models.UserAnswers
import navigation.FakeNavigators.FakeTransportArrangerNavigator
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.SummaryListFluency
import views.html.sections.transportArranger.TransportArrangerCheckAnswersView

class TransportArrangerCheckAnswersControllerSpec extends SpecBase with SummaryListFluency
  with MockTransportArrangerCheckAnswersHelper with MockUserAnswersService {

  lazy val view: TransportArrangerCheckAnswersView = app.injector.instanceOf[TransportArrangerCheckAnswersView]

  lazy val list: SummaryList = SummaryListViewModel(Seq.empty).withCssClass("govuk-!-margin-bottom-9")

  class Fixtures(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportArrangerCheckAnswersController(
      messagesApi,
      mockUserAnswersService,
      new FakeTransportArrangerNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      fakeUserAllowListAction,
      MockTransportArrangerCheckAnswersHelper,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "TransportArrangerCheckAnswers Controller" - {

    "must return OK and the correct view for a GET" in new Fixtures(Some(emptyUserAnswers)) {

      MockCheckAnswersJourneyTypeHelper.summaryList().returns(list)

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        list = list,
        submitAction = controllers.sections.transportArranger.routes.TransportArrangerCheckAnswersController.onSubmit(testErn, testArc)
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixtures(Some(emptyUserAnswers)) {
      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixtures(None) {

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixtures(None) {
      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
