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
import forms.sections.items.ItemExciseProductCodeFormProvider
import mocks.services.{MockGetExciseProductCodesService, MockUserAnswersService}
import models.{ExciseProductCode, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.ItemExciseProductCodePage
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.Aliases.SelectItem
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemExciseProductCodeView

import scala.concurrent.Future

class ItemExciseProductCodeControllerSpec extends SpecBase
  with MockUserAnswersService
  with MockGetExciseProductCodesService
  with ItemFixtures {

  val action: Call = controllers.sections.items.routes.ItemExciseProductCodeController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  val sampleEPCs: Seq[ExciseProductCode] = Seq(beerExciseProductCode, wineExciseProductCode)

  lazy val formProvider: ItemExciseProductCodeFormProvider = new ItemExciseProductCodeFormProvider()
  lazy val form: Form[String] = formProvider.apply(sampleEPCs)
  lazy val view: ItemExciseProductCodeView = app.injector.instanceOf[ItemExciseProductCodeView]

  class Fixture(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemExciseProductCodeController(
      messagesApi,
      mockUserAnswersService,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      mockGetExciseProductCodesService,
      view
    )

    val sampleEPCsSelectOptions: Seq[SelectItem] = SelectItemHelper.constructSelectItems(
      selectOptions = sampleEPCs,
      defaultTextMessageKey = "itemExciseProductCode.select.defaultValue")(messages(FakeRequest()))
  }

  "ItemExciseProductCode Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture(Some(emptyUserAnswers)) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", "W200")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
    }

    "must return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {

      MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, action, sampleEPCsSelectOptions)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "B000"))
    ) {

      MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

      val sampleEPCsSelectOptionsWithBeerSelected = SelectItemHelper.constructSelectItems(
        selectOptions = sampleEPCs,
        defaultTextMessageKey = "itemExciseProductCode.select.defaultValue",
        existingAnswer = Some("B000"))(messages(request))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill("B000"), action, sampleEPCsSelectOptionsWithBeerSelected)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "when valid data is submitted" - {
      "must redirect to the next page" - {
        "when there was no previous answer" in new Fixture(Some(emptyUserAnswers)) {
          MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

          MockUserAnswersService.set(
            emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          ).returns(Future.successful(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)))

          val result: Future[Result] =
            controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
        "when the previous answer is the same as the new answer" in new Fixture(Some(
          emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)
        )) {
          MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

          val result: Future[Result] =
            controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
        "when the previous answer is different to the new answer" in new Fixture(Some(
          emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
        )) {
          MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

          MockUserAnswersService.set(
            emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)
          ).returns(Future.successful(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)))

          val result: Future[Result] =
            controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
        "and only clear down the current item's answers when the previous answer is different to the new answer" in new Fixture(Some(
          emptyUserAnswers
            .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
            .set(ItemExciseProductCodePage(testIndex2), testExciseProductCodeB000.code)
        )) {
          MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

          MockUserAnswersService.set(
            emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
              .set(ItemExciseProductCodePage(testIndex2), testExciseProductCodeB000.code)
          ).returns(Future.successful(
            emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
              .set(ItemExciseProductCodePage(testIndex2), testExciseProductCodeB000.code)
          ))

          val result: Future[Result] =
            controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

      MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

      val boundForm = form.bind(Map("excise-product-code" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual
        view(boundForm, action, sampleEPCsSelectOptions)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
