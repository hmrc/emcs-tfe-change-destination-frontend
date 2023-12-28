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

package navigation

import base.SpecBase
import controllers.routes
import models._
import pages._
import pages.sections.info._
import play.api.test.FakeRequest

class InformationNavigatorSpec extends SpecBase {

  val navigator = new InformationNavigator

  implicit val request = dataRequest(FakeRequest())

  "InfoNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.routes.IndexController.onPageLoad(testErn, testArc)
      }

      "for the DispatchPlace page" - {

        "must go to the Destination Type page" in {

          navigator.nextPage(DispatchPlacePage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the DestinationType page" - {

        "must go to the Deferred Movement page" in {

          navigator.nextPage(DestinationTypePage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.info.routes.DispatchDetailsController.onPreDraftPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the Dispatch Details page" - {

        "must go to the DraftMovement" in {

          navigator.nextPage(DispatchDetailsPage(), NormalMode, emptyUserAnswers) mustBe
            routes.DraftMovementController.onPageLoad(testErn, testArc)
        }
      }
    }

    "in Check mode" - {
      "for all pages" - {
        "must redirect to draft Movement" in {

          val pages: Seq[QuestionPage[_]] = Seq(
            DispatchDetailsPage(),
            DestinationTypePage,
            DispatchPlacePage
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
