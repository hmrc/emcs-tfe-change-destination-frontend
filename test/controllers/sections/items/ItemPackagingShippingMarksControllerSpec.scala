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
import forms.sections.items.ItemPackagingShippingMarksFormProvider
import mocks.services.MockUserAnswersService
import models.response.referenceData.ItemPackaging
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemPackagingProductTypePage, ItemPackagingShippingMarksPage, ItemSelectPackagingPage}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemPackagingShippingMarksView

import scala.concurrent.Future

class ItemPackagingShippingMarksControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  lazy val formProvider = new ItemPackagingShippingMarksFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[ItemPackagingShippingMarksView]
  val action: Call = controllers.sections.items.routes.ItemPackagingShippingMarksController.onSubmit(testErn, testDraftId, testIndex1,
    testPackagingIndex1, NormalMode)
  val skipLink: Call = controllers.sections.items.routes.ItemPackagingShippingMarksController.onNoShippingMarks(testErn, testDraftId, testIndex1,
    testPackagingIndex1, NormalMode)

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("AE", "Aerosol"))

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemPackagingShippingMarksController(
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

  "ItemPackagingShippingMarks Controller" - {

    "must redirect to Index of section when the packaging idx is outside of bounds for a GET" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the packaging idx is outside of bounds for a GET (no shipping marks)" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W300")
    )) {
      val result = controller.onNoShippingMarks(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the packaging idx is outside of bounds for a POST" in new Test(Some(
      emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)
    )) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the item idx is outside of bounds for a GET (no shipping marks - " +
      "which redirects to Items index)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onNoShippingMarks(testErn, testDraftId, testIndex2, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex2).url
    }

    "must redirect to Index of section when the item idx is outside of bounds for a GET (which redirects to Items index)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex2).url
    }

    "must redirect to Index of section when the item idx is outside of bounds for a POST (which redirects to Items index)" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex2).url
    }

    "must redirect to Index of section when Select Packaging Type is missing" in new Test(
      Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W300"))
    ) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must return OK and the correct view for a GET" in new Test(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, action, skipLink, "Aerosol")(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      baseUserAnswers.set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "answer")
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), action, skipLink, "Aerosol")(
        dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must clear down the shipping marks when onNoShippingMarks is called and redirect to the next page" +
      "setting he Packaging Product Type to 'Yes'" in new Test(Some(
      baseUserAnswers
        .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "answer")
    )) {
      MockUserAnswersService.set(
        baseUserAnswers.set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
      ).returns(Future.successful(
        baseUserAnswers.set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
      ))

      val result = controller.onNoShippingMarks(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(baseUserAnswers)) {

      MockUserAnswersService.set(
        baseUserAnswers.set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "answer")
      ).returns(Future.successful(
        baseUserAnswers.set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "answer")
      ))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(baseUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, action, skipLink, "Aerosol")(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a GET (no shipping marks) if no existing data is found" in new Test(None) {
      val result = controller.onNoShippingMarks(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
