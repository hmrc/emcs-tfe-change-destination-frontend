/*
 * Copyright 2024 HM Revenue & Customs
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
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.routes
import forms.sections.consignee.ConsigneeExportInformationFormProvider
import mocks.services.MockUserAnswersService
import models.sections.consignee.ConsigneeExportInformation
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation, VatNumber}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.{ConsigneeExportEoriPage, ConsigneeExportInformationPage, ConsigneeExportVatPage}
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.consignee.ConsigneeExportInformationView

import scala.concurrent.Future

class ConsigneeExportInformationControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: ConsigneeExportInformationFormProvider = new ConsigneeExportInformationFormProvider()
  lazy val form: Form[Set[ConsigneeExportInformation]] = formProvider()
  lazy val view: ConsigneeExportInformationView = app.injector.instanceOf[ConsigneeExportInformationView]

  lazy val consigneeExportInformationRoute: Call =
    controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode)
  lazy val consigneeExportInformationRouteSubmit: Call =
    controllers.sections.consignee.routes.ConsigneeExportInformationController.onSubmit(testErn, testArc, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, consigneeExportInformationRoute.url)

    lazy val testController = new ConsigneeExportInformationController(
      messagesApi,
      mockUserAnswersService,
      new FakeConsigneeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(consigneeTrader = None)),
      formProvider,
      messagesControllerComponents,
      view
    )
  }

  "ConsigneeExportInformation Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, consigneeExportInformationRouteSubmit)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(
        emptyUserAnswers
          .set(ConsigneeExportInformationPage, Set(EoriNumber))
      )
    ) {

      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(Set(EoriNumber)),
        consigneeExportInformationRouteSubmit
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {
      MockUserAnswersService.set().returns(
        Future.successful(
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(EoriNumber))
        )
      )

      val req = FakeRequest(POST, consigneeExportInformationRouteSubmit.url).withFormUrlEncodedBody(
        ("value[]", EoriNumber.toString)
      )

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest(POST, consigneeExportInformationRouteSubmit.url).withFormUrlEncodedBody(("value[]", "invalid"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      val boundForm = form.bind(Map("value[0]" -> "invalid"))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, consigneeExportInformationRouteSubmit)(dataRequest(req), messages(req)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, consigneeExportInformationRouteSubmit.url).withFormUrlEncodedBody(("value", EoriNumber.toString))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must cleanse the VAT and EORI values when answering `No information`" in new Fixture(
      Some(emptyUserAnswers
        .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
        .set(ConsigneeExportVatPage, testVatNumber)
        .set(ConsigneeExportEoriPage, testEoriNumber)
      )) {

      val expectedAnswers = emptyUserAnswers
        .set(ConsigneeExportInformationPage, Set(NoInformation))

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val req = FakeRequest(POST, consigneeExportInformationRouteSubmit.url).withFormUrlEncodedBody(("value[]", NoInformation.toString))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
    }

    "must cleanse the VAT when removing VAT and leaving EORI" in new Fixture(
      Some(emptyUserAnswers
        .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
        .set(ConsigneeExportVatPage, testVatNumber)
        .set(ConsigneeExportEoriPage, testEoriNumber))
    ) {

      val expectedAnswers = emptyUserAnswers
        .set(ConsigneeExportInformationPage, Set(EoriNumber))
        .set(ConsigneeExportEoriPage, testEoriNumber)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val req = FakeRequest(POST, consigneeExportInformationRouteSubmit.url).withFormUrlEncodedBody(("value[]", EoriNumber.toString))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
    }

    "must cleanse the EORI when removing EORI and leaving VAT" in new Fixture(
      Some(emptyUserAnswers
        .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
        .set(ConsigneeExportVatPage, testVatNumber)
        .set(ConsigneeExportEoriPage, testEoriNumber))) {

      val expectedAnswers = emptyUserAnswers
        .set(ConsigneeExportInformationPage, Set(VatNumber))
        .set(ConsigneeExportVatPage, testVatNumber)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val req = FakeRequest(POST, consigneeExportInformationRouteSubmit.url).withFormUrlEncodedBody(("value[]", VatNumber.toString))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
    }
  }
}
