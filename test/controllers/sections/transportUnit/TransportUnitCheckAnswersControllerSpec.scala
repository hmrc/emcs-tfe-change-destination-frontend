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

package controllers.sections.transportUnit

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockCheckYourAnswersTransportUnitsHelper
import models.UserAnswers
import navigation.FakeNavigators.FakeTransportUnitNavigator
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.summarylist._
import views.html.sections.transportUnit.TransportUnitCheckAnswersView

class TransportUnitCheckAnswersControllerSpec extends SpecBase with MockUserAnswersService with MockCheckYourAnswersTransportUnitsHelper{

  lazy val view = app.injector.instanceOf[TransportUnitCheckAnswersView]

  val list: SummaryList = SummaryListViewModel(Seq.empty)

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportUnitCheckAnswersController(
      messagesApi,
      mockUserAnswersService,
      fakeBetaAllowListAction,
      new FakeTransportUnitNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      Helpers.stubMessagesControllerComponents(),
      view,
      mockCheckYourAnswersTransportUnitsHelper
    )
  }

  "TransportUnitCheckAnswers Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {

      MockCheckYourAnswersTransportUnitsHelper.summaryList().returns(list)

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(list)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page on POST" in new Test(Some(emptyUserAnswers)) {

      val result = controller.onSubmit(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }
  }
}
