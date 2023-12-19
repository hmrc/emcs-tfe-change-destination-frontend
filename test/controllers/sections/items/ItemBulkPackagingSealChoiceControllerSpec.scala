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
import forms.sections.items.ItemPackagingSealChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.response.referenceData.BulkPackagingType
import models.sections.items.ItemBulkPackagingCode.BulkSolidPowders
import models.sections.items.ItemPackagingSealTypeModel
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemBulkPackagingSealChoicePage, ItemBulkPackagingSealTypePage, ItemBulkPackagingSelectPage, ItemExciseProductCodePage}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemPackagingSealChoiceView

import scala.concurrent.Future

class ItemBulkPackagingSealChoiceControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider = new ItemPackagingSealChoiceFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[ItemPackagingSealChoiceView]
  val action: Call = controllers.sections.items.routes.ItemBulkPackagingSealChoiceController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), "W300")
    .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkSolidPowders, "Bulk, solid, fine (powders)"))

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemBulkPackagingSealChoiceController(
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

  "ItemBulkPackagingSealChoice Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Test(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Test(Some(baseUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when Excise Product Code is missing (for GET)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when Excise Product Code is missing (for POST)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Test(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, action, "Bulk, solid, fine (powders)")(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      baseUserAnswers.set(ItemBulkPackagingSealChoicePage(testIndex1), true)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), action, "Bulk, solid, fine (powders)")(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(baseUserAnswers)) {

      MockUserAnswersService.set(
        baseUserAnswers.set(ItemBulkPackagingSealChoicePage(testIndex1), true)
      ).returns(Future.successful(
        baseUserAnswers.set(ItemBulkPackagingSealChoicePage(testIndex1), true)
      ))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must remove previous ItemBulkPackagingSealTypePage answer when submitted answer has changed" in new Test(Some(
      baseUserAnswers
        .set(ItemBulkPackagingSealChoicePage(testIndex1), true)
        .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("type", None))
    )) {

      MockUserAnswersService.set(
        baseUserAnswers.set(ItemBulkPackagingSealChoicePage(testIndex1), false)
      ).returns(Future.successful(
        baseUserAnswers.set(ItemBulkPackagingSealChoicePage(testIndex1), false)
      ))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(baseUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, action, "Bulk, solid, fine (powders)")(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
