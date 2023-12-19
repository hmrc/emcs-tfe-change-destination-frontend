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
import controllers.actions.{DataRequiredAction, FakeDataRetrievalAction}
import fixtures.ItemFixtures
import mocks.config.MockAppConfig
import mocks.services.{MockSubmitChangeDestinationService, MockUserAnswersService}
import models.UserAnswers
import models.response.SubmitChangeDestinationException
import navigation.FakeNavigators.FakeNavigator
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.DeclarationView

import scala.concurrent.Future


class DeclarationControllerSpec extends SpecBase with MockUserAnswersService with MockSubmitChangeDestinationService with MockAppConfig with ItemFixtures {

  lazy val view: DeclarationView = app.injector.instanceOf[DeclarationView]
  val ern: String = "XIRC123"
  lazy val submitRoute = routes.DeclarationController.onSubmit(ern, testDraftId)


  class Test(userAnswers: UserAnswers = baseFullUserAnswers) {
    implicit val request = dataRequest(
      FakeRequest(),
      userAnswers,
      ern
    )
    implicit lazy val messagesInstance = messages(request)
    val controller: DeclarationController = new DeclarationController(
      messagesApi,
      fakeAuthAction,
      fakeUserAllowListAction,
      new FakeDataRetrievalAction(Some(userAnswers), Some(testMinTraderKnownFacts)),
      app.injector.instanceOf[DataRequiredAction],
      Helpers.stubMessagesControllerComponents(),
      mockUserAnswersService,
      new FakeNavigator(testOnwardRoute),
      mockSubmitChangeDestinationService,
      view,
      errorHandler
    )(mockAppConfig)
  }


  "DeclarationController" - {
    "for GET onPageLoad" - {
      "must return the declaration page" in new Test() {
        MockAppConfig.destinationOfficeSuffix.returns("004098")
        val res = controller.onPageLoad(ern, testDraftId)(request)

        status(res) mustBe OK
        contentAsString(res) mustBe view(submitRoute).toString()
      }

      "when creating a request model fails" - {
        "must return a BadRequest when MissingMandatoryPage" in new Test(emptyUserAnswers) {
          val res = controller.onPageLoad(ern, testDraftId)(request)

          status(res) mustBe SEE_OTHER
          redirectLocation(res) mustBe Some(routes.DraftMovementController.onPageLoad(ern, testDraftId).url)
        }
        "must return a InternalServerError when something else goes wrong" in new Test() {
          MockAppConfig.destinationOfficeSuffix.throws(new Exception("test error"))

          val res = controller.onPageLoad(ern, testDraftId)(request)

          status(res) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "for POST submit" - {
      "when downstream call is successful" - {
        "must save the timestamp and redirect" in new Test() {
          MockAppConfig.destinationOfficeSuffix.returns("004098")
          MockSubmitChangeDestinationService.submit(xircSubmitChangeDestinationModel).returns(Future.successful(submitChangeDestinationResponseEIS))
          MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

          val res = controller.onSubmit(ern, testDraftId)(request)

          status(res) mustBe SEE_OTHER
          redirectLocation(res) must contain(testOnwardRoute.url)
        }
      }

      "when downstream call is unsuccessful" - {
        "must return an InternalServerError" in new Test() {
          MockAppConfig.destinationOfficeSuffix.returns("004098")
          MockSubmitChangeDestinationService.submit(xircSubmitChangeDestinationModel).returns(Future.failed(SubmitChangeDestinationException("test error")))

          val res = controller.onSubmit(ern, testDraftId)(request)

          status(res) mustBe INTERNAL_SERVER_ERROR
        }
      }

      "when creating a request model fails" - {
        "must return a BadRequest when MissingMandatoryPage" in new Test(emptyUserAnswers) {
          val res = controller.onSubmit(ern, testDraftId)(request)

          status(res) mustBe SEE_OTHER
          redirectLocation(res) mustBe Some(routes.DraftMovementController.onPageLoad(ern, testDraftId).url)
        }
        "must return a InternalServerError when something else goes wrong" in new Test() {
          MockAppConfig.destinationOfficeSuffix.throws(new Exception("test error"))

          val res = controller.onSubmit(ern, testDraftId)(request)

          status(res) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
