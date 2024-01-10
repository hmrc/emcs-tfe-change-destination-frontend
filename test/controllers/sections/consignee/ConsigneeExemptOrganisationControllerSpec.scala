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
import fixtures.OrganisationDetailsFixtures
import forms.sections.consignee.ConsigneeExemptOrganisationFormProvider
import mocks.services.{MockGetMemberStatesService, MockUserAnswersService}
import models.{ExemptOrganisationDetailsModel, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeConsigneeNavigator
import pages.sections.consignee.ConsigneeExemptOrganisationPage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import views.html.sections.consignee.ConsigneeExemptOrganisationView

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class ConsigneeExemptOrganisationControllerSpec extends SpecBase with MockUserAnswersService with OrganisationDetailsFixtures with MockGetMemberStatesService {

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  lazy val formProvider: ConsigneeExemptOrganisationFormProvider = new ConsigneeExemptOrganisationFormProvider()
  lazy val form: Form[ExemptOrganisationDetailsModel] = formProvider()
  lazy val view: ConsigneeExemptOrganisationView = app.injector.instanceOf[ConsigneeExemptOrganisationView]

  lazy val consigneeExemptOrganisationRoute: String =
    controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(testErn, testArc, NormalMode).url
  lazy val onSubmitCall: Call =
    controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onSubmit(testErn, testArc, NormalMode)


  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val testSelectItems: Seq[SelectItem] = Seq(
      SelectItem(
        value = Some("one"),
        text = "one"
      ),
      SelectItem(
        value = Some("two"),
        text = "two"
      ),
      SelectItem(
        value = Some("three"),
        text = "three"
      )
    )
    val request = FakeRequest(GET, consigneeExemptOrganisationRoute)

    lazy val testController = new ConsigneeExemptOrganisationController(
      messagesApi,
      mockUserAnswersService,
      new FakeConsigneeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(memberStateCode = None, serialNumberOfCertificateOfExemption = None)),
      fakeUserAllowListAction,
      formProvider,
      messagesControllerComponents,
      view,
      mockGetMemberStatesService
    )

  }


  "ConsigneeExemptOrganisation Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      MockGetMemberStatesService.getMemberStatesSelectItems().returns(Future(testSelectItems))

      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form,
        items = testSelectItems,
        call = onSubmitCall
      )(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(emptyUserAnswers.set(ConsigneeExemptOrganisationPage, exemptOrganisationDetailsModel))) {

      MockGetMemberStatesService.getMemberStatesSelectItems().returns(Future(testSelectItems))

      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = form.fill(exemptOrganisationDetailsModel),
        items = testSelectItems,
        call = onSubmitCall
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
      val req = FakeRequest(POST, onSubmitCall.url).withFormUrlEncodedBody(
        ("memberState", "answer"),
        ("certificateSerialNumber", "answer")
      )

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Fixture() {
      val req = FakeRequest(POST, onSubmitCall.url).withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      MockGetMemberStatesService.getMemberStatesSelectItems().returns(Future(testSelectItems))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(
        form = boundForm,
        items = testSelectItems,
        call = onSubmitCall
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testArc, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, onSubmitCall.url).withFormUrlEncodedBody(("value", "answer"))

      val result = testController.onSubmit(testErn, testArc, NormalMode)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
