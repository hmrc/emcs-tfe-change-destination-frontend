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

package controllers.sections.info

import base.SpecBase
import controllers.actions.FakeMovementAction
import controllers.actions.predraft.FakePreDraftRetrievalAction
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.UserAnswers
import models.response.emcsTfe.GetMovementResponse
import models.sections.info.ChangeType.Consignee
import models.sections.info.movementScenario.DestinationType.UnknownDestination
import navigation.FakeNavigators.FakeInfoNavigator
import pages.sections.info.{ChangeDestinationTypePage, ChangeTypePage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}

import scala.concurrent.Future

class InfoIndexControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService {

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers), movementDetails: GetMovementResponse = maxGetMovementResponse) {
    lazy val controller = new InfoIndexController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      userAllowList = fakeUserAllowListAction,
      navigator = new FakeInfoNavigator(testOnwardRoute),
      auth = fakeAuthAction,
      preDraftService = mockPreDraftService,
      getPreDraftData = new FakePreDraftRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requirePreDraftData = preDraftDataRequiredAction,
      withMovement = new FakeMovementAction(movementDetails),
      controllerComponents = Helpers.stubMessagesControllerComponents()
    )
  }

  "InfoIndex Controller" - {

    "when the Destination Type of the IE801 is UnknownDestination" - {

      "must set the ChangeType to Consignee and the ChangeDestinationType to True redirecting to NewDestinationType" in new Test(
        movementDetails = maxGetMovementResponse.copy(destinationType = UnknownDestination)
      ) {

        MockPreDraftService.set(emptyUserAnswers
          .set(ChangeTypePage, Consignee)
          .set(ChangeDestinationTypePage, true)
        ).returns(Future.successful(true))

        val result = controller.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.NewDestinationTypeController.onPreDraftPageLoad(testErn, testArc).url

      }
    }

    "in any other scenario must redirect to the Change Type page" in new Test() {

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ChangeTypeController.onPageLoad(testErn, testArc).url
    }
  }
}