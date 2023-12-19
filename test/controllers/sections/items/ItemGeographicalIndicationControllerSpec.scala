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
import forms.sections.items.ItemGeographicalIndicationFormProvider
import mocks.services.MockUserAnswersService
import models.GoodsType.Wine
import models.sections.items.ItemGeographicalIndicationType.ProtectedDesignationOfOrigin
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemGeographicalIndicationChoicePage, ItemGeographicalIndicationPage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemGeographicalIndicationView

import scala.concurrent.Future

class ItemGeographicalIndicationControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  //Ensures a dummy item exists in the array for testing
  val defaultUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)

  def itemGeographicalIndicationSubmitAction(idx: Index = testIndex1): Call =
    routes.ItemGeographicalIndicationController.onSubmit(testErn, testDraftId, idx, NormalMode)

  lazy val formProvider: ItemGeographicalIndicationFormProvider = new ItemGeographicalIndicationFormProvider()
  lazy val form: Form[String] = formProvider()
  lazy val view: ItemGeographicalIndicationView = app.injector.instanceOf[ItemGeographicalIndicationView]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemGeographicalIndicationController(
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

  "ItemGeographicalIndication Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture() {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "1")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return SEE OTHER and redirect when no Goods Type exists" in new Fixture(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Fixture() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, itemGeographicalIndicationSubmitAction(), Wine, ProtectedDesignationOfOrigin)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(
      defaultUserAnswers.set(ItemGeographicalIndicationPage(testIndex1), "foo")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill("foo"),
        itemGeographicalIndicationSubmitAction(),
        Wine,
        ProtectedDesignationOfOrigin
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "foo")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must render BadRequest when invalid data is submitted" in new Fixture() {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))
      val boundForm = form.bind(Map("value" -> ""))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        boundForm,
        itemGeographicalIndicationSubmitAction(),
        Wine,
        ProtectedDesignationOfOrigin
      )(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "foo")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
