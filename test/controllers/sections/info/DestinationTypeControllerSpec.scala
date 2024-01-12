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

package controllers.sections.info

import base.SpecBase
import controllers.actions.predraft.FakePreDraftRetrievalAction
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.info.DestinationTypeFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.NormalMode
import models.requests.DataRequest
import models.sections.info.DispatchPlace
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, ExportWithCustomsDeclarationLodgedInTheUk}
import navigation.FakeNavigators.FakeInfoNavigator
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.info.DestinationTypeView

import scala.concurrent.Future

class DestinationTypeControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService {

  lazy val formProvider: DestinationTypeFormProvider = new DestinationTypeFormProvider()
  lazy val view: DestinationTypeView = app.injector.instanceOf[DestinationTypeView]

  class Fixture(
                 dispatchPlace: Option[DispatchPlace],
                 ern: String = testGreatBritainErn,
                 value: String = "unknownDestination"
               ) {

    val userAnswersSoFar = dispatchPlace match {
      case Some(place) => emptyUserAnswers.copy(ern = ern).set(DispatchPlacePage, place)
      case None => emptyUserAnswers.copy(ern = ern)
    }

    lazy val destinationTypeGetRoute: String = controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(ern, testArc).url
    lazy val destinationTypePostRoute: String = controllers.sections.info.routes.DestinationTypeController.onPreDraftSubmit(ern, testArc).url

    implicit lazy val getRequest: DataRequest[AnyContentAsEmpty.type] =
      dataRequest(FakeRequest(GET, destinationTypeGetRoute), ern = ern)

    implicit lazy val postRequest: DataRequest[AnyContentAsFormUrlEncoded] =
      dataRequest(FakeRequest(POST, destinationTypePostRoute).withFormUrlEncodedBody(("value", value)), ern = ern)

    lazy val form: Form[MovementScenario] = formProvider()(getRequest)

    lazy val controller = new DestinationTypeController(
      messagesApi,
      mockPreDraftService,
      new FakeInfoNavigator(testOnwardRoute),
      mockUserAnswersService,
      fakeAuthAction,
      new FakePreDraftRetrievalAction(Some(userAnswersSoFar), Some(testMinTraderKnownFacts)),
      preDraftDataRequiredAction,
      new FakeDataRetrievalAction(Some(userAnswersSoFar), Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      fakeUserAllowListAction
    )

    lazy val getResult: Future[Result] = controller.onPreDraftPageLoad(ern, testArc)(getRequest)
    lazy val postResult: Future[Result] = controller.onPreDraftSubmit(ern, testArc)(postRequest)
  }

  "DestinationTypeController" - {
    "onPreDraftPageLoad" - {
      "must render the view" - {
        "when the request contains a GBRC ERN" in new Fixture(None, ern = "GBRC123") {
          status(getResult) mustEqual OK
          contentAsString(getResult) mustEqual
            view(
              GreatBritain,
              form,
              controllers.sections.info.routes.DestinationTypeController.onPreDraftSubmit("GBRC123", testArc)
            )(getRequest, messages(getRequest)).toString
        }
        "when the request contains a GBWK ERN" in new Fixture(None, ern = "GBWK123") {
          status(getResult) mustEqual OK
          contentAsString(getResult) mustEqual
            view(
              GreatBritain,
              form,
              controllers.sections.info.routes.DestinationTypeController.onPreDraftSubmit("GBWK123", testArc)
            )(getRequest, messages(getRequest)).toString
        }
        "when the request contains a XIRC ERN" in new Fixture(None, ern = "XIRC123") { // TODO fix/remove test
          status(getResult) mustEqual OK
          contentAsString(getResult) mustEqual
            view(
              GreatBritain,
              form.fill(EuTaxWarehouse),
              controllers.sections.info.routes.DestinationTypeController.onPreDraftSubmit("XIRC123", testArc)
            )(getRequest, messages(getRequest)).toString
        }
        "when the request contains a XIWK ERN and dispatchPlace is GreatBritain" in new Fixture(dispatchPlace = Some(GreatBritain), ern = "XIWK123") {
          status(getResult) mustEqual OK
          contentAsString(getResult) mustEqual
            view(
              GreatBritain,
              form,
              controllers.sections.info.routes.DestinationTypeController.onPreDraftSubmit("XIWK123", testArc)
            )(getRequest, messages(getRequest)).toString
        }
        "when the request contains a XIWK ERN and dispatchPlace is NorthernIreland" in new Fixture(dispatchPlace = Some(NorthernIreland), ern = "XIWK123") {
          status(getResult) mustEqual OK
          contentAsString(getResult) mustEqual
            view(
              NorthernIreland,
              form.fill(EuTaxWarehouse),
              controllers.sections.info.routes.DestinationTypeController.onPreDraftSubmit("XIWK123", testArc)
            )(getRequest, messages(getRequest)).toString
        }
      }
      "must redirect to the DispatchPlace page" - {
        "when the request contains a XI ERN and dispatchPlace is not known" in new Fixture(None, ern = "XIWK123") {
          status(getResult) mustEqual SEE_OTHER
          redirectLocation(getResult).value mustEqual controllers.sections.info.routes.DispatchPlaceController.onPreDraftPageLoad("XIWK123", testArc, NormalMode).url
        }
      }
    }

    "onPreDraftSubmit" - {
      "must redirect to the next page when valid data is submitted" in new Fixture(None, value = "exportWithCustomsDeclarationLodgedInTheUk") {
        val expectedAnswersToSave = emptyUserAnswers.copy(ern = testGreatBritainErn).set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)

        MockUserAnswersService.set(expectedAnswersToSave).returns(Future.successful(expectedAnswersToSave))
        MockPreDraftService.clear(testGreatBritainErn, testArc).returns(Future.successful(true))

        status(postResult) mustEqual SEE_OTHER
        redirectLocation(postResult).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(None, value = "") {
        val boundForm = form.bind(Map("value" -> ""))

        status(postResult) mustEqual BAD_REQUEST
        contentAsString(postResult) mustEqual
          view(
            GreatBritain,
            boundForm,
            controllers.sections.info.routes.DestinationTypeController.onPreDraftSubmit(testGreatBritainErn, testArc)
          )(postRequest, messages(postRequest)).toString
      }
    }
  }
}
