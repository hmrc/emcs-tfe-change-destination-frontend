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
import forms.sections.info.DispatchDetailsFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.sections.info.DispatchDetailsModel
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeInfoNavigator
import pages.sections.info.DispatchDetailsPage
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import utils.DateTimeUtils
import views.html.sections.info.DispatchDetailsView

import java.time.{LocalDate, LocalTime}
import scala.concurrent.Future

class DispatchDetailsControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService with DateTimeUtils {

  val testLocalDate: LocalDate = LocalDate.of(2023, 2, 9)

  lazy val dispatchDetailsPreDraftSubmitRoute: Call = controllers.sections.info.routes.DispatchDetailsController.onPreDraftSubmit(testErn, testArc, NormalMode)

  lazy val formProvider: DispatchDetailsFormProvider = new DispatchDetailsFormProvider()
  lazy val form: Form[DispatchDetailsModel] = formProvider()
  lazy val view: DispatchDetailsView = app.injector.instanceOf[DispatchDetailsView]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new DispatchDetailsController(
      messagesApi,
      mockPreDraftService,
      new FakeInfoNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakePreDraftRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      preDraftDataRequiredAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      formProvider,
      mockUserAnswersService,
      Helpers.stubMessagesControllerComponents(),
      view,
      fakeUserAllowListAction
    )
  }

  "DispatchDetails Controller" - {

    "onPreDraftPageLoad" - {

      "must return OK and the correct view" in new Fixture(Some(emptyUserAnswers)) {
        val result = controller.onPreDraftPageLoad(testErn, testArc, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          deferredMovement = false, //TODO: Refactor in future - we don't know if it was originally deferred. So delete?
          onSubmitCall = dispatchDetailsPreDraftSubmitRoute,
          skipQuestionCall = testOnwardRoute
        )(dataRequest(request), messages(request)).toString
      }
    }

    "onPreDraftSubmit" - {

      "must redirect to the next page when valid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
        val dispatchDetailsModel = DispatchDetailsModel(
          date = LocalDate.of(2022, 12, 31),
          time = LocalTime.of(6, 0)
        )

        MockPreDraftService.set(emptyUserAnswers.set(DispatchDetailsPage(), dispatchDetailsModel)).returns(Future.successful(true))

        val result = controller.onPreDraftSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(
          ("value.day", "31"),
          ("value.month", "12"),
          ("value.year", "2022"),
          ("time", "6am")
        ))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {
        val boundForm = form.bind(Map("time" -> "ten past twelve"))

        val result = controller.onPreDraftSubmit(testErn, testArc, NormalMode)(request.withFormUrlEncodedBody(("time", "ten past twelve")))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          deferredMovement = false, //TODO: Refactor in future - we don't know if it was originally deferred. So delete?
          onSubmitCall = dispatchDetailsPreDraftSubmitRoute,
          skipQuestionCall = testOnwardRoute
        )(dataRequest(request), messages(request)).toString
      }
    }
  }
}
