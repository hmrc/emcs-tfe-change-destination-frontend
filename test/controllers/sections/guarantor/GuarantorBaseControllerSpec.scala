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

package controllers.sections.guarantor

import base.SpecBase
import mocks.services.MockUserAnswersService
import models.UserAnswers
import models.sections.guarantor.GuarantorArranger._
import navigation.BaseNavigator
import navigation.FakeNavigators.FakeNavigator
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage}
import play.api.mvc.Results.Ok
import play.api.mvc.{MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService

import scala.concurrent.Future

class GuarantorBaseControllerSpec extends SpecBase with MockUserAnswersService with GuiceOneAppPerSuite {
  object TestController extends GuarantorBaseController {

    override val userAnswersService: UserAnswersService = mockUserAnswersService
    override val navigator: BaseNavigator = new FakeNavigator(testOnwardRoute)

    override protected def controllerComponents: MessagesControllerComponents = messagesControllerComponents
  }

  class Test(ua: UserAnswers) {
    implicit val dr = dataRequest(FakeRequest(), ua)
  }

  "withGuarantorRequiredAnswer" - {
    "must return the success result" - {
      "when guarantor is required" in new Test(emptyUserAnswers.set(GuarantorRequiredPage, true)) {
        val result: Future[Result] = TestController.withGuarantorRequiredAnswer(Future.successful(Ok("beans")))

        status(result) mustBe OK
        contentAsString(result) mustBe "beans"
      }
    }
    "must redirect to GuarantorIndexController" - {
      "when GuarantorRequiredPage is not present in UserAnswers" in new Test(emptyUserAnswers) {
        val result: Future[Result] = TestController.withGuarantorRequiredAnswer(Future.successful(Ok("beans")))

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testDraftId).url)
      }
    }
    "must redirect to guarantor CYA" - {
      "when guarantor is not required" in new Test(emptyUserAnswers.set(GuarantorRequiredPage, false)) {
        val result: Future[Result] = TestController.withGuarantorRequiredAnswer(Future.successful(Ok("beans")))

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId).url)
      }
    }
  }

  "withGuarantorArrangerAnswer" - {
    "must return the success result" - {
      "when guarantor arranger is GoodsOwner" in new Test(emptyUserAnswers.set(GuarantorArrangerPage, GoodsOwner)) {
        val result: Future[Result] = TestController.withGuarantorArrangerAnswer(_ => Future.successful(Ok("beans")))

        status(result) mustBe OK
        contentAsString(result) mustBe "beans"
      }
      "when guarantor arranger is Transporter" in new Test(emptyUserAnswers.set(GuarantorArrangerPage, Transporter)) {
        val result: Future[Result] = TestController.withGuarantorArrangerAnswer(_ => Future.successful(Ok("beans")))

        status(result) mustBe OK
        contentAsString(result) mustBe "beans"
      }
    }
    "must redirect to GuarantorIndexController" - {
      "when GuarantorArrangerPage is not present in UserAnswers" in new Test(emptyUserAnswers) {
        val result: Future[Result] = TestController.withGuarantorArrangerAnswer(_ => Future.successful(Ok("beans")))

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testDraftId).url)
      }
    }
    "must redirect to guarantor CYA" - {
      "when guarantor arranger is Consignor" in new Test(emptyUserAnswers.set(GuarantorArrangerPage, Consignor)) {
        val result: Future[Result] = TestController.withGuarantorArrangerAnswer(_ => Future.successful(Ok("beans")))

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId).url)
      }
      "when guarantor arranger is Consignee" in new Test(emptyUserAnswers.set(GuarantorArrangerPage, Consignee)) {
        val result: Future[Result] = TestController.withGuarantorArrangerAnswer(_ => Future.successful(Ok("beans")))

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testDraftId).url)
      }
    }
  }
}
