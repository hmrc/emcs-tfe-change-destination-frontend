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

package controllers.sections.items

import base.SpecBase
import controllers.actions.{DataRequiredAction, FakeDataRetrievalAction}
import fixtures.ItemFixtures
import mocks.services.{MockGetCnCodeInformationService, MockUserAnswersService}
import models.UserAnswers
import models.requests.CnCodeInformationItem
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemCheckAnswersView

import scala.concurrent.Future

class ItemCheckAnswersControllerSpec extends SpecBase
  with MockUserAnswersService
  with MockGetCnCodeInformationService
  with ItemFixtures {

  lazy val view: ItemCheckAnswersView = app.injector.instanceOf[ItemCheckAnswersView]

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val baseUserAnswers: UserAnswers =
    emptyUserAnswers
      .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
      .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)

  val submitCall: Call = routes.ItemCheckAnswersController.onSubmit(testErn, testDraftId, testIndex1)

  class Test(val userAnswers: Option[UserAnswers] = Some(baseUserAnswers)) {
    val controller: ItemCheckAnswersController = new ItemCheckAnswersController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      app.injector.instanceOf[DataRequiredAction],
      mockGetCnCodeInformationService,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemCheckAnswers Controller" - {
    "onPageLoad" - {
      "must render the page" - {
        "when EPC and CN Code are in UserAnswers service calls are successful" in new Test() {
          MockGetCnCodeInformationService
            .getCnCodeInformationWithMovementItems(Seq(CnCodeInformationItem(testEpcWine, testCnCodeWine)))
            .returns(Future.successful(Seq(CnCodeInformationItem(testEpcWine, testCnCodeWine) -> testCommodityCodeWine)))

          val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

          status(result) mustEqual OK
          contentAsString(result) mustBe
            view(testIndex1, testCommodityCodeWine, submitCall)(dataRequest(request, userAnswers.get), messages(request)).toString
        }
      }
      "must redirect to Index of section" - {
        "when EPC is not in UserAnswers" in new Test(Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine))) {
          val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
        }
        "when CN Code is not in UserAnswers" in new Test(Some(emptyUserAnswers.set(ItemCommodityCodePage(testIndex1), testCnCodeWine))) {
          val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
        }
        "when call to service returns an unexpected number of items" in new Test() {
          MockGetCnCodeInformationService
            .getCnCodeInformationWithMovementItems(Seq(CnCodeInformationItem(testEpcWine, testCnCodeWine)))
            .returns(Future.successful(Seq()))

          val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
        }
        "when the idx is outside of bounds" in new Test() {
          val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex2)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
        }
      }
      "must redirect to Journey recovery when no user answers" in new Test(None) {
        val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.JourneyRecoveryController.onPageLoad().url)
      }
    }

    "onSubmit" - {
      "must redirect to Index of section when the idx is outside of bounds for a POST" in new Test() {
        val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex2)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
      }
      "must redirect to Journey recovery when no user answers" in new Test(None) {
        val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.JourneyRecoveryController.onPageLoad().url)
      }
    }
  }

}
