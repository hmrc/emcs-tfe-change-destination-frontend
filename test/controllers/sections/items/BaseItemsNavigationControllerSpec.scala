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
import fixtures.ItemFixtures
import mocks.services.{MockGetCnCodeInformationService, MockUserAnswersService}
import models.UserAnswers
import models.requests.DataRequest
import models.response.referenceData.{BulkPackagingType, CnCodeInformation, ItemPackaging}
import models.sections.items.ItemBulkPackagingCode.BulkSolidPowders
import navigation.BaseNavigator
import navigation.FakeNavigators.FakeNavigator
import pages.sections.items._
import play.api.mvc.{MessagesControllerComponents, Result, Results}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import services.UserAnswersService

import scala.concurrent.Future

class BaseItemsNavigationControllerSpec extends SpecBase
  with MockGetCnCodeInformationService
  with MockUserAnswersService
  with ItemFixtures {

  class Test(val userAnswers: UserAnswers) {
    implicit val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

    val controller: BaseItemsNavigationController = new BaseItemsNavigationController {
      override val userAnswersService: UserAnswersService = mockUserAnswersService
      override val navigator: BaseNavigator = new FakeNavigator(testOnwardRoute)

      override protected def controllerComponents: MessagesControllerComponents = Helpers.stubMessagesControllerComponents()
    }

    val cnCodeSuccessFunction: CnCodeInformation => Result = _ => Results.Ok
    val itemPackagingSuccessFunction: String => Future[Result] = _ => Future.successful(Results.Ok)
  }

  "withItemPackaging" - {
    "must return the item packaging description when both the item and packaging indexes are valid" in new Test(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
        .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BG", "Bag"))
    ) {
      val result: Future[Result] = controller.withItemPackaging(testIndex1, testPackagingIndex1)(itemPackagingSuccessFunction)

      status(result) mustBe OK
    }

    "must redirect to the packaging index" - {
      "when the item index is invalid" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BG", "Bag"))
      ) {
        val result: Future[Result] = controller.withItemPackaging(testIndex2, testPackagingIndex1)(itemPackagingSuccessFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.arc, testIndex2).url
      }

      "when the packaging index is invalid" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BG", "Bag"))
      ) {
        val result: Future[Result] = controller.withItemPackaging(testIndex1, testPackagingIndex2)(itemPackagingSuccessFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(request.ern, request.arc, testIndex1).url
      }
    }
  }

  "withBulkItemPackaging" - {
    "must return the item bulk packaging description when the item index is valid" in new Test(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testCnCodeTobacco)
        .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkSolidPowders, "Bulk, solid, fine (powders)"))
    ) {
      val result: Future[Result] = controller.withItemBulkPackaging(testIndex1)(itemPackagingSuccessFunction)

      status(result) mustBe OK
    }

    "must redirect to the items index" - {
      "when the index is invalid" in new Test(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testCnCodeTobacco)
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkSolidPowders, "Bulk, solid, fine (powders)"))
      ) {
        val result: Future[Result] = controller.withItemBulkPackaging(testIndex2)(itemPackagingSuccessFunction)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(request.ern, request.arc).url
      }
    }
  }
}
