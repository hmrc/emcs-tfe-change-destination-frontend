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

package controllers.sections.firstTransporter

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.routes
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockFirstTransporterCheckAnswersHelper
import models.UserAnswers
import navigation.FakeNavigators.FakeFirstTransporterNavigator
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.SummaryListFluency
import views.html.sections.firstTransporter.FirstTransporterCheckAnswersView

class FirstTransporterCheckAnswersControllerSpec extends SpecBase with SummaryListFluency
  with MockFirstTransporterCheckAnswersHelper with MockUserAnswersService {

  lazy val checkYourAnswersJourneyTypeRoute: String =
    controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(testErn, testArc).url

  lazy val view: FirstTransporterCheckAnswersView = app.injector.instanceOf[FirstTransporterCheckAnswersView]

  val list: SummaryList = SummaryListViewModel(Seq.empty).withCssClass("govuk-!-margin-bottom-9")

  class Fixtures(optUserAnswers: Option[UserAnswers]) {

    implicit val request = dataRequest(FakeRequest(GET, checkYourAnswersJourneyTypeRoute))

    lazy val testController = new FirstTransporterCheckAnswersController(
      messagesApi,
      mockUserAnswersService,
      new FakeFirstTransporterNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      fakeUserAllowListAction,
      mockFirstTransporterCheckAnswersHelper,
      messagesControllerComponents,
      view
    )

  }

  "FirstTransporterCheckAnswers Controller" - {
    "must return OK and the correct view for a GET" in new Fixtures(Some(emptyUserAnswers)) {
      MockFirstTransporterCheckAnswersHelper.summaryList().returns(list)

      val result = testController.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        list = list,
        submitAction = controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onSubmit(testErn, testArc)
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixtures(Some(emptyUserAnswers)) {
      val req = FakeRequest(POST, checkYourAnswersJourneyTypeRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixtures(None) {
      val result = testController.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixtures(None) {
      val req = FakeRequest(POST, checkYourAnswersJourneyTypeRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
