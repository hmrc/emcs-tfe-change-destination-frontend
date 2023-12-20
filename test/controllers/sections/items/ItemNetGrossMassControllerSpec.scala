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
import forms.sections.items.ItemNetGrossMassFormProvider
import mocks.services.MockUserAnswersService
import models.sections.items.ItemNetGrossMassModel
import models.{GoodsType, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemNetGrossMassPage}
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemNetGrossMassView

import scala.concurrent.Future

class ItemNetGrossMassControllerSpec extends SpecBase with MockUserAnswersService {

  val testModel: ItemNetGrossMassModel = ItemNetGrossMassModel(BigDecimal("1234"), BigDecimal("4523"))

  val formProvider: ItemNetGrossMassFormProvider = new ItemNetGrossMassFormProvider()
  val form: Form[ItemNetGrossMassModel] = formProvider.form

  lazy val itemNetGrossMassRoute: String =
    routes.ItemNetGrossMassController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url

  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val view: ItemNetGrossMassView = app.injector.instanceOf[ItemNetGrossMassView]

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val controller = new ItemNetGrossMassController(
      messagesApi,
      mockUserAnswersService,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      app.injector.instanceOf[DataRequiredAction],
      fakeUserAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemNetGrossMass Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, testIndex1, GoodsType.Beer, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers
        .set(ItemNetGrossMassPage(testIndex1), testModel)
        .set(ItemExciseProductCodePage(testIndex1), "B000")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(testModel), testIndex1, GoodsType.Beer, NormalMode)(dataRequest(request, userAnswers.get), messages(request)
      ).toString
    }

    "must return Redirect to index controller if no EPC found for GET" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.items.routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000")
    )) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody("netMass" -> "123", "grossMass" -> "124"))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return Redirect to index controller if no EPC found for POST" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody("netMass" -> "123", "grossMass" -> "124"))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.sections.items.routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000")
    )) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, testIndex1, GoodsType.Beer, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody("netMass" -> "123", "grossMass" -> "124"))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
