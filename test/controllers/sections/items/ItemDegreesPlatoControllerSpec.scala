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
import fixtures.messages.sections.items.ItemDegreesPlatoMessages
import forms.sections.items.ItemDegreesPlatoFormProvider
import forms.sections.items.ItemDegreesPlatoFormProvider.{degreesPlatoField, hasDegreesPlatoField}
import mocks.services.MockUserAnswersService
import models.GoodsType.Wine
import models.sections.items.ItemDegreesPlatoModel
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemDegreesPlatoPage, ItemExciseProductCodePage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemDegreesPlatoView

import scala.concurrent.Future

class ItemDegreesPlatoControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  //Ensures a dummy item exists in the array for testing
  val defaultUserAnswers: UserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)

  def itemDegreesPlatoSubmitAction(idx: Index = testIndex1): Call = routes.ItemDegreesPlatoController.onSubmit(testErn, testDraftId, idx, NormalMode)

  lazy val formProvider: ItemDegreesPlatoFormProvider = new ItemDegreesPlatoFormProvider()
  lazy val form: Form[ItemDegreesPlatoModel] = formProvider()
  lazy val view: ItemDegreesPlatoView = app.injector.instanceOf[ItemDegreesPlatoView]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemDegreesPlatoController(
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

  "ItemDegreesPlato Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture() {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody((hasDegreesPlatoField, "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Fixture() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, itemDegreesPlatoSubmitAction(), Wine)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(defaultUserAnswers.set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(5))))
    ) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(ItemDegreesPlatoModel(hasDegreesPlato = true, Some(5))),
        itemDegreesPlatoSubmitAction(),
        Wine
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody((hasDegreesPlatoField, "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted (form provider validation fails)" in new Fixture() {
      val boundForm = form.bind(Map(hasDegreesPlatoField -> ""))
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody((hasDegreesPlatoField, "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, itemDegreesPlatoSubmitAction(), Wine)(dataRequest(request), messages(request)).toString
    }

    "must return a Bad Request and errors when invalid data is submitted (no brand name supplied)" in new Fixture() {
      val boundForm =
        form
          .bind(Map(hasDegreesPlatoField -> "true"))
          .withError(degreesPlatoField, ItemDegreesPlatoMessages.English.errorDegreesPlatoRequired)
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody((hasDegreesPlatoField, "true")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, itemDegreesPlatoSubmitAction(), Wine)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody((hasDegreesPlatoField, "false")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
