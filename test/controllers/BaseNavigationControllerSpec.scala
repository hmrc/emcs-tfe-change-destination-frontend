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

package controllers

import base.SpecBase
import mocks.services.MockUserAnswersService
import models.requests.DataRequest
import models.{Index, NormalMode, UserAnswers}
import navigation.BaseNavigator
import navigation.FakeNavigators.FakeNavigator
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.QuestionPage
import play.api.libs.json.{JsObject, JsPath, __}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, defaultAwaitTimeout, redirectLocation}
import queries.Derivable
import services.UserAnswersService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class BaseNavigationControllerSpec extends SpecBase with GuiceOneAppPerSuite with MockUserAnswersService {
  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(GET, "/foo/bar"))

    val page = new QuestionPage[String] {
      override val path: JsPath = __ \ "page1"
      override def getValueFromIE801(implicit request: DataRequest[_]): Option[String] = None
    }
    val page2 = new QuestionPage[String] {
      override val path: JsPath = __ \ "page2"
      override def getValueFromIE801(implicit request: DataRequest[_]): Option[String] = Some("IE801 answer")
    }
    val page3 = new QuestionPage[String] {
      override val path: JsPath = __ \ "page3"
      override def getValueFromIE801(implicit request: DataRequest[_]): Option[String] = None
    }
    val value = "foo"

    case class TestIndexPage(index: Index) extends QuestionPage[String] {
      override val path: JsPath = TestDerivable.path \ index.position \ "test"
      override def getValueFromIE801(implicit request: DataRequest[_]): Option[String] = None
    }

    object TestDerivable extends Derivable[Seq[JsObject], Int] {
      override val derive: Seq[JsObject] => Int = _.size

      override val path: JsPath = JsPath \ "something"
      override def getValueFromIE801(implicit request: DataRequest[_]): Option[Seq[JsObject]] = None
    }

    val testNavigator = new FakeNavigator(testOnwardRoute)

    lazy val testController = new BaseNavigationController with BaseController {
      override val userAnswersService: UserAnswersService = mockUserAnswersService
      override val navigator: BaseNavigator = testNavigator

      override protected def controllerComponents: MessagesControllerComponents = messagesControllerComponents
    }
  }

  "saveAndRedirect" - {
    "with currentAnswers" - {
      "must save the answer and redirect" - {
        "when current UserAnswers doesn't contain the input answer" in new Test {

          val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

          MockUserAnswersService.set().returns(Future.successful(newUserAnswers))

          val answer: Future[Result] =
            testController.saveAndRedirect(page, value, emptyUserAnswers, NormalMode)

          redirectLocation(answer) mustBe Some(testOnwardRoute.url)
        }
      }

      "must only redirect" - {
        "when current UserAnswers contains the input answer" in new Test {
          val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

          MockUserAnswersService.set().never()

          val answer: Future[Result] =
            testController.saveAndRedirect(page, value, newUserAnswers, NormalMode)

          redirectLocation(answer) mustBe Some(testOnwardRoute.url)
        }
      }
    }

    "without currentAnswers" - {
      "must save the answer and redirect" in new Test {
        val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

        MockUserAnswersService.set().returns(Future.successful(newUserAnswers))

        val answer: Future[Result] =
          testController.saveAndRedirect(page, value, NormalMode)(dataRequest(FakeRequest()), implicitly)

        redirectLocation(answer) mustBe Some(testOnwardRoute.url)
      }
    }
  }

  "cleanseUserAnswersIfValueHasChanged" - {

    // To make it clear, this function just gets rid of everything
    // so when the cleansing function runs, the result should be an empty UserAnswers
    // otherwise, the result should be whatever you pass in the DataRequest
    def cleansingFunction: UserAnswers = emptyUserAnswers

    "must run the cleansing function" - {
      "when current UserAnswers contains the input page but the answer is different" in new Test {
        val result: UserAnswers =
          testController.cleanseUserAnswersIfValueHasChanged(
            page = page,
            newAnswer = "bar",
            cleansingFunction = cleansingFunction
          )(dataRequest(FakeRequest(), emptyUserAnswers.set(page, value)), implicitly)

        result mustBe emptyUserAnswers
      }

      "when current UserAnswers DOES NOT contain the input page but 801 does and it's different" in new Test {
        val result: UserAnswers =
          testController.cleanseUserAnswersIfValueHasChanged(
            page = page2,
            newAnswer = "bar",
            cleansingFunction = cleansingFunction
          )(dataRequest(FakeRequest(), emptyUserAnswers.set(page, value)), implicitly)

        result mustBe emptyUserAnswers
      }
    }
    "must not run the cleansing function" - {
      "when current UserAnswers contains the input page but the answer is the same" in new Test {
        val result: UserAnswers =
          testController.cleanseUserAnswersIfValueHasChanged(
            page = page,
            newAnswer = value,
            cleansingFunction = cleansingFunction
          )(dataRequest(FakeRequest(), emptyUserAnswers.set(page, value)), implicitly)

        result mustBe emptyUserAnswers.set(page, value)
      }
      "when current UserAnswers doesn't contain the input page" in new Test {

        val result: UserAnswers =
          testController.cleanseUserAnswersIfValueHasChanged(
            page = page3,
            newAnswer = value,
            cleansingFunction = cleansingFunction
          )(dataRequest(FakeRequest(), emptyUserAnswers.set(page, value)), implicitly)

        result mustBe emptyUserAnswers.set(page, value)
      }
    }
  }

  "validateIndexForJourneyEntry" - {
    "must run the onSuccess function" - {
      "when no user answers present and value is Index(0)" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(TestDerivable, Index(0))(true, false)(dataRequest(FakeRequest(), emptyUserAnswers), implicitly)

        result mustBe true
      }

      "when index is for a new page where index exists in previous items" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(
            TestDerivable, Index(2)
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers
              .set(TestIndexPage(Index(0)), "answer")
              .set(TestIndexPage(Index(1)), "answer")
              .set(TestIndexPage(Index(2)), "answer")
              .set(TestIndexPage(Index(3)), "answer")
            ), implicitly
          )

        result mustBe true
      }

      "when index is for a new page where index is one more than previous page" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(
            TestDerivable, Index(1)
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers.set(TestIndexPage(Index(0)), "answer")), implicitly
          )

        result mustBe true
      }
    }
    "must run the onFailure function" - {
      "when no user answers present and index position is more than 0" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(TestDerivable, Index(2))(true, false)(dataRequest(FakeRequest(), emptyUserAnswers), implicitly)

        result mustBe false
      }

      "when index larger than max value - 1" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(
            TestDerivable, Index(2), max = 2
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers
              .set(TestIndexPage(Index(0)), "answer")
              .set(TestIndexPage(Index(1)), "answer")
            ), implicitly
          )

        result mustBe false
      }

      "when index is for a new page where index is greater than one more than previous page" in new Test {
        val result: Boolean =
          testController.validateIndexForJourneyEntry(
            TestDerivable, Index(2)
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers.set(TestIndexPage(Index(0)), "answer")), implicitly
          )

        result mustBe false
      }
    }
  }

  "validateIndex" - {

    "must run the onSuccess function" - {
      "when index is for a new page where index exists in previous items" in new Test {
        val result: Boolean =
          testController.validateIndex(
            TestDerivable, Index(2)
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers
              .set(TestIndexPage(Index(0)), "answer")
              .set(TestIndexPage(Index(1)), "answer")
              .set(TestIndexPage(Index(2)), "answer")
              .set(TestIndexPage(Index(3)), "answer")
            ), implicitly
          )

        result mustBe true
      }
    }
    "must run the onFailure function" - {
      "when no user answers present and value is Index(0)" in new Test {
        val result: Boolean =
          testController.validateIndex(TestDerivable, Index(0))(true, false)(dataRequest(FakeRequest(), emptyUserAnswers), implicitly)

        result mustBe false
      }

      "when index is greater than last index in user answers" in new Test {
        val result: Boolean =
          testController.validateIndex(
            TestDerivable, Index(2)
          )(
            true, false
          )(
            dataRequest(FakeRequest(), emptyUserAnswers
              .set(TestIndexPage(Index(0)), "answer")
              .set(TestIndexPage(Index(1)), "answer")
            ), implicitly
          )

        result mustBe false
      }
    }
  }
}
