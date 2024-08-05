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
import controllers.actions.{DataRequiredAction, FakeDataRetrievalAction, FakeMovementAction}
import fixtures.SubmitChangeDestinationFixtures
import mocks.services.{MockSubmitChangeDestinationService, MockUserAnswersService}
import models.UserAnswers
import models.requests.DataRequest
import models.response.UnexpectedDownstreamSubmissionResponseError
import models.response.emcsTfe.GuarantorType.NoGuarantor
import models.response.emcsTfe.MovementGuaranteeModel
import navigation.FakeNavigators.FakeNavigator
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.DeclarationView

import scala.concurrent.Future


class DeclarationControllerSpec extends SpecBase with MockUserAnswersService with MockSubmitChangeDestinationService with SubmitChangeDestinationFixtures {

  lazy val view: DeclarationView = app.injector.instanceOf[DeclarationView]

  lazy val submitRoute = routes.DeclarationController.onSubmit(testGreatBritainErn, testArc)

  class Test(userAnswers: UserAnswers = baseFullUserAnswers) {

    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
      FakeRequest(),
      userAnswers,
      testGreatBritainErn
    )

    implicit lazy val messagesInstance: Messages = messages(request)

    val controller: DeclarationController = new DeclarationController(
      messagesApi,
      fakeAuthAction,
      fakeBetaAllowListAction,
      new FakeDataRetrievalAction(Some(userAnswers), Some(testMinTraderKnownFacts)),
      app.injector.instanceOf[DataRequiredAction],
      new FakeMovementAction(maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))),
      Helpers.stubMessagesControllerComponents(),
      mockUserAnswersService,
      new FakeNavigator(testOnwardRoute),
      mockSubmitChangeDestinationService,
      view,
      errorHandler
    )
  }


  "DeclarationController" - {
    "for GET onPageLoad" - {
      "must return the declaration page" in new Test() {

        val res = controller.onPageLoad(testGreatBritainErn, testArc)(request)

        status(res) mustBe OK
        contentAsString(res) mustBe view(submitRoute).toString()
      }
    }

    "for POST submit" - {
      "when downstream call is successful" - {
        "must save the timestamp and redirect" in new Test() {

          MockSubmitChangeDestinationService.submit(maxSubmitChangeDestination, testGreatBritainErn).returns(Future.successful(Right(submitChangeDestinationResponseEIS)))
          MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

          val res = controller.onSubmit(testGreatBritainErn, testArc)(request)

          status(res) mustBe SEE_OTHER
          redirectLocation(res) must contain(testOnwardRoute.url)
        }
      }

      "when downstream call is unsuccessful" - {
        "when downstream returns a 422" - {
          "must redirect to the DraftMovementController" in new Test() {
            MockSubmitChangeDestinationService.submit(maxSubmitChangeDestination, testGreatBritainErn).returns(Future.successful(Left(UnexpectedDownstreamSubmissionResponseError(UNPROCESSABLE_ENTITY))))

            val res = controller.onSubmit(testGreatBritainErn, testArc)(request)

            status(res) mustBe SEE_OTHER
            redirectLocation(res) mustBe Some(routes.TaskListController.onPageLoad(testGreatBritainErn, testArc).url)
          }
        }
        "when downstream returns an unexpected response status" - {
          "must return an InternalServerError" in new Test() {
            // arbitrary 5xx status codes
            Seq(INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT).foreach { responseStatus =>
              MockSubmitChangeDestinationService.submit(maxSubmitChangeDestination, testGreatBritainErn).returns(Future.successful(Left(UnexpectedDownstreamSubmissionResponseError(responseStatus))))

              val res = controller.onSubmit(testGreatBritainErn, testArc)(request)

              status(res) mustBe INTERNAL_SERVER_ERROR
            }
          }
        }
      }

      "when creating a request model fails" - {
        "must redirect back to the task list when MissingMandatoryPage" in new Test(emptyUserAnswers) {
          val res = controller.onSubmit(testGreatBritainErn, testArc)(request)

          status(res) mustBe SEE_OTHER
          redirectLocation(res) mustBe Some(routes.TaskListController.onPageLoad(testGreatBritainErn, testArc).url)
        }
      }
    }
  }
}
