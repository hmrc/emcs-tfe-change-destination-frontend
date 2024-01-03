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

package controllers.sections.guarantor

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.routes
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockGuarantorCheckAnswersHelper
import models.UserAnswers
import navigation.FakeNavigators.FakeGuarantorNavigator
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.SummaryListFluency
import views.html.sections.guarantor.GuarantorCheckAnswersView

class GuarantorCheckAnswersControllerSpec extends SpecBase with SummaryListFluency
  with MockGuarantorCheckAnswersHelper with MockUserAnswersService {

  lazy val checkYourAnswersRoute: String = controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc).url

  lazy val view: GuarantorCheckAnswersView = app.injector.instanceOf[GuarantorCheckAnswersView]

  val list: SummaryList = SummaryListViewModel(Seq.empty).withCssClass("govuk-!-margin-bottom-9")

  class Fixtures(optUserAnswers: Option[UserAnswers]) {
    implicit val request = dataRequest(FakeRequest(GET, checkYourAnswersRoute))

    lazy val testController = new GuarantorCheckAnswersController(
      messagesApi,
      mockUserAnswersService,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      fakeUserAllowListAction,
      mockGuarantorCheckAnswersHelper,
      messagesControllerComponents,
      view
    )

  }

  "GuarantorCheckAnswers Controller" - {
    "must return OK and the correct view for a GET" in new Fixtures(Some(emptyUserAnswers)) {
      MockGuarantorCheckAnswersHelper.summaryList().returns(list)

      val result = testController.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        list = list,
        submitAction = controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onSubmit(testErn, testArc)
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixtures(Some(emptyUserAnswers)) {
      val req = FakeRequest(POST, checkYourAnswersRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixtures(None) {
      val req = FakeRequest(GET, checkYourAnswersRoute)

      val result = testController.onPageLoad(testErn, testArc)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixtures(None) {
      val req = FakeRequest(POST, checkYourAnswersRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
