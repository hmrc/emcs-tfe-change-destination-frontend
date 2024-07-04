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
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.guarantor.GuarantorRequiredView

class GuarantorRequiredControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val view: GuarantorRequiredView = app.injector.instanceOf[GuarantorRequiredView]
  lazy val guarantorRequiredRoute: String = routes.GuarantorRequiredController.onPageLoad(testErn, testArc).url

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val request = FakeRequest(GET, guarantorRequiredRoute)
    implicit val dr = dataRequest(request)
    implicit val msgs = messages(request)

    lazy val testController = new GuarantorRequiredController(
      messagesApi,
      mockUserAnswersService,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      fakeBetaAllowListAction,
      messagesControllerComponents,
      view
    )
  }

  "must return OK and the correct view for a GET" in new Fixture() {

    val result = testController.onPageLoad(testErn, testArc)(request)

    status(result) mustEqual OK
    contentAsString(result) mustEqual view(routes.GuarantorArrangerController.onPageLoad(testErn, testArc, NormalMode)).toString
  }

  "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

    val result = testController.onPageLoad(testErn, testArc)(request)

    status(result) mustEqual SEE_OTHER
    redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
  }
}
