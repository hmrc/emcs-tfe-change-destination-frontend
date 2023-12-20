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
import forms.sections.items.ItemBulkPackagingChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.GoodsType.Tobacco
import models.response.referenceData.BulkPackagingType
import models.sections.items.{ItemBulkPackagingCode, ItemPackagingSealTypeModel}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemBulkPackagingChoicePage, ItemBulkPackagingSealChoicePage, ItemBulkPackagingSealTypePage, ItemBulkPackagingSelectPage, ItemCommodityCodePage, ItemExciseProductCodePage, ItemPackagingQuantityPage, ItemWineMoreInformationPage}
import play.api.Play.materializer
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemBulkPackagingChoiceView

import scala.concurrent.Future

class ItemBulkPackagingChoiceControllerSpec extends SpecBase
  with MockUserAnswersService
  with ItemFixtures {

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val formProvider = new ItemBulkPackagingChoiceFormProvider()
  lazy val form: Form[Boolean] = formProvider.apply(Tobacco)(messages(request))
  lazy val view: ItemBulkPackagingChoiceView = app.injector.instanceOf[ItemBulkPackagingChoiceView]

  def submitRoute: Call = routes.ItemBulkPackagingChoiceController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  class Test(userAnswers: Option[UserAnswers]) {
    lazy val controller = new ItemBulkPackagingChoiceController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      app.injector.instanceOf[DataRequiredAction],
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemBulkPackagingChoice Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
    )) {
      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest())

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, submitRoute, Tobacco)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
        .set(ItemBulkPackagingChoicePage(testIndex1), true)
    )) {
      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest())

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), submitRoute, Tobacco)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
    )) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest().withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must remove previously answered Packaging pages when submitted data has changed" in new Test(Some(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
        .set(ItemBulkPackagingChoicePage(testIndex1), false)
        .set(ItemBulkPackagingSealChoicePage(testIndex1), true)
        .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(ItemBulkPackagingCode.BulkLiquid, "test description"))
        .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("test type", Some("test description")))
        .set(ItemWineMoreInformationPage(testIndex1), Some("test information"))
        .set(ItemPackagingQuantityPage(testIndex1, testIndex1), "test quantity")
    )) {

      MockUserAnswersService.set(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemBulkPackagingChoicePage(testIndex1), true)
      ).returns(Future.successful(emptyUserAnswers))

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest().withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must not remove previously answered Packaging pages when submitted data has not changed" in new Test(Some(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
        .set(ItemBulkPackagingChoicePage(testIndex1), true)
        .set(ItemBulkPackagingSealChoicePage(testIndex1), true)
        .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(ItemBulkPackagingCode.BulkLiquid, "test description"))
    )) {
      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest().withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the Index page for GET when no Item" in new Test(Some(emptyUserAnswers)) {
      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest())

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the Index page for POST when no Item" in new Test(Some(emptyUserAnswers)) {
      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest().withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the Index page for GET when no EPC" in new Test(Some(emptyUserAnswers.set(ItemCommodityCodePage(testIndex1), testCnCodeWine))) {
      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest())

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the Index page for POST when no EPC" in new Test(Some(emptyUserAnswers.set(ItemCommodityCodePage(testIndex1), testCnCodeWine))) {
      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest().withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(
      emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
    )) {
      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest().withFormUrlEncodedBody(("value", "")))

      val boundForm = form.bind(Map("value" -> ""))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, submitRoute, Tobacco)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest())

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest().withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
