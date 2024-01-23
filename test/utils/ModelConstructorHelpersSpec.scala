/*
 * Copyright 2024 HM Revenue & Customs
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

package utils

import base.SpecBase
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import pages.{QuestionPage, ReviewPage}
import play.api.libs.json.JsPath
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class ModelConstructorHelpersSpec extends SpecBase {

  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  object TestHelper extends ModelConstructorHelpers

  object TestPage extends QuestionPage[String] {
    override val toString: String = "testPage"
    override val path: JsPath = JsPath \ toString

    override def getValueFromIE801(implicit request: DataRequest[_]): Option[String] = None
  }

  object TestReviewPage extends ReviewPage {
    override val toString: String = "testPage"
    override val path: JsPath = JsPath \ toString
  }

  ".mandatoryPage" - {

    "should return the value if the page answer is present" in {

      TestHelper.mandatoryPage(TestPage)(dataRequest(request, emptyUserAnswers.set(TestPage, "great success")), implicitly) mustBe "great success"
    }

    "should throw a MissingMandatoryPage when the page answer is missing" in {

      intercept[MissingMandatoryPage](
        TestHelper.mandatoryPage(TestPage)(dataRequest(request), implicitly)
      ).message mustBe s"Missing mandatory UserAnswer for page: '$TestPage'"

    }
  }

  ".whenSectionChanged" - {

    "should return None" - {

      "when the review answer page contains KeepAnswers" in {

        TestHelper.whenSectionChanged(TestReviewPage)("great success")(dataRequest(request, emptyUserAnswers.set(TestReviewPage, KeepAnswers))) mustBe None
      }

      "when the review answers page does not have an answer" in {

        TestHelper.whenSectionChanged(TestReviewPage)("great success")(dataRequest(request)) mustBe None
      }
    }

    "should run the function provided" - {

      "when the review answer page is ChangeAnswers" in {

        TestHelper.whenSectionChanged(TestReviewPage)("great success")(dataRequest(request, emptyUserAnswers.set(TestReviewPage, ChangeAnswers))) mustBe Some("great success")
      }
    }
  }
}
