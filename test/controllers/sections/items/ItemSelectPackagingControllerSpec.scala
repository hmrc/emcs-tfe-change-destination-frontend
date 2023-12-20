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
import forms.sections.items.ItemSelectPackagingFormProvider
import mocks.services.{MockGetPackagingTypesService, MockUserAnswersService}
import models.GoodsType.Wine
import models.response.referenceData.ItemPackaging
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemSelectPackagingPage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemSelectPackagingView

import scala.concurrent.Future

class ItemSelectPackagingControllerSpec extends SpecBase
  with MockUserAnswersService
  with MockGetPackagingTypesService
  with ItemFixtures {

  val formProvider = new ItemSelectPackagingFormProvider()
  lazy val view: ItemSelectPackagingView = app.injector.instanceOf[ItemSelectPackagingView]

  val action: Call = controllers.sections.items.routes.ItemSelectPackagingController.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)

  val baseUserAnswers: UserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)

  class Test(val userAnswers: Option[UserAnswers], callsService: Boolean = false) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    if (callsService) {
      MockGetPackagingTypesService.getItemPackagingTypes().returns(Future.successful(testItemPackagingTypes))
    }

    lazy val form: Form[ItemPackaging] = formProvider.apply(Wine, Seq.empty)(messages(request))

    lazy val controller = new ItemSelectPackagingController(
      messagesApi,
      mockUserAnswersService,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      mockGetPackagingTypesService,
      view
    )

    val sampleEPCsSelectOptions: Seq[SelectItem] = SelectItemHelper.constructSelectItems(
      selectOptions = testItemPackagingTypes,
      defaultTextMessageKey = "itemSelectPackaging.select.defaultValue")(messages(FakeRequest()))
  }

  "ItemSelectPackaging Controller" - {

    "must redirect to Index of section when the packaging idx is outside of bounds for a GET" in new Test(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the packaging idx is outside of bounds for a POST" in new Test(Some(baseUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "AE")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1).url
    }

    "must redirect to Index of section when the item idx is outside of bounds for a GET" in new Test(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the item idx is outside of bounds for a POST" in new Test(Some(baseUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "AE")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when Excise Product Code is missing" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Test(Some(baseUserAnswers), callsService = true) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, action, sampleEPCsSelectOptions, Wine)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      baseUserAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("AE", "Aerosol"))
    ), callsService = true) {

      val sampleEPCsSelectOptionsWithAerosolSelected: Seq[SelectItem] = SelectItemHelper.constructSelectItems(
        selectOptions = testItemPackagingTypes,
        defaultTextMessageKey = "itemSelectPackaging.select.defaultValue",
        existingAnswer = Some("AE"))(messages(request))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(ItemPackaging("AE", "Aerosol")), action, sampleEPCsSelectOptionsWithAerosolSelected,
        Wine)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(baseUserAnswers), callsService = true) {

      MockUserAnswersService.set(
        baseUserAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("AE", "Aerosol"))
      ).returns(Future.successful(
        baseUserAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("AE", "Aerosol"))
      ))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("packaging", "AE")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(baseUserAnswers), callsService = true) {
      val boundForm = form.bind(Map("packaging" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("packaging", "")))
      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, action, sampleEPCsSelectOptions,
        Wine)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)(request.withFormUrlEncodedBody(("packaging", "AE")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
