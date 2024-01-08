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

package services

import base.SpecBase
import mocks.connectors.MockUserAnswersConnector
import models.response.{UnexpectedDownstreamResponseError, UserAnswersException}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class UserAnswersServiceSpec extends SpecBase with MockUserAnswersConnector {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new UserAnswersService(mockUserAnswersConnector)

  ".get()" - {

    "should return Some(UserAnswers)" - {

      "when Connector returns success from downstream" in {

        MockUserAnswersConnector.get(testErn, testArc).returns(Future.successful(Right(Some(emptyUserAnswers))))
        testService.get(testErn, testArc).futureValue mustBe Some(emptyUserAnswers)
      }
    }

    "should return None" - {

      "when Connector returns success from downstream with no data" in {

        MockUserAnswersConnector.get(testErn, testArc).returns(Future.successful(Right(None)))
        testService.get(testErn, testArc).futureValue mustBe None
      }
    }

    "should throw UserAnswersException" - {

      "when Connector returns failure from downstream" in {

        MockUserAnswersConnector.get(testErn, testArc).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        intercept[UserAnswersException](await(testService.get(testErn, testArc))).getMessage mustBe
          s"Failed to retrieve UserAnswers from emcs-tfe for ern: '$testErn' & arc: '$testArc'"
      }
    }
  }

  ".put()" - {

    "should return UserAnswers" - {

      "when Connector returns success from downstream" in {

        MockUserAnswersConnector.put(emptyUserAnswers).returns(Future.successful(Right(emptyUserAnswers)))
        testService.set(emptyUserAnswers).futureValue mustBe emptyUserAnswers
      }
    }

    "should throw UserAnswersException" - {

      "when Connector returns failure from downstream" in {

        MockUserAnswersConnector.put(emptyUserAnswers).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        intercept[UserAnswersException](await(testService.set(emptyUserAnswers))).getMessage mustBe
          s"Failed to store UserAnswers in emcs-tfe for ern: '$testErn' & arc: '$testArc'"
      }
    }
  }

  ".delete()" - {

    "should return true" - {

      "when Connector returns success from downstream" in {

        MockUserAnswersConnector.delete(testErn, testArc).returns(Future.successful(Right(true)))
        testService.clear(emptyUserAnswers).futureValue mustBe true
      }
    }

    "should throw UserAnswersException" - {

      "when Connector returns failure from downstream" in {

        MockUserAnswersConnector.delete(testErn, testArc).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        intercept[UserAnswersException](await(testService.clear(emptyUserAnswers))).getMessage mustBe
          s"Failed to delete UserAnswers from emcs-tfe for ern: '$testErn' & arc: '$testArc'"
      }
    }
  }
}
