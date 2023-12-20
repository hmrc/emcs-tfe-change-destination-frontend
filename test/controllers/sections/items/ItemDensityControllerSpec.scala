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
import forms.sections.items.ItemDensityFormProvider
import forms.sections.items.ItemDensityFormProvider.itemDensityFormField
import mocks.services.MockUserAnswersService
import models.{GoodsType, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemDensityPage, ItemExciseProductCodePage}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemDensityView

import scala.concurrent.Future

class ItemDensityControllerSpec extends SpecBase with MockUserAnswersService {
  lazy val testGoodsType: GoodsType = GoodsType.Energy
  lazy val defaultUserAnswers: UserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testGoodsType.code)

  class Test(val userAnswers: Option[UserAnswers]) {

    lazy val submitCall: Call = routes.ItemDensityController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val messages: Messages = messagesApi.preferred(request)

    lazy val formProvider: ItemDensityFormProvider = new ItemDensityFormProvider()
    lazy val form: Form[BigDecimal] = formProvider(testGoodsType)(messages)
    lazy val view: ItemDensityView = app.injector.instanceOf[ItemDensityView]

    lazy val controller = new ItemDensityController(
      messagesApi,
      mockUserAnswersService,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemDensity Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(defaultUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, submitCall, testGoodsType)(dataRequest(request, userAnswers.get), messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      defaultUserAnswers.set(ItemDensityPage(testIndex1), BigDecimal("123.45"))
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(BigDecimal("123.45")), submitCall, testGoodsType)(dataRequest(request, userAnswers.get), messages).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(defaultUserAnswers)) {
      MockUserAnswersService.set().returns(Future.successful(defaultUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(
        request.withFormUrlEncodedBody((itemDensityFormField, "1234.5"))
      )

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(defaultUserAnswers)) {
      val boundForm = form.bind(Map(itemDensityFormField -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody((itemDensityFormField, "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, submitCall, testGoodsType)(dataRequest(request, userAnswers.get), messages).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody((itemDensityFormField, "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
