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

class InformationNavigatorSpec extends SpecBase {

  val navigator = new InformationNavigator

  "InfoNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          controllers.routes.IndexController.onPageLoad(testErn)
      }

      "for the DispatchPlace page" - {

        "must go to the Destination Type page" in {

          navigator.nextPage(DispatchPlacePage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(testErn, NormalMode)
        }
      }

      "for the DestinationType page" - {

        "must go to the Deferred Movement page" in {

          navigator.nextPage(DestinationTypePage, NormalMode, emptyUserAnswers) mustBe
            controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(testErn, NormalMode)
        }
      }

      "for the DeferredMovement page" - {

        "must go to the LocalReferenceNumber page" in {

          navigator.nextPage(DeferredMovementPage(), NormalMode, emptyUserAnswers) mustBe
            controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftPageLoad(testErn, NormalMode)
        }
      }

      "for the LocalReferenceNumber page" - {

        "must go to the Invoice Details page" in {

          navigator.nextPage(LocalReferenceNumberPage(), NormalMode, emptyUserAnswers) mustBe
            controllers.sections.info.routes.InvoiceDetailsController.onPreDraftPageLoad(testErn, NormalMode)
        }
      }

      "for the Invoice Details page" - {

        "must go to the CAM-INFO06" in {

          navigator.nextPage(InvoiceDetailsPage(), NormalMode, emptyUserAnswers) mustBe
            controllers.sections.info.routes.DispatchDetailsController.onPreDraftPageLoad(testErn, NormalMode)
        }
      }

      "for the Dispatch Details page" - {

        "must go to the CAM-INFO07" in {

          navigator.nextPage(DispatchDetailsPage(), NormalMode, emptyUserAnswers) mustBe
            controllers.sections.info.routes.InformationCheckAnswersController.onPreDraftPageLoad(testErn)
        }
      }

      "for the CYA page" - {
        "must go to the tasklist" in {
          navigator.nextPage(InformationCheckAnswersPage, NormalMode, emptyUserAnswers) mustBe
            routes.DraftMovementController.onPageLoad(testErn, testDraftId)
        }
      }
    }

    "in Check mode" - {
      "for pages with isOnPreDraftFlow" - {
        def pagesWithIsOnPreDraftFlow(isOnPreDraftFlow: Boolean): Seq[QuestionPage[_]] = Seq(
          DeferredMovementPage(isOnPreDraftFlow),
          DispatchDetailsPage(isOnPreDraftFlow),
          InvoiceDetailsPage(isOnPreDraftFlow),
          LocalReferenceNumberPage(isOnPreDraftFlow)
        )

        "when isOnPreDraftFlow is true" - {
          "must redirect to pre-draft CYA" in {
            pagesWithIsOnPreDraftFlow(true).foreach {
              page =>
                navigator.nextPage(page, CheckMode, emptyUserAnswers) mustBe
                  controllers.sections.info.routes.InformationCheckAnswersController.onPreDraftPageLoad(testErn)
            }
          }
        }
        "when isOnPreDraftFlow is false" - {
          "must redirect to post-draft CYA" in {
            pagesWithIsOnPreDraftFlow(false).foreach {
              page =>
                navigator.nextPage(page, CheckMode, emptyUserAnswers) mustBe
                  controllers.sections.info.routes.InformationCheckAnswersController.onPageLoad(testErn, testDraftId)
            }
          }
        }
      }
      "for all other pages" - {
        "must redirect to pre-draft CYA" in {
          val allOtherPages: Seq[QuestionPage[_]] = Seq(
            DestinationTypePage,
            DispatchPlacePage
          )

          allOtherPages.foreach {
            page =>
              navigator.nextPage(page, CheckMode, emptyUserAnswers) mustBe
                controllers.sections.info.routes.InformationCheckAnswersController.onPreDraftPageLoad(testErn)
          }
        }
      }
    }
  }
}
