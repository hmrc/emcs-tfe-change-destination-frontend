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

package controllers.sections.movement

import base.SpecBase
import controllers.actions.predraft.FakePreDraftRetrievalAction
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import forms.sections.movement.InvoiceDetailsFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.sections.info.InvoiceDetailsModel
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.{FakeInfoNavigator, FakeMovementNavigator}
import pages.sections.movement.InvoiceDetailsPage
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import utils.{DateTimeUtils, TimeMachine}
import views.html.sections.movement.InvoiceDetailsView

import java.time.LocalDate
import scala.concurrent.Future

class InvoiceDetailsControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService with DateTimeUtils {

  val testLocalDate: LocalDate = LocalDate.of(2023, 2, 9)

  val timeMachine: TimeMachine = () => testLocalDate.atStartOfDay()

  lazy val invoiceDetailsPreDraftSubmitRoute: Call = controllers.sections.movement.routes.InvoiceDetailsController.onPreDraftSubmit(testErn, testArc, NormalMode)

  lazy val formProvider: InvoiceDetailsFormProvider = app.injector.instanceOf[InvoiceDetailsFormProvider]
  lazy val form: Form[InvoiceDetailsModel] = formProvider()
  lazy val view: InvoiceDetailsView = app.injector.instanceOf[InvoiceDetailsView]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new InvoiceDetailsController(
      messagesApi = messagesApi,
      navigator = new FakeMovementNavigator(testOnwardRoute),
      auth = fakeAuthAction,
      getData = new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requireData = dataRequiredAction,
      userAllowList = fakeUserAllowListAction,
      withMovement = new FakeMovementAction(maxGetMovementResponse.copy(eadEsad = maxGetMovementResponse.eadEsad.copy(invoiceDate = Some("2023-01-01")))),
      formProvider = formProvider,
      userAnswersService = mockUserAnswersService,
      controllerComponents = Helpers.stubMessagesControllerComponents(),
      view = view,
      timeMachine = timeMachine
    )
  }

  "InvoiceDetails Controller" - {

    "pre-draft" - {

      "must return OK and the correct view for a GET" in new Fixture() {

        val result = controller.onPreDraftPageLoad(testErn, testArc, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(InvoiceDetailsModel("EadEsadInvoiceNumber", LocalDate.parse("2023-01-01"))),
          currentDate = testLocalDate.formatDateNumbersOnly(),
          onSubmitCall = invoiceDetailsPreDraftSubmitRoute,
          skipQuestionCall = testOnwardRoute
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to the next page when valid data is submitted" in new Fixture() {

        val expectedToSaveAnswers = emptyUserAnswers
          .set(InvoiceDetailsPage, InvoiceDetailsModel("answer", LocalDate.of(2020, 1, 1)))

        MockUserAnswersService.set(expectedToSaveAnswers).returns(Future.successful(expectedToSaveAnswers))

        val result = controller.onPreDraftSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(
          ("invoice-reference", "answer"),
          ("value.day", "1"),
          ("value.month", "1"),
          ("value.year", "2020")
        ))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {

        val boundForm = form.bind(Map("value" -> ""))

        val result = controller.onPreDraftSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          currentDate = testLocalDate.formatDateNumbersOnly(),
          onSubmitCall = invoiceDetailsPreDraftSubmitRoute,
          skipQuestionCall = testOnwardRoute
        )(dataRequest(request), messages(request)).toString
      }
    }
  }
}
