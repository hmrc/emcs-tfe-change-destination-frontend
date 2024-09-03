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

package controllers.sections.destination

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.destination.DestinationBusinessNameFormProvider
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario.{DirectDelivery, UkTaxWarehouse}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.destination.DestinationBusinessNamePage
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.destination.DestinationBusinessNameView

import scala.concurrent.Future

class DestinationBusinessNameControllerSpec extends SpecBase with MockUserAnswersService {

  lazy val formProvider: DestinationBusinessNameFormProvider = new DestinationBusinessNameFormProvider()
  lazy val form: Form[String] = formProvider()
  lazy val view: DestinationBusinessNameView = app.injector.instanceOf[DestinationBusinessNameView]

  lazy val destinationBusinessNameRoute: String =
    controllers.sections.destination.routes.DestinationBusinessNameController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val destinationBusinessNameOnSubmit: Call =
    controllers.sections.destination.routes.DestinationBusinessNameController.onSubmit(testErn, testArc, NormalMode)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {


    lazy val testController = new DestinationBusinessNameController(
      messagesApi,
      mockUserAnswersService,
      new FakeDestinationNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      formProvider,
      messagesControllerComponents,
      view
    )

    val request = FakeRequest(GET, destinationBusinessNameRoute)

  }

  "DestinationBusinessName Controller" - {
    Seq(DirectDelivery, UkTaxWarehouse.GB, UkTaxWarehouse.NI).foreach { destinationType =>
      s"must populate the view correctly on a GET when the question has previously been answered when destinationType is '$destinationType'" in new Fixture(Some(emptyUserAnswers
        .set(DestinationTypePage, destinationType).set(DestinationBusinessNamePage, "answer")
      )) {
        val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), destinationBusinessNameOnSubmit, destinationType, controllers.sections.destination.routes.DestinationBusinessNameController.skipThisQuestion(testErn, testArc, NormalMode))(dataRequest(request), messages(request)).toString
      }

      s"must redirect to the next page when valid data is submitted when destinationType is '${destinationType}'" in new Fixture(Some(emptyUserAnswers.set(DestinationTypePage, destinationType))) {
        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val req = FakeRequest(POST, destinationBusinessNameRoute).withFormUrlEncodedBody(("value", "answer"))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      s"must return a Bad Request and errors when invalid data is submitted when destinationType is '${destinationType}'" in new Fixture(Some(emptyUserAnswers.set(DestinationTypePage, destinationType))) {
        val req = FakeRequest(POST, destinationBusinessNameRoute).withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, destinationBusinessNameOnSubmit, destinationType, controllers.sections.destination.routes.DestinationBusinessNameController.skipThisQuestion(testErn, testArc, NormalMode))(dataRequest(request), messages(request)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onSubmit(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, destinationBusinessNameRoute).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
