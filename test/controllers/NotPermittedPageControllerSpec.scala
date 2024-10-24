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
import fixtures.SubmitChangeDestinationFixtures
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.NotPermittedView

class NotPermittedPageControllerSpec extends SpecBase with SubmitChangeDestinationFixtures {

  lazy val view: NotPermittedView = app.injector.instanceOf[NotPermittedView]

  class Test {

    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
      FakeRequest(),
      baseFullUserAnswers,
      testGreatBritainErn
    )

    implicit lazy val messagesInstance: Messages = messages(request)

    val controller: NotPermittedPageController = new NotPermittedPageController(
      messagesApi,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "NotPermittedPage Controller" - {

    "must return OK and the correct view for a GET" in new Test {
      val res = controller.onPageLoad(testGreatBritainErn, testArc)(request)

      status(res) mustBe OK
      contentAsString(res) mustBe view(testErn, testArc).toString()
    }
  }
}
