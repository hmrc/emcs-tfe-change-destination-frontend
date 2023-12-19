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
import forms.sections.items.ItemGeographicalIndicationChoiceFormProvider
import mocks.services.MockUserAnswersService
import models.GoodsType.Beer
import models.sections.items.ItemGeographicalIndicationType
import models.sections.items.ItemGeographicalIndicationType.{NoGeographicalIndication, ProtectedDesignationOfOrigin, ProtectedGeographicalIndication}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemGeographicalIndicationChoicePage, ItemGeographicalIndicationPage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemGeographicalIndicationChoiceView

import scala.concurrent.Future

class ItemGeographicalIndicationChoiceControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: ItemGeographicalIndicationChoiceFormProvider = new ItemGeographicalIndicationChoiceFormProvider()
  lazy val form: Form[ItemGeographicalIndicationType] = formProvider()
  lazy val view: ItemGeographicalIndicationChoiceView = app.injector.instanceOf[ItemGeographicalIndicationChoiceView]

  val baseUserAnswers: UserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000")

  val action: Call = controllers.sections.items.routes.ItemGeographicalIndicationChoiceController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemGeographicalIndicationChoiceController(
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

  "ItemGeographicalIndicationChoice Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Test(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Test(Some(baseUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "PGI")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when Excise Product Code is missing" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Test(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, action, Beer)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      baseUserAnswers.set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedDesignationOfOrigin)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(ProtectedDesignationOfOrigin), action, Beer)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" - {
      "when the answer has changed" in new Test(Some(
        baseUserAnswers
          .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
          .set(ItemGeographicalIndicationPage(testIndex1), "beans")
      )) {

        MockUserAnswersService.set(
          baseUserAnswers.set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedGeographicalIndication)
        ).returns(Future.successful(baseUserAnswers))

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "PGI")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
      "when the answer has not changed" in new Test(Some(
        baseUserAnswers
          .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedGeographicalIndication)
          .set(ItemGeographicalIndicationPage(testIndex1), "beans")
      )) {

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "PGI")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
      "when the answer is new" in new Test(Some(baseUserAnswers)) {

        MockUserAnswersService.set(
          baseUserAnswers.set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedGeographicalIndication)
        ).returns(Future.successful(baseUserAnswers))

        val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "PGI")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(baseUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, action, Beer)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "PGI")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
