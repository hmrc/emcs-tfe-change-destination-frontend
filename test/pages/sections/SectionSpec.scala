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

package pages.sections

import base.SpecBase
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import pages.ReviewPage
import play.api.libs.json.{JsObject, JsPath, __}
import play.api.test.FakeRequest
import viewmodels.taskList.{Completed, Review, TaskListStatus}

class SectionSpec extends SpecBase {

  object TestSection extends Section[JsObject] {
    override def status(implicit request: DataRequest[_]): TaskListStatus = Completed

    override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true

    override val path: JsPath = __ \ "path"
  }

  object FakeReviewPage extends ReviewPage {
    override val path: JsPath = __ \ "reviewPage"
  }

  ".getValueFromIE801" - {
    "must return None" in {
      TestSection.getValueFromIE801(dataRequest(FakeRequest())) mustBe None
    }
  }

  ".sectionHasBeenReviewed" - {
    "must return Completed" - {
      "when the review page answer is KeepAnswers" in {
        val status = TestSection.sectionHasBeenReviewed(FakeReviewPage)(Review)(dataRequest(FakeRequest(), emptyUserAnswers.set(FakeReviewPage, KeepAnswers)), implicitly)
        status mustBe Completed
      }
    }

    "must execute the provided function" - {
      "when the review page answer is ChangeAnswers" in {
        val status = TestSection.sectionHasBeenReviewed(FakeReviewPage)(Review)(dataRequest(FakeRequest(), emptyUserAnswers.set(FakeReviewPage, ChangeAnswers)), implicitly)
        status mustBe Review
      }
    }

    "must return Review" - {
      "when there is no review page answer" in {
        val status = TestSection.sectionHasBeenReviewed(FakeReviewPage)(Completed)(dataRequest(FakeRequest(), emptyUserAnswers), implicitly)
        status mustBe Review
      }
    }

  }

}
