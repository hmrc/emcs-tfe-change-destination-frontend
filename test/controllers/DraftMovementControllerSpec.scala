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
import controllers.actions.FakeMovementAction
import models.sections.info.movementScenario.MovementScenario
import pages.sections.info.DestinationTypePage
import play.api.Play.materializer
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.DraftMovementView

class DraftMovementControllerSpec extends SpecBase {
  "onPageLoad" - {

    val userAnswers = emptyUserAnswers.set(DestinationTypePage, MovementScenario.GbTaxWarehouse)
    lazy val view = app.injector.instanceOf[DraftMovementView]

    lazy val testController = new DraftMovementController(
      messagesApi,
      fakeAuthAction,
      fakeUserAllowListAction,
      new FakeDataRetrievalAction(Some(userAnswers), Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse),
      messagesControllerComponents,
      view
    )

    lazy val request = FakeRequest(GET, routes.DraftMovementController.onPageLoad(testErn, testArc).url)

    "must render the page" in {
      lazy val result = testController.onPageLoad(testErn, testArc)(request)


      status(result) mustEqual OK
      contentAsString(result) mustEqual view()(dataRequest(request, userAnswers), messages(request)).toString
    }
  }
}
