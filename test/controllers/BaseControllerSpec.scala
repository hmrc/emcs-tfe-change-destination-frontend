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
import models.UserAnswers
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.QuestionPage
import play.api.data.Form
import play.api.data.Forms.text
import play.api.i18n.MessagesApi
import play.api.libs.json.JsPath
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import uk.gov.hmrc.http.HeaderCarrier

class BaseControllerSpec extends SpecBase with GuiceOneAppPerSuite with BaseController {

  override lazy val controllerComponents: MessagesControllerComponents = messagesControllerComponents
  override lazy val messagesApi: MessagesApi = super.messagesApi

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  val testForm = Form("value" -> text)

  object TestPage extends QuestionPage[String] {
    override val path: JsPath = JsPath \ "test"

    override def getValueFromIE801(implicit request: DataRequest[_]): Option[String] =
      request.movementDetails.competentAuthorityDispatchOfficeReferenceNumber
  }

  class Test(val userAnswers: UserAnswers = emptyUserAnswers,
             val movementDetails: GetMovementResponse = maxGetMovementResponse) {

    implicit val request = dataRequest(FakeRequest(GET, "/foo/bar"), userAnswers, movementDetails = movementDetails)
  }

  "fillForm" - {
    "when there is a value for the page in UserAnswers" - {
      "fill the form" in new Test(emptyUserAnswers.set(TestPage, "foo")) {
        fillForm(TestPage, testForm) mustEqual testForm.fill("foo")
      }
    }

    "when there is a value for the page in IE801" - {
      "fill the form" in new Test(movementDetails = maxGetMovementResponse.copy(competentAuthorityDispatchOfficeReferenceNumber = Some("bar"))) {
        fillForm(TestPage, testForm) mustEqual testForm.fill("bar")
      }
    }

    "when there is NO value in either user answers or IE801" - {
      "NOT fill the form" in new Test(movementDetails = maxGetMovementResponse.copy(competentAuthorityDispatchOfficeReferenceNumber = None)) {
        fillForm(TestPage, testForm) mustEqual testForm
      }
    }
  }

  "fillFormFromUserAnswersOnly" - {
    "when there is a value for the page in UserAnswers" - {
      "fill the form" in new Test(emptyUserAnswers.set(TestPage, "foo")) {
        fillFormFromUserAnswersOnly(TestPage, testForm) mustEqual testForm.fill("foo")
      }
    }

    "when there is a value for the page in IE801" - {
      "NOT fill the form" in new Test(movementDetails = maxGetMovementResponse.copy(competentAuthorityDispatchOfficeReferenceNumber = Some("bar"))) {
        fillFormFromUserAnswersOnly(TestPage, testForm) mustEqual testForm
      }
    }

    "when there is NO value in either user answers or IE801" - {
      "NOT fill the form" in new Test(movementDetails = maxGetMovementResponse.copy(competentAuthorityDispatchOfficeReferenceNumber = None)) {
        fillFormFromUserAnswersOnly(TestPage, testForm) mustEqual testForm
      }
    }
  }
}
