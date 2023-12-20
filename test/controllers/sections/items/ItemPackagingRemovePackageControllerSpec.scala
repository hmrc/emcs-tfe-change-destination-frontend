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
import forms.sections.items.ItemPackagingRemovePackageFormProvider
import mocks.services.MockUserAnswersService
import models.UserAnswers
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemSelectPackagingPage, ItemsPackagingSectionItems}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemPackagingRemovePackageView

import scala.concurrent.Future

class ItemPackagingRemovePackageControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  lazy val formProvider = new ItemPackagingRemovePackageFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[ItemPackagingRemovePackageView]

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
    .set(ItemExciseProductCodePage(testIndex2), testEpcWine)
    .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageAerosol)
    .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), testPackageBag)
    .set(ItemSelectPackagingPage(testIndex2, testPackagingIndex1), testPackageBag)

  val action: Call = controllers.sections.items.routes.ItemPackagingRemovePackageController.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1)

  class Test(val userAnswers: Option[UserAnswers] = Some(baseUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemPackagingRemovePackageController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemPackagingRemovePackage Controller" - {

    "must redirect to Index of Packaging section when the Items idx is outside of bounds for a GET" in new Test() {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex3, testPackagingIndex1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex3).url
    }

    "must redirect to Index of Packaging section when the Items idx is outside of bounds for a POST" in new Test() {

      val result = controller.onSubmit(testErn, testDraftId, testIndex3, testPackagingIndex1)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex3).url
    }

    "must redirect to Index of Packaging section when the Package idx is outside of bounds for a GET" in new Test() {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex3)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of Packaging section when the Package idx is outside of bounds for a POST" in new Test() {

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex3)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must return OK and the correct view for a GET" in new Test() {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, action, testPackageAerosol.description)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the Index Controller when yes is selected (removing the section)" in new Test() {

      val updatedAnswers = baseUserAnswers.remove(ItemsPackagingSectionItems(testIndex1, testPackagingIndex2))

      MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex2)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.items.routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to the Item CYA page when no is selected" in new Test() {

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.items.routes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test() {

      val boundForm = form.bind(Map("value" -> ""))
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, action, testPackageAerosol.description)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
