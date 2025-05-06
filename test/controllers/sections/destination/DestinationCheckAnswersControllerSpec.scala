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

package controllers.sections.destination

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.routes
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockDestinationCheckAnswersHelper
import models.UserAnswers
import models.sections.info.movementScenario.MovementScenario
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.info.DestinationTypePage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.summarylist._
import views.html.sections.destination.DestinationCheckAnswersView


class DestinationCheckAnswersControllerSpec extends SpecBase with MockUserAnswersService with MockDestinationCheckAnswersHelper {

  lazy val destinationCheckAnswersRoute: String =
    controllers.sections.destination.routes.DestinationCheckAnswersController.onPageLoad(testErn, testArc).url
  lazy val destinationCheckAnswersOnSubmit: Call =
    controllers.sections.destination.routes.DestinationCheckAnswersController.onSubmit(testErn, testArc)

  val list: SummaryList = SummaryListViewModel(Seq.empty).withCssClass("govuk-!-margin-bottom-9")

  lazy val view: DestinationCheckAnswersView = app.injector.instanceOf[DestinationCheckAnswersView]

  class Test(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val request = FakeRequest(GET, destinationCheckAnswersRoute)

    lazy val testController = new DestinationCheckAnswersController(
      messagesApi,
      mockUserAnswersService,
      new FakeDestinationNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      mockDestinationCheckAnswersHelper,
      messagesControllerComponents,
      view
    )

  }

  "DestinationCheckAnswers Controller" - {
    "must return OK and the correct view for a GET when destination type has been answered" in new Test(Some(emptyUserAnswers
      .set(DestinationTypePage, MovementScenario.DirectDelivery)
    )) {
      MockCheckAnswersJourneyTypeHelper.summaryList().returns(list)
      val result = testController.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        list = list,
        onSubmitCall = destinationCheckAnswersOnSubmit
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when submitted" in new Test() {
      val req =
        FakeRequest(POST, destinationCheckAnswersRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testArc)(req)


      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = testController.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val req =
        FakeRequest(POST, destinationCheckAnswersRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testArc)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
