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
import controllers.actions.FakeDataRetrievalAction
import mocks.services.MockUserAnswersService
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import views.html.IndexPage

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with MockUserAnswersService {

  "IndexController Controller" - {

    "when calling .onPageLoad()" - {

      "when existing UserAnswers don't exist" - {

        "must Initialise the UserAnswers and redirect to the IndexController" in {

          MockUserAnswersService.set(emptyUserAnswers).returns(Future.successful(emptyUserAnswers))

          val controller = new IndexController(
            messagesApi,
            mockUserAnswersService,
            messagesControllerComponents,
            fakeAuthAction,
            fakeMovementAction,
            new FakeDataRetrievalAction(None, Some(testMinTraderKnownFacts)),
            app.injector.instanceOf[IndexPage]
          )

          val result = controller.onPageLoad(testErn, testArc)(FakeRequest())

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.IndexController.onPageLoad(testErn, testArc).url)
        }
      }
    }
  }
}
