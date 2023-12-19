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
import controllers.routes
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockDispatchCheckAnswersHelper
import models.UserAnswers
import navigation.FakeNavigators.FakeDispatchNavigator
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.all.FluentSummaryList
import viewmodels.govuk.summarylist._
import views.html.sections.dispatch.DispatchCheckAnswersView

class DispatchCheckAnswersControllerSpec extends SpecBase with MockUserAnswersService with MockDispatchCheckAnswersHelper {

  lazy val view: DispatchCheckAnswersView = app.injector.instanceOf[DispatchCheckAnswersView]

  lazy val dispatchCheckAnswersRoute: String =
    controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(testErn, testDraftId).url

  val list: SummaryList = SummaryListViewModel(Seq.empty).withCssClass("govuk-!-margin-bottom-9")

  class Test(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val request = FakeRequest(GET, dispatchCheckAnswersRoute)

    lazy val testController = new DispatchCheckAnswersController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeDispatchNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      messagesControllerComponents,
      mockDispatchCheckAnswersHelper,
      view
    )
  }


  "DispatchCheckAnswers Controller" - {
    "must return OK and the correct view for a GET" in new Test() {
      MockDispatchCheckAnswersHelper.summaryList().returns(list)

      val result = testController.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        list = list,
        onSubmitCall = controllers.sections.dispatch.routes.DispatchCheckAnswersController.onSubmit(testErn, testDraftId)
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test() {
      val req = FakeRequest(POST, dispatchCheckAnswersRoute).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testDraftId)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = testController.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val req = FakeRequest(POST, dispatchCheckAnswersRoute).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testDraftId)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
