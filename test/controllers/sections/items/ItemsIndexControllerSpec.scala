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
import controllers.actions.FakeDataRetrievalAction
import fixtures.ItemFixtures
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.ItemsNavigator
import pages.sections.items.ItemExciseProductCodePage
import play.api.http.Status.SEE_OTHER
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}

class ItemsIndexControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemsIndexController(
      mockUserAnswersService,
      app.injector.instanceOf[ItemsNavigator],
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      Helpers.stubMessagesControllerComponents()
    )
  }

  "ItemsIndexController" - {

    "when ItemsSectionItems.isCompleted" - {

      "must redirect to the Items Add to List controller" in new Test(Some(singleCompletedWineItem)) {

        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsAddToListController.onPageLoad(testErn, testDraftId).url
      }
    }

    "when more than one item is added and is in progress" - {

      "must redirect to the Items Add to List controller" in new Test(Some(singleCompletedWineItem
        .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
      )) {

        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsAddToListController.onPageLoad(testErn, testDraftId).url
      }
    }

    "when one item is currently in progress" - {

      "must redirect to the start of that item" in new Test(Some(emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
      )) {

        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
      }
    }

    "when no item added yet" - {

      "must redirect to the start of that item" in new Test(Some(emptyUserAnswers)) {

        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url
      }
    }
  }
}