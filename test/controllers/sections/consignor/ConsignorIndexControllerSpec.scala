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
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeConsignorNavigator
import pages.sections.consignor.ConsignorAddressPage
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ConsignorIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, controllers.sections.consignor.routes.ConsignorIndexController.onPageLoad(testErn, testDraftId).url)

    lazy val testController = new ConsignorIndexController(
      mockUserAnswersService,
      new FakeConsignorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      messagesControllerComponents
    )
  }

  "ConsignorIndexController" - {
    "when ConsignorSection.isCompleted" - {
      "must redirect to the consignor CYA controller" in new Fixture(Some(emptyUserAnswers.set(ConsignorAddressPage, UserAddress(None, "", "", "")))) {
        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onPageLoad(testErn, testDraftId).url)
      }
    }
    "must redirect to the guarantor required controller" in new Fixture() {
      val result = testController.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(testErn, testDraftId, NormalMode).url)
    }
  }

}
