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
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.routes
import forms.sections.consignee.ConsigneeExciseFormProvider
import mocks.services.{MockGetMemberStatesService, MockUserAnswersService}
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, TemporaryCertifiedConsignee, TemporaryRegisteredConsignee, UkTaxWarehouse}
import models.sections.info.movementScenario.OriginType.TaxWarehouse
import models.{CountryModel, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.{ConsigneeAddressPage, ConsigneeExcisePage}
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.consignee.ConsigneeExciseView

import scala.concurrent.Future

class ConsigneeExciseControllerSpec extends SpecBase
  with MockUserAnswersService
  with MockGetMemberStatesService {

  lazy val formProvider: ConsigneeExciseFormProvider = new ConsigneeExciseFormProvider()
  lazy val view: ConsigneeExciseView = app.injector.instanceOf[ConsigneeExciseView]

  lazy val consigneeExciseRoute: String =
    controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val consigneeExciseSubmit: Call =
    controllers.sections.consignee.routes.ConsigneeExciseController.onSubmit(testErn, testArc, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    val request = FakeRequest(GET, consigneeExciseRoute)

    implicit val dr: DataRequest[_] = dataRequest(request, optUserAnswers.getOrElse(emptyUserAnswers))

    lazy val form: Form[String] = formProvider(None)

    lazy val testController = new ConsigneeExciseController(
      messagesApi,
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(consigneeTrader = None)),
      new FakeConsigneeNavigator(testOnwardRoute),
      mockUserAnswersService,
      formProvider,
      messagesControllerComponents,
      mockGetMemberStatesService,
      view
    )

  }

  val userAnswersWithConsigneeExcise: UserAnswers = emptyUserAnswers
    .set(ConsigneeExcisePage, testErn)
    .set(DestinationTypePage, UkTaxWarehouse.GB)
  val userAnswersWithDestinationTypeTemporaryRegisteredConsignee: UserAnswers = emptyUserAnswers.set(DestinationTypePage, TemporaryRegisteredConsignee)
  val userAnswersWithDestinationTypeTemporaryCertifiedConsignee: UserAnswers = emptyUserAnswers.set(DestinationTypePage, TemporaryCertifiedConsignee)
  val userAnswersWithDestinationTypeEU: UserAnswers = emptyUserAnswers.set(DestinationTypePage, EuTaxWarehouse)

  "ConsigneeExciseController Controller" - {
    "must return OK and the correct view for a GET" - {

      "when Destination type is EuTaxWarehouse (retrieving EU member state country codes)" in
        new Fixture(Some(userAnswersWithDestinationTypeEU)) {

          MockGetMemberStatesService.getEuMemberStates().returns(Future.successful(Seq(CountryModel("FR", "France"))))

          override lazy val form = formProvider(Some(Seq(CountryModel("FR", "France"))))

          val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form,
            consigneeExciseSubmit,
            isNorthernIrishTemporaryRegisteredConsignee = false,
            isNorthernIrishTemporaryCertifiedConsignee = false
          )(dataRequest(request), messages(request)).toString
        }

      "when Destination type is TemporaryRegisteredConsignee and Northern Irish" in
        new Fixture(Some(userAnswersWithDestinationTypeTemporaryRegisteredConsignee)) {
          val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form,
            consigneeExciseSubmit,
            isNorthernIrishTemporaryRegisteredConsignee = true,
            isNorthernIrishTemporaryCertifiedConsignee = false
          )(dataRequest(request), messages(request)).toString
        }

      "when Destination type is TemporaryCertifiedConsignee and Northern Irish" in
        new Fixture(Some(userAnswersWithDestinationTypeTemporaryCertifiedConsignee)) {
          val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            form,
            consigneeExciseSubmit,
            isNorthernIrishTemporaryRegisteredConsignee = false,
            isNorthernIrishTemporaryCertifiedConsignee = true
          )(dataRequest(request), messages(request)).toString
        }

      "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(Some(userAnswersWithConsigneeExcise)) {
        val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

        override lazy val form = formProvider(None)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(testErn),
          consigneeExciseSubmit,
          isNorthernIrishTemporaryRegisteredConsignee = false,
          isNorthernIrishTemporaryCertifiedConsignee = false
        )(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Fixture(Some(userAnswersWithConsigneeExcise)) {

      val expectedSavedAnswers = userAnswersWithConsigneeExcise.set(ConsigneeExcisePage, testGreatBritainWarehouseErn)

      MockUserAnswersService.set(expectedSavedAnswers).returns(Future.successful(expectedSavedAnswers))

      val req = FakeRequest(POST, consigneeExciseSubmit.url).withFormUrlEncodedBody(("value", testGreatBritainWarehouseErn))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(userAnswersWithConsigneeExcise)) {
      val req = FakeRequest(POST, consigneeExciseSubmit.url).withFormUrlEncodedBody(("value", ""))

      override lazy val form = formProvider(None)

      val boundForm = form.bind(Map("value" -> ""))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        boundForm,
        consigneeExciseSubmit,
        isNorthernIrishTemporaryRegisteredConsignee = false,
        isNorthernIrishTemporaryCertifiedConsignee = false
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, consigneeExciseSubmit.url).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }

}
