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
import forms.sections.items.ItemFiscalMarksChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.GoodsType.Tobacco
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemFiscalMarksChoicePage, ItemFiscalMarksPage}
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemFiscalMarksChoiceView

import scala.concurrent.Future

class ItemFiscalMarksChoiceControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  val baseUserAnswers: UserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val formProvider: ItemFiscalMarksChoiceFormProvider = new ItemFiscalMarksChoiceFormProvider()
  lazy val view: ItemFiscalMarksChoiceView = app.injector.instanceOf[ItemFiscalMarksChoiceView]
  lazy val form: Form[Boolean] = formProvider(Tobacco)(messages(request))

  class Fixture(val userAnswers: Option[UserAnswers]) {
    lazy val controller = new ItemFiscalMarksChoiceController(
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

  val action = controllers.sections.items.routes.ItemFiscalMarksChoiceController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  "ItemFiscalMarksChoice Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture(Some(baseUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when Excise Product Code is missing (for GET)" in new Fixture(Some(emptyUserAnswers)) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when Excise Product Code is missing (for POST)" in new Fixture(Some(emptyUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Fixture(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, action, Tobacco)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(baseUserAnswers.set(ItemFiscalMarksChoicePage(testIndex1), true))
    ) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), action, Tobacco)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" - {
      "when the answer has changed" in new Fixture(Some(
        baseUserAnswers
          .set(ItemFiscalMarksChoicePage(testIndex1), false)
          .set(ItemFiscalMarksPage(testIndex1), "beans")
      )) {

        MockUserAnswersService.set(
          baseUserAnswers.set(ItemFiscalMarksChoicePage(testIndex1), true)
        ).returns(Future.successful(baseUserAnswers))

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
      "when the answer has not changed" in new Fixture(Some(
        baseUserAnswers
          .set(ItemFiscalMarksChoicePage(testIndex1), true)
          .set(ItemFiscalMarksPage(testIndex1), "beans")
      )) {

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
      "when the answer is new" in new Fixture(Some(baseUserAnswers)) {

        MockUserAnswersService.set(
          baseUserAnswers.set(ItemFiscalMarksChoicePage(testIndex1), true)
        ).returns(Future.successful(baseUserAnswers))

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(baseUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, action, Tobacco)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
