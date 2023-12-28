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

package controllers.actions.predraft

import base.SpecBase
import mocks.services.{MockGetTraderKnownFactsService, MockPreDraftService}
import models.requests.{MovementRequest, OptionalDataRequest, UserRequest}
import play.api.mvc.ActionTransformer
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PreDraftRetrievalActionSpec extends SpecBase with MockPreDraftService with MockGetTraderKnownFactsService {

  lazy val dataRetrievalAction: ActionTransformer[MovementRequest, OptionalDataRequest] =
    new PreDraftDataRetrievalActionImpl(
      mockPreDraftService,
      mockGetTraderKnownFactsService
    ).apply()

  "Data Retrieval Action" - {
    "when there is no data in the cache" - {
      "must set userAnswers to 'None' in the request" in {
        MockPreDraftService.get(testErn, testSessionId).returns(Future.successful(None))
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(Some(testMinTraderKnownFacts)))

        val result = dataRetrievalAction.refine(movementRequest(FakeRequest())).futureValue.value

        result.userAnswers must not be defined
      }
      "must set TraderKnownFacts to 'None' in the request" in {
        MockPreDraftService.get(testErn, testSessionId).returns(Future.successful(None))
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(None))

        val result = dataRetrievalAction.refine(movementRequest(FakeRequest())).futureValue.value

        result.traderKnownFacts must not be defined
      }
    }

    "when there is data in the cache" - {
      "must build a userAnswers object and add it to the request" in {
        MockPreDraftService.get(testErn, testSessionId).returns(Future(Some(emptyUserAnswers)))
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(Some(testMinTraderKnownFacts)))

        val result = dataRetrievalAction.refine(movementRequest(FakeRequest())).futureValue.value

        result.userAnswers mustBe defined
      }

      "must build a TraderKnownFacts object and add it to the request" in {
        MockPreDraftService.get(testErn, testSessionId).returns(Future(Some(emptyUserAnswers)))
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(Some(testMinTraderKnownFacts)))

        val result = dataRetrievalAction.refine(movementRequest(FakeRequest())).futureValue.value

        result.traderKnownFacts mustBe defined
      }
    }
  }
}
