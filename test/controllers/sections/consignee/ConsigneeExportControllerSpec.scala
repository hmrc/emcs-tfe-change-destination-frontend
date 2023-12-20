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

package controllers.sections.consignee

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import forms.sections.consignee.ConsigneeExportFormProvider
import mocks.services.MockUserAnswersService
import models.sections.consignee.{ConsigneeExportVat, ConsigneeExportVatType}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.{ConsigneeExportPage, ConsigneeExportVatPage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.consignee.ConsigneeExportView

import scala.concurrent.Future

class ConsigneeExportControllerSpec extends SpecBase with MockUserAnswersService {

  val userAnswers: UserAnswers = emptyUserAnswers.set(ConsigneeExportPage, true)

  lazy val formProvider: ConsigneeExportFormProvider = new ConsigneeExportFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: ConsigneeExportView = app.injector.instanceOf[ConsigneeExportView]

  lazy val consigneeExportRoute: String =
    controllers.sections.consignee.routes.ConsigneeExportController.onPageLoad(testErn, testLrn, NormalMode).url
  lazy val consigneeExportRouteSubmit: String =
    controllers.sections.consignee.routes.ConsigneeExportController.onSubmit(testErn, testLrn, NormalMode).url

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, consigneeExportRoute)

    lazy val testController = new ConsigneeExportController(
      messagesApi,
      mockUserAnswersService,
      new FakeConsigneeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      messagesControllerComponents,
      view
    )
  }


  "ConsigneeExport Controller" - {
    "onPageLoad" - {
      "must return OK and the correct view for a GET" in new Fixture() {
        val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(request)).toString
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(userAnswers)) {
        val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(dataRequest(request), messages(request)).toString
      }
    }

    "onSubmit" - {

      "must redirect to the next page when valid data is submitted - data is new" in new Fixture() {
        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val req = FakeRequest(POST, consigneeExportRouteSubmit).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      val userAnswersChanged = emptyUserAnswers
        .set(ConsigneeExportPage, true)
        .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.No, None, None))

      "must redirect to the next page when valid data is submitted - data has changed" in new Fixture(Some(userAnswersChanged)) {
        MockUserAnswersService.set(emptyUserAnswers.set(ConsigneeExportPage, false)).returns(Future.successful(emptyUserAnswers))

        val req = FakeRequest(POST, consigneeExportRouteSubmit).withFormUrlEncodedBody(("value", "false"))

        val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page when valid data is submitted - data has not changed" in new Fixture(Some(userAnswers)) {
        val req = FakeRequest(POST, consigneeExportRouteSubmit).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
        val req = FakeRequest(POST, consigneeExportRouteSubmit).withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(req), messages(req)).toString
      }

    }

    "must redirect to Journey Recovery" - {

      "for a GET if no existing data is found" in new Fixture(None) {
        val result = testController.onPageLoad(testErn, testDraftId, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }

      "for a POST if no existing data is found" in new Fixture(None) {
        val req = FakeRequest(POST, consigneeExportRouteSubmit).withFormUrlEncodedBody(("value", "true"))

        val result = testController.onSubmit(testErn, testDraftId, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

  }
}
