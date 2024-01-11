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
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.UserAnswers
import models.requests.DataRequest
import navigation.BaseNavigator
import navigation.FakeNavigators.FakeNavigator
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.QuestionPage
import play.api.libs.json.{JsPath, __}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, defaultAwaitTimeout, redirectLocation}
import services.{PreDraftService, UserAnswersService}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class BasePreDraftNavigationControllerSpec extends SpecBase with GuiceOneAppPerSuite with MockUserAnswersService with MockPreDraftService {
  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(GET, "/foo/bar"))

    val page = new QuestionPage[String] {
      override val path: JsPath = __ \ "page1"
      override def getValueFromIE801(implicit request: DataRequest[_]): Option[String] = None
    }

    val value = "foo"

    val testNavigator = new FakeNavigator(testOnwardRoute)

    lazy val controller = new BasePreDraftNavigationController with BaseController {
      override val preDraftService: PreDraftService = mockPreDraftService
      override val userAnswersService: UserAnswersService = mockUserAnswersService
      override val navigator: BaseNavigator = testNavigator

      override protected def controllerComponents: MessagesControllerComponents = messagesControllerComponents
    }
  }

  "createDraftEntryAndRedirect" - {

    "must save the current UserAnswers to UserAnswersService and clear the pre draft" in new Test {

      val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

      MockUserAnswersService.set(newUserAnswers).returns(Future.successful(newUserAnswers))
      MockPreDraftService.clear(testErn, testArc).returns(Future.successful(true))

      val result: Future[Result] = controller.createDraftEntryAndRedirect(page, value)

      redirectLocation(result).value mustBe testOnwardRoute.url
    }
  }
}
