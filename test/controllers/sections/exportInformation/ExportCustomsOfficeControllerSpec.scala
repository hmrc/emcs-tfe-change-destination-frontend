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

package controllers.sections.exportInformation

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.exportInformation.ExportCustomsOfficeFormProvider
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeExportInformationNavigator
import pages.sections.exportInformation.ExportCustomsOfficePage
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.exportInformation.ExportCustomsOfficeView

import scala.concurrent.Future

class ExportCustomsOfficeControllerSpec extends SpecBase with MockUserAnswersService {

  val defaultUserAnswers: UserAnswers = emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheEu)

  lazy val formProvider: ExportCustomsOfficeFormProvider = new ExportCustomsOfficeFormProvider()
  lazy val form: Form[String] = formProvider()
  lazy val view: ExportCustomsOfficeView = app.injector.instanceOf[ExportCustomsOfficeView]

  lazy val exportCustomsOfficeRoute: String = routes.ExportCustomsOfficeController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val exportCustomsOfficeSubmitAction: Call = routes.ExportCustomsOfficeController.onSubmit(testErn, testArc, NormalMode)


  class Fixture(optUserAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    val request = FakeRequest(GET, exportCustomsOfficeRoute)

    lazy val testController = new ExportCustomsOfficeController(
      messagesApi,
      mockUserAnswersService,
      new FakeExportInformationNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(deliveryPlaceCustomsOfficeReferenceNumber = None)),
      fakeBetaAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "ExportCustomsOffice Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, exportCustomsOfficeSubmitAction, euExport = true)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(defaultUserAnswers.set(ExportCustomsOfficePage, "answer"))
    ) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), exportCustomsOfficeSubmitAction, euExport = true)(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val req = FakeRequest(POST, exportCustomsOfficeRoute).withFormUrlEncodedBody(("value", "AB123456"))
      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest(POST, exportCustomsOfficeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, exportCustomsOfficeSubmitAction, euExport = true)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, exportCustomsOfficeRoute).withFormUrlEncodedBody(("value", "answer"))
      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
