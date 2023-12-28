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
import controllers.actions.FakeDataRetrievalAction
import controllers.actions.FakeMovementAction
import mocks.services.MockUserAnswersService
import models.sections.transportUnit.TransportUnitType
import models.{NormalMode, UserAnswers}
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import queries.TransportUnitsCount

class TransportUnitIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Test(userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportUnitIndexController(
      messagesApi,
      mockUserAnswersService,
      app.injector.instanceOf[TransportUnitNavigator],
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      fakeUserAllowListAction,
      Helpers.stubMessagesControllerComponents()
    )
  }

  "transportUnitIndex Controller" - {

    "must redirect to the transport unit type page (CAM-TU01) when no transport units answered" in new Test(Some(emptyUserAnswers)) {

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe
        Some(routes.TransportUnitTypeController.onPageLoad(testErn, testArc, testIndex1, NormalMode).url)
    }

    "must redirect to the transport unit type page (CAM-TU01) when there is an empty transport unit list" in new Test(Some(
      emptyUserAnswers.set(TransportUnitsCount, Seq.empty)
    )) {

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(testErn, testArc, testIndex1, NormalMode).url
    }

    "must redirect to the add to list page (CAM-TU07) when any answer is present" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), TransportUnitType.Vehicle)
    )) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe
        Some(routes.TransportUnitsAddToListController.onPageLoad(testErn, testArc).url)
    }
  }
}
