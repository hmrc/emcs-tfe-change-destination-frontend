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

package controllers.sections.guarantor

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.guarantor.GuarantorRequiredFormProvider
import mocks.services.MockUserAnswersService
import models.requests.DataRequest
import models.response.emcsTfe.{GetMovementResponse, GuarantorType, MovementGuaranteeModel}
import models.sections.info.movementScenario.MovementScenario.{DirectDelivery, ExportWithCustomsDeclarationLodgedInTheUk}
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.guarantor.{GuarantorRequiredQuestionView, GuarantorRequiredView}

import scala.concurrent.Future

class GuarantorRequiredControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val view: GuarantorRequiredView = app.injector.instanceOf[GuarantorRequiredView]
  lazy val questionView: GuarantorRequiredQuestionView = app.injector.instanceOf[GuarantorRequiredQuestionView]
  lazy val formProvider: GuarantorRequiredFormProvider = app.injector.instanceOf[GuarantorRequiredFormProvider]

  def guarantorRequiredRoute(ern: String): String = routes.GuarantorRequiredController.onPageLoad(ern, testArc, NormalMode).url

  class Fixture(val optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers),
                val movementResponse: GetMovementResponse = maxGetMovementResponse,
                val ern: String = testErn) {

    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, guarantorRequiredRoute(ern))
    implicit val dr: DataRequest[AnyContentAsEmpty.type] = dataRequest(request, ern = ern)
    implicit val msgs: Messages = messages(request)

    lazy val testController = new GuarantorRequiredController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      navigator = new FakeGuarantorNavigator(testOnwardRoute),
      auth = fakeAuthAction,
      getData = new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      requireData = dataRequiredAction,
      withMovement = new FakeMovementAction(movementResponse),
      controllerComponents = messagesControllerComponents,
      formProvider = formProvider,
      view = view,
      questionView = questionView
    )
  }

  "GET onPageLoad" - {

    "when User answers exist" - {

      "when guarantor NOT required (NI to EU, Energy, Fixed Transport)" - {

        "must return OK and the correct view (view is populated from IE801 answer)" in new Fixture(
          optUserAnswers = Some(
            emptyUserAnswers
              .set(DestinationTypePage, DirectDelivery)
              .set(HowMovementTransportedPage, FixedTransportInstallations)
          ),
          movementResponse = maxGetMovementResponse.copy(
            items = maxGetMovementResponse.items.map(_.copy(productCode = "E500"))
          ),
          ern = testNorthernIrelandWarehouseKeeperErn
        ) {

          val result = testController.onPageLoad(ern, testArc, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual questionView(formProvider().fill(true), routes.GuarantorRequiredController.onSubmit(ern, testArc, NormalMode)).toString
        }
      }

      "when a new guarantor is required (UK to UK, Consignee guarantor, Changed to Export)" - {

        "must return OK and the correct view" in new Fixture(
          optUserAnswers = Some(emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)),
          movementResponse = maxGetMovementResponse.copy(
            movementGuarantee = MovementGuaranteeModel(GuarantorType.Consignee, None)
          ),
          ern = testGreatBritainErn
        ) {

          val result = testController.onPageLoad(ern, testArc, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            onwardRoute = routes.GuarantorRequiredController.enterGuarantorDetails(ern, testArc),
            newGuarantorIsRequired = true
          ).toString
        }
      }

      "when guarantor required (NI to EU, Not Fixed Transport Installation)" - {

        "must return OK and the correct view" in new Fixture(Some(emptyUserAnswers
          .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
        )) {

          val result = testController.onPageLoad(ern, testArc, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            onwardRoute = routes.GuarantorRequiredController.enterGuarantorDetails(ern, testArc),
            newGuarantorIsRequired = false
          ).toString
        }
      }
    }

    "when UserAnswers don't exist" - {

      "must redirect to Journey Recovery" in new Fixture(None) {

        val result = testController.onPageLoad(ern, testArc, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }

  "POST onSubmit" - {

    "when valid data is submitted and the answer is different (saving the answer)" - {

      "must return SEE_OTHER and redirect to the onward route" in new Fixture(
        optUserAnswers = Some(
          emptyUserAnswers
            .set(DestinationTypePage, DirectDelivery)
            .set(HowMovementTransportedPage, FixedTransportInstallations)
        ),
        movementResponse = maxGetMovementResponse.copy(
          items = maxGetMovementResponse.items.map(_.copy(productCode = "E500")),
          movementGuarantee = MovementGuaranteeModel(GuarantorType.NoGuarantor, None)
        ),
        ern = testNorthernIrelandWarehouseKeeperErn
      ) {

        val savedAnswers = optUserAnswers.get.set(GuarantorRequiredPage, true)

        MockUserAnswersService.set(savedAnswers).returns(Future.successful(savedAnswers))

        val req = request.withFormUrlEncodedBody("value" -> "true")
        val result = testController.onSubmit(ern, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(testOnwardRoute.url)
      }
    }

    "when valid data is submitted and the answer is NOT different (do not save the answer)" - {

      "must return SEE_OTHER and redirect to the onward route" in new Fixture(
        optUserAnswers = Some(
          emptyUserAnswers
            .set(DestinationTypePage, DirectDelivery)
            .set(HowMovementTransportedPage, FixedTransportInstallations)
        ),
        movementResponse = maxGetMovementResponse.copy(
          items = maxGetMovementResponse.items.map(_.copy(productCode = "E500"))
        ),
        ern = testNorthernIrelandWarehouseKeeperErn
      ) {

        MockUserAnswersService.set().never()

        val req = request.withFormUrlEncodedBody("value" -> "true")
        val result = testController.onSubmit(ern, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(testOnwardRoute.url)
      }
    }

    "when invalid data is submitted" - {

      "must return BAD_REQUEST and render the form with error" in new Fixture(
        optUserAnswers = Some(
          emptyUserAnswers
            .set(DestinationTypePage, DirectDelivery)
            .set(HowMovementTransportedPage, FixedTransportInstallations)
        ),
        movementResponse = maxGetMovementResponse.copy(items = maxGetMovementResponse.items.map(_.copy(productCode = "E500"))),
        ern = testNorthernIrelandWarehouseKeeperErn
      ) {

        val req = request.withFormUrlEncodedBody("value" -> "")

        val result = testController.onSubmit(ern, testArc, NormalMode)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual questionView(
          formProvider().withError("value", "guarantorRequired.error.required"),
          routes.GuarantorRequiredController.onSubmit(ern, testArc, NormalMode)
        ).toString
      }
    }
  }

  "GET enterGuarantorDetails" - {

    "when User answers exist" - {

      "must return SEE_OTHER and redirect to the onward route (removing the GuarantorRequired answer)" in new Fixture(Some(
        emptyUserAnswers.set(GuarantorRequiredPage, false)
      )) {

        val savedAnswers = emptyUserAnswers.remove(GuarantorRequiredPage)

        MockUserAnswersService.set(savedAnswers).returns(Future.successful(savedAnswers))

        val result = testController.enterGuarantorDetails(ern, testArc, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "when UserAnswers don't exist" - {

      "must redirect to Journey Recovery" in new Fixture(None) {

        val result = testController.enterGuarantorDetails(ern, testArc, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
