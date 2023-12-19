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
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDispatchNavigator
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.dispatch.{DispatchUseConsignorDetailsPage, DispatchWarehouseExcisePage}
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class DispatchIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, controllers.sections.dispatch.routes.DispatchIndexController.onPageLoad(testErn, testDraftId).url)

    lazy val testController = new DispatchIndexController(
      mockUserAnswersService,
      new FakeDispatchNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      messagesControllerComponents
    )
  }

  "DispatchIndexController" - {
    "when DispatchSection.isCompleted" - {
      "must redirect to the CYA controller" in new Fixture(Some(emptyUserAnswers
        .set(DispatchWarehouseExcisePage, "beans")
        .set(DispatchUseConsignorDetailsPage, true)
        .set(ConsignorAddressPage, testUserAddress)
      )) {

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.dispatch.routes.DispatchCheckAnswersController.onPageLoad(testErn, testDraftId).url)
      }
    }

    "must redirect to the dispatch warehouse excise controller" in new Fixture() {
      val result = testController.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.sections.dispatch.routes.DispatchWarehouseExciseController.onPageLoad(testErn, testDraftId, NormalMode).url)
    }
  }
}
