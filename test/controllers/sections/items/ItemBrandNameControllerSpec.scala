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
import fixtures.messages.sections.items.ItemBrandNameMessages
import forms.sections.items.ItemBrandNameFormProvider
import forms.sections.items.ItemBrandNameFormProvider.{brandNameField, hasBrandNameField}
import mocks.services.MockUserAnswersService
import models.GoodsType.Wine
import models.sections.items.ItemBrandNameModel
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemBrandNamePage, ItemExciseProductCodePage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemBrandNameView

import scala.concurrent.Future

class ItemBrandNameControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  //Ensures a dummy item exists in the array for testing
  val defaultUserAnswers: UserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)

  def itemBrandNameSubmitAction(idx: Index = testIndex1): Call = routes.ItemBrandNameController.onSubmit(testErn, testDraftId, idx, NormalMode)

  lazy val formProvider: ItemBrandNameFormProvider = new ItemBrandNameFormProvider()
  lazy val form: Form[ItemBrandNameModel] = formProvider()
  lazy val view: ItemBrandNameView = app.injector.instanceOf[ItemBrandNameView]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemBrandNameController(
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

  "ItemBrandName Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture() {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody((hasBrandNameField, "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
    }

    "must return OK and the correct view for a GET" in new Fixture() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, itemBrandNameSubmitAction(), Wine)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      defaultUserAnswers.set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(ItemBrandNameModel(hasBrandName = true, Some("brand"))),
        itemBrandNameSubmitAction(),
        Wine
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody((hasBrandNameField, "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted (no brand name supplied)" in new Fixture() {
      val boundForm =
        form
          .bind(Map(hasBrandNameField -> "true"))
          .withError(brandNameField, ItemBrandNameMessages.English.errorBrandNameRequired)
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody((hasBrandNameField, "true")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, itemBrandNameSubmitAction(), Wine)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody((hasBrandNameField, "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
