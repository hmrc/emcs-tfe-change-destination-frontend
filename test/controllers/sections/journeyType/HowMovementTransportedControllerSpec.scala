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

package controllers.sections.journeyType

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.routes
import forms.sections.journeyType.HowMovementTransportedFormProvider
import mocks.services.MockUserAnswersService
import models.response.emcsTfe.GetMovementResponse
import models.sections.ReviewAnswer.ChangeAnswers
import models.sections.info.movementScenario.MovementScenario
import models.sections.journeyType.HowMovementTransported
import models.sections.transportUnit.TransportUnitType.{Container, Tractor}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeJourneyTypeNavigator
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.{GiveInformationOtherTransportPage, HowMovementTransportedPage, JourneyTimeDaysPage, JourneyTypeReviewPage}
import pages.sections.transportUnit.{TransportUnitIdentityPage, TransportUnitTypePage, TransportUnitsSection}
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.journeyType.{HowMovementTransportedNoOptionView, HowMovementTransportedView}

import scala.concurrent.Future

class HowMovementTransportedControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: HowMovementTransportedFormProvider = new HowMovementTransportedFormProvider()
  lazy val form: Form[HowMovementTransported] = formProvider()
  lazy val view: HowMovementTransportedView = app.injector.instanceOf[HowMovementTransportedView]
  lazy val onlyFixedView: HowMovementTransportedNoOptionView = app.injector.instanceOf[HowMovementTransportedNoOptionView]

  class Test(val userAnswers: Option[UserAnswers], movementResponse: GetMovementResponse = maxGetMovementResponse) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new HowMovementTransportedController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      navigator = new FakeJourneyTypeNavigator(testOnwardRoute),
      auth = fakeAuthAction,
      getData = new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requireData = dataRequiredAction,
      withMovement = new FakeMovementAction(movementResponse),
      formProvider = formProvider,
      controllerComponents = Helpers.stubMessagesControllerComponents(),
      view = view,
      onlyFixedView = onlyFixedView,
      betaAllowList = fakeBetaAllowListAction
    )
  }

  "HowMovementTransported Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(HowMovementTransportedPage, HowMovementTransported.values.head)
    )) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(HowMovementTransported.values.head), NormalMode)(dataRequest(request), messages(request)).toString
    }

    Seq(
      MovementScenario.EuTaxWarehouse,
      MovementScenario.TemporaryRegisteredConsignee,
      MovementScenario.RegisteredConsignee,
      MovementScenario.DirectDelivery,
      MovementScenario.UnknownDestination,
      MovementScenario.ExemptedOrganisation
    ).foreach { scenario =>
      s"must return OK and the onlyFixedView when destination type is $scenario and destination type has not changed, and no guarantor exists" in new Test(
        userAnswers = Some(
          emptyUserAnswers.copy(ern = testNorthernIrelandErn)
            .set(DestinationTypePage, scenario)
        ),
        movementResponse = maxGetMovementResponse.copy(
          movementGuarantee = maxGetMovementResponse.movementGuarantee.copy(guarantorTrader = None),
          destinationType = scenario.destinationType
        )
      ) {
        val result = controller.onPageLoad(testNorthernIrelandErn, testArc, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual onlyFixedView(NormalMode)(dataRequest(request, ern = testNorthernIrelandErn), messages(request)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(emptyUserAnswers)) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must cleanse the journey section when changing the answer (retaining the review page answer)" in new Test(Some(
      emptyUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.Other)
        .set(GiveInformationOtherTransportPage, "blah")
        .set(JourneyTimeDaysPage, 1)
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportUnitIdentityPage(testIndex1), "Container1")
        .set(TransportUnitTypePage(testIndex2), Tractor)
        .set(TransportUnitIdentityPage(testIndex2), "Tractor")
        .set(JourneyTypeReviewPage, ChangeAnswers)
    )) {
      val expectedAnswers = emptyUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.values.head)
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportUnitIdentityPage(testIndex1), "Container1")
        .set(TransportUnitTypePage(testIndex2), Tractor)
        .set(TransportUnitIdentityPage(testIndex2), "Tractor")
        .set(JourneyTypeReviewPage, ChangeAnswers)

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must cleanse the journey and transport unit section when changing the answer (from fixed transport installations)" in new Test(Some(
      emptyUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.FixedTransportInstallations)
        .set(GiveInformationOtherTransportPage, "blah")
        .set(JourneyTimeDaysPage, 1)
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportUnitIdentityPage(testIndex1), "Container1")
        .set(TransportUnitTypePage(testIndex2), Tractor)
        .set(TransportUnitIdentityPage(testIndex2), "Tractor")
    )) {
      val expectedAnswers = emptyUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.values.head)
        .set(TransportUnitsSection, Json.obj())

      MockUserAnswersService.set(expectedAnswers).returns(Future.successful(expectedAnswers))

      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to next page when answer unchanged" in new Test(Some(
      emptyUserAnswers
        .set(HowMovementTransportedPage, HowMovementTransported.SeaTransport)
        .set(JourneyTimeDaysPage, 1)
    )) {
      val result = controller.onSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", HowMovementTransported.SeaTransport.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }
  }
}