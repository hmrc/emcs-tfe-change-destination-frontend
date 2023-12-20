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

package controllers.sections.consignor

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import models.UserAnswers
import navigation.FakeNavigators.FakeConsignorNavigator
import pages.sections.consignor.ConsignorAddressPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.consignor.CheckYourAnswersConsignorView

class CheckYourAnswersConsignorControllerSpec extends SpecBase {

  lazy val view: CheckYourAnswersConsignorView = app.injector.instanceOf[CheckYourAnswersConsignorView]

  lazy val route: String =
    controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onPageLoad(testErn, testDraftId).url
  lazy val submitRoute: String =
    controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onSubmit(testErn, testDraftId).url

  class Fixture(optUserAnswers: Option[UserAnswers]) {

    lazy val testController = new CheckYourAnswersConsignorController(
      messagesApi,
      fakeAuthAction,
      fakeUserAllowListAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      messagesControllerComponents,
      new FakeConsignorNavigator(testOnwardRoute),
      view
    )

    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, route)
  }

  "Check Your Answers Consignor Controller" - {
    ".onPageLoad" - {
      "must return OK and the correct view" in new Fixture(Some(emptyUserAnswers.set(ConsignorAddressPage, testUserAddress))) {
        val result = testController.onPageLoad(testErn, testDraftId)(request)

        lazy val viewAsString = view(controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onSubmit(testErn, testDraftId),
          testErn,
          testDraftId,
          testUserAddress,
          testMinTraderKnownFacts
        )(dataRequest(request), messages(request)).toString

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString
      }

      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {
        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
      }

      "must redirect to /consignor if user answers doesn't contain the correct page" in new Fixture(Some(emptyUserAnswers)) {
        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe controllers.sections.consignor.routes.ConsignorIndexController.onPageLoad(testErn, testDraftId).url
      }
    }
  }

  ".onSubmit" - {
    "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers)) {
      val req: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, submitRoute)

      val result = testController.onSubmit(testErn, testDraftId)(req)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe testOnwardRoute.url
    }
  }
}
