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

package controllers

import base.SpecBase
import config.AppConfig
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import models.requests.DataRequest
import models.{ExemptOrganisationDetailsModel, UserAnswers}
import org.scalamock.scalatest.MockFactory
import pages.DeclarationPage
import pages.sections.consignee._
import play.api.Application
import play.api.i18n.Messages
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConfirmationView

class ConfirmationControllerSpec extends SpecBase with MockFactory {
  val testUserAnswers: UserAnswers = emptyUserAnswers
    .set(DeclarationPage, testSubmissionDate)

  lazy val testExciseEnquiriesLink = "testExciseEnquiriesLink"
  lazy val testReturnToAccountLink = "testReturnToAccountLink"
  lazy val testFeedbackBaseUrl = "testFeedbackBaseUrl"
  lazy val testDeskproName = "testDeskproName"

  override lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "urls.exciseGuidance" -> testExciseEnquiriesLink,
        "urls.emcsTfeHome" -> testReturnToAccountLink,
        "feedback-frontend.host" -> testFeedbackBaseUrl,
        "deskproName" -> testDeskproName
      ).build()

  lazy val view: ConfirmationView = app.injector.instanceOf[ConfirmationView]
  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.ConfirmationController.onPageLoad(testErn, testArc).url)
  lazy val controller: ConfirmationController = app.injector.instanceOf[ConfirmationController]

  def getController(userAnswers: UserAnswers) = new ConfirmationController(
    messagesApi,
    fakeAuthAction,
    fakeBetaAllowListAction,
    new FakeDataRetrievalAction(Some(userAnswers), Some(testMinTraderKnownFacts)),
    requireData = dataRequiredAction,
    new FakeMovementAction(maxGetMovementResponse),
    controllerComponents = messagesControllerComponents,
    config = app.injector.instanceOf[AppConfig],
    view = view
  )

  "Confirmation Controller" - {

    "when the confirmation receipt reference is held in session" - {

      "must return OK and the correct view for a GET" in {
        implicit val req: DataRequest[AnyContentAsEmpty.type] = dataRequest(
          request = request,
          answers = testUserAnswers
        )

        implicit val messagesInstance: Messages = messages(req)

        val result = getController(testUserAnswers).onPageLoad(testErn, testArc)(req)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          reference = testArc,
          dateOfSubmission = testSubmissionDate.toLocalDate,
          hasConsigneeChanged = false,
          exciseEnquiriesLink = testExciseEnquiriesLink,
          returnToAccountLink = testReturnToAccountLink,
          feedbackLink = s"$testFeedbackBaseUrl/feedback/$testDeskproName/beta"
        ).toString()
      }

      Seq(ConsigneeExcisePage -> testUserAnswers.set(ConsigneeExcisePage, "changed"),
        ConsigneeExemptOrganisationPage -> testUserAnswers.set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("changed", "changed")),
        ConsigneeBusinessNamePage -> testUserAnswers.set(ConsigneeBusinessNamePage, "changed"),
        ConsigneeAddressPage -> testUserAnswers.set(ConsigneeAddressPage, testUserAddress),
        ConsigneeExportVatPage -> testUserAnswers.set(ConsigneeExportVatPage, testVatNumber)).foreach { pageAndAnswers =>
        s"must return OK and the correct view for a GET - when the answer has changed from the 801 for ${pageAndAnswers._1}" in {
          implicit val req: DataRequest[AnyContentAsEmpty.type] = dataRequest(
            request = request,
            answers = pageAndAnswers._2
          )

          implicit val messagesInstance: Messages = messages(req)

          val result = getController(pageAndAnswers._2).onPageLoad(testErn, testArc)(req)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(
            reference = testArc,
            dateOfSubmission = testSubmissionDate.toLocalDate,
            hasConsigneeChanged = true,
            exciseEnquiriesLink = testExciseEnquiriesLink,
            returnToAccountLink = testReturnToAccountLink,
            feedbackLink = s"$testFeedbackBaseUrl/feedback/$testDeskproName/beta"
          ).toString()
        }
      }
    }

    "when no local reference or submission date is found" - {
      "must redirect to Journey Recovery" in {
        val result = getController(emptyUserAnswers).onPageLoad(testErn, testArc)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
