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

package navigation

import base.SpecBase
import controllers.routes
import models._
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import pages._
import pages.sections.movement._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class MovementNavigatorSpec extends SpecBase {

  val navigator = new MovementNavigator

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  "MovementNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.routes.IndexController.onPageLoad(testErn, testArc)
      }

      "for the MovementReview page" - {

        "when answered ChangeAnswers" - {

          "must go to the InvoiceDetails page" in {

            val userAnswers = emptyUserAnswers
              .set(MovementReviewPage, ChangeAnswers)

            navigator.nextPage(MovementReviewPage, NormalMode, userAnswers) mustBe
              controllers.sections.movement.routes.InvoiceDetailsController.onPageLoad(testErn, testArc)
          }
        }

        "when answered KeepAnswers" - {

          "must go to the Task List page" in {

            val userAnswers = emptyUserAnswers
              .set(MovementReviewPage, KeepAnswers)

            navigator.nextPage(MovementReviewPage, NormalMode, userAnswers) mustBe
              controllers.routes.DraftMovementController.onPageLoad(testErn, testArc)
          }
        }
      }

      "for the InvoiceDetails page" - {

        "must go to the MovementReview page" in {
          //TODO: there is a CYA page on the prototype, however there is currently no story for this? (doing as part of this story)
          navigator.nextPage(InvoiceDetailsPage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.movement.routes.MovementReviewAnswersController.onPageLoad(testErn, testArc)
        }
      }
    }

    "in Check mode" - {

      "for all pages" - {

        "must redirect to draft Movement" in {

          val pages: Seq[QuestionPage[_]] = Seq(
            MovementReviewPage,
            InvoiceDetailsPage
          )

          pages.foreach {
            page =>
              navigator.nextPage(page, CheckMode, emptyUserAnswers) mustBe
                routes.DraftMovementController.onPageLoad(testErn, testArc)
          }
        }
      }
    }
  }
}
