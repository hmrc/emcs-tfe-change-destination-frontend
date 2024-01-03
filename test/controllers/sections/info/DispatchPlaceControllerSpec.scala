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
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import controllers.actions.predraft.FakePreDraftRetrievalAction
import forms.sections.info.DispatchPlaceFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.sections.info.DispatchPlace
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeInfoNavigator
import pages.sections.info.DispatchPlacePage
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.info.DispatchPlaceView

import scala.concurrent.Future

class DispatchPlaceControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService {

  lazy val formProvider: DispatchPlaceFormProvider = new DispatchPlaceFormProvider()
  lazy val form: Form[DispatchPlace] = formProvider()
  lazy val view: DispatchPlaceView = app.injector.instanceOf[DispatchPlaceView]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new DispatchPlaceController(
      messagesApi,
      mockPreDraftService,
      mockUserAnswersService,
      new FakeInfoNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakePreDraftRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      preDraftDataRequiredAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      fakeUserAllowListAction
    )
  }

  val northernIrelandUserAnswers: UserAnswers = UserAnswers(testNorthernIrelandErn, testArc)
  val greatBritainUserAnswers: UserAnswers = UserAnswers(testGreatBritainErn, testArc)

  "DispatchPlace Controller" - {

    ".onPreDraftPageLoad()" - {

      "with a Northern Ireland ERN" - {
        lazy val dispatchPlaceSubmitAction = controllers.sections.info.routes.DispatchPlaceController.onPreDraftSubmit(testNorthernIrelandErn, testArc, NormalMode)

        "must return OK and the correct view for a GET" in new Fixture(userAnswers = Some(northernIrelandUserAnswers)) {
          val result = controller.onPreDraftPageLoad(testNorthernIrelandErn, testArc, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, dispatchPlaceSubmitAction)(dataRequest(request), messages(request)).toString
        }
      }

      "with a Great Britain ERN" - {
        lazy val destinationTypeRoute = controllers.sections.info.routes.DestinationTypeController.onPreDraftSubmit(testGreatBritainErn, testArc, NormalMode).url

        "must redirect to the destination type page (CAM-INFO08)" in new Fixture(userAnswers = Some(greatBritainUserAnswers)) {
          val result = controller.onPreDraftPageLoad(testGreatBritainErn, testArc, NormalMode)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(destinationTypeRoute)
        }
      }
    }

    ".onPreDraftSubmit()" - {

      "with a Northern Ireland ERN" - {
        lazy val dispatchPlaceSubmitAction = controllers.sections.info.routes.DispatchPlaceController.onPreDraftSubmit(testNorthernIrelandErn, testArc, NormalMode)

        "must return a Bad Request and errors when invalid data is submitted" in new Fixture(userAnswers = Some(northernIrelandUserAnswers)) {
          val boundForm = form.bind(Map("value" -> ""))
          val result = controller.onPreDraftSubmit(testNorthernIrelandErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(boundForm, dispatchPlaceSubmitAction)(dataRequest(request), messages(request)).toString
        }

        "must redirect to the next page when valid data is submitted" in new Fixture(userAnswers = Some(northernIrelandUserAnswers)) {
          val validDispatchPlaceValue = DispatchPlace.values.head

          MockPreDraftService.set(northernIrelandUserAnswers.set(DispatchPlacePage, validDispatchPlaceValue)).returns(Future.successful(true))

          val result = controller.onPreDraftSubmit(testNorthernIrelandErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", validDispatchPlaceValue.toString)))

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual testOnwardRoute.url
        }

      }

    }
  }
}
