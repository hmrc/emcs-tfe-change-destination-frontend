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

package controllers.sections.importInformation

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.sections.importInformation.ImportCustomsOfficeCodeFormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, NorthernIrelandRegisteredConsignor, UserAnswers}
import navigation.FakeNavigators.FakeImportInformationNavigator
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.importInformation.ImportCustomsOfficeCodeView

import scala.concurrent.Future

class ImportCustomsOfficeCodeControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: ImportCustomsOfficeCodeFormProvider = new ImportCustomsOfficeCodeFormProvider()
  lazy val form: Form[String] = formProvider()
  lazy val view: ImportCustomsOfficeCodeView = app.injector.instanceOf[ImportCustomsOfficeCodeView]

  lazy val importCustomsOfficeSubmitAction: Call =
    controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onSubmit(testErn, testDraftId, NormalMode)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ImportCustomsOfficeCodeController(
      messagesApi,
      fakeAuthAction,
      fakeUserAllowListAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeImportInformationNavigator(testOnwardRoute),
      mockUserAnswersService,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "Import Customs Office Code Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form, importCustomsOfficeSubmitAction, NorthernIrelandRegisteredConsignor)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(ImportCustomsOfficeCodePage, "answer"))
    ) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), importCustomsOfficeSubmitAction, NorthernIrelandRegisteredConsignor)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "AB123456")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val boundForm = form.bind(Map("value" -> ""))
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, importCustomsOfficeSubmitAction, NorthernIrelandRegisteredConsignor)(dataRequest(request), messages(request)).toString
    }

    "must redirect to tasklist for GET if ERN is not XIRC or GBRC" in new Fixture() {
      Seq("GB", "XI").foreach {
        prefix =>
          val ern = s"${prefix}WK123"
          val result = controller.onPageLoad(ern, testDraftId, NormalMode)(request)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.routes.DraftMovementController.onPageLoad(ern, testDraftId).url)
      }
    }

    "must redirect to tasklist for POST if ERN is not XIRC or GBRC" in new Fixture() {
      Seq("GB", "XI").foreach {
        prefix =>
          val ern = s"${prefix}WK123"
          val result = controller.onSubmit(ern, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "AB123456")))

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.routes.DraftMovementController.onPageLoad(ern, testDraftId).url)
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(("value", "AB123456")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
