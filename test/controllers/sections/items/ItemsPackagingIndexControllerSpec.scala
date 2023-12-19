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

class ItemsPackagingIndexControllerSpec extends SpecBase
  with MockUserAnswersService
  with ItemFixtures {

  class Test(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemsPackagingIndexController(
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

    "when ItemsSection.isCompleted" - {

      "must redirect to the CYA controller" in new Test(Some(singleCompletedWineItem)) {
        val result = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe
          controllers.sections.items.routes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1).url
      }
    }

    "must redirect to the items select packaging controller" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.sections.items.routes.ItemSelectPackagingController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode).url
    }

    "must redirect to the items index controller" - {
      "when the items index is out of bounds" in new Test(Some(
        emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
      )) {
        val result = controller.onPageLoad(testErn, testDraftId, testIndex2)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe
          controllers.sections.items.routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }
    }
  }
}