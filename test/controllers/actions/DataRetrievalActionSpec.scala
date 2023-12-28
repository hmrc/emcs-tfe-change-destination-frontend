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

package controllers.actions

import base.SpecBase
import mocks.services.{MockGetTraderKnownFactsService, MockUserAnswersService}
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with MockUserAnswersService with MockGetTraderKnownFactsService {

  lazy val dataRetrievalAction: DataRetrievalActionImpl =
    new DataRetrievalActionImpl(
      mockUserAnswersService,
      mockGetTraderKnownFactsService
    )

  "Data Retrieval Action" - {
    "when there is no data in the cache" - {
      "must set userAnswers to 'None' in the request" in {
        MockUserAnswersService.get(testErn, testArc).returns(Future.successful(None))
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(Some(testMinTraderKnownFacts)))

        val result = dataRetrievalAction.refine(movementRequest(FakeRequest())).futureValue.value

        result.userAnswers must not be defined
      }
      "must set TraderKnownFacts to 'None' in the request" in {
        MockUserAnswersService.get(testErn, testArc).returns(Future.successful(None))
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(None))

        val result = dataRetrievalAction.refine(movementRequest(FakeRequest())).futureValue.value

        result.traderKnownFacts must not be defined
      }
    }

    "when there is data in the cache" - {
      "must build a userAnswers object and add it to the request" in {
        MockUserAnswersService.get(testErn, testArc).returns(Future(Some(emptyUserAnswers)))
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(Some(testMinTraderKnownFacts)))

        val result = dataRetrievalAction.refine(movementRequest(FakeRequest())).futureValue.value

        result.userAnswers mustBe defined
      }

      "must build a TraderKnownFacts object and add it to the request" in {
        MockUserAnswersService.get(testErn, testArc).returns(Future(Some(emptyUserAnswers)))
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(Some(testMinTraderKnownFacts)))

        val result = dataRetrievalAction.refine(movementRequest(FakeRequest())).futureValue.value

        result.traderKnownFacts mustBe defined
      }
    }
  }
}
