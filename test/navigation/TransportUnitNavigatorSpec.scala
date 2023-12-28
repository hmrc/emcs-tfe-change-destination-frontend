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
import controllers.sections.transportUnit.{routes => transportUnitRoutes}
import fixtures.TransportUnitFixtures
import models.sections.transportUnit.TransportUnitType.Tractor
import models.sections.transportUnit.TransportUnitsAddToListModel
import models.{CheckMode, Index, NormalMode, ReviewMode}
import pages.Page
import pages.sections.transportUnit._
import play.api.test.FakeRequest

class TransportUnitNavigatorSpec extends SpecBase with TransportUnitFixtures {

  val navigator = new TransportUnitNavigator

  implicit val request = dataRequest(FakeRequest())

  "in Normal mode" - {

    "must go from a page that doesn't exist in the route map to Transport Unit CYA" in {
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
        transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(testErn, testArc)
    }

    "for the TransportUnitType (CAM-TU01)" - {

      "must go to CAM-TU02" in {
        val userAnswers = emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)

        navigator.nextPage(TransportUnitTypePage(testIndex1), NormalMode, userAnswers) mustBe
          transportUnitRoutes.TransportUnitIdentityController.onPageLoad(testErn, testArc, Index(0), NormalMode)
      }
    }

    "for the TransportUnitIdentity (CAM-TU02)" - {

      "must go to CAM-TU03" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor)
          .set(TransportUnitIdentityPage(testIndex1), "weee")

        navigator.nextPage(TransportUnitIdentityPage(testIndex1), NormalMode, userAnswers) mustBe
          transportUnitRoutes.TransportSealChoiceController.onPageLoad(userAnswers.ern, userAnswers.arc, testIndex1, NormalMode)
      }
    }

    "for the TransportSealChoicePage (CAM-TU03)" - {

      "must go to CAM-TU04 when TransportSealChoice is true" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor)
          .set(TransportSealChoicePage(testIndex1), true)

        navigator.nextPage(TransportSealChoicePage(testIndex1), NormalMode, userAnswers) mustBe
          transportUnitRoutes.TransportSealTypeController.onPageLoad(testErn, testArc, testIndex1, NormalMode)
      }

      "must go to CAM-TU05 when TransportSealChoice is false" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor)
          .set(TransportSealChoicePage(testIndex1), false)

        navigator.nextPage(TransportSealChoicePage(testIndex1), NormalMode, userAnswers) mustBe
          transportUnitRoutes.TransportUnitGiveMoreInformationChoiceController.onPageLoad(testErn, testArc, testIndex1, NormalMode)
      }
    }

    "for the TransportSealType (CAM-TU04)" - {

      "must go to CAM-TU05" in {

        val userAnswers = emptyUserAnswers.set(TransportSealTypePage(testIndex1), transportSealTypeModelMax)

        navigator.nextPage(TransportSealTypePage(testIndex1), NormalMode, userAnswers) mustBe
          transportUnitRoutes.TransportUnitGiveMoreInformationChoiceController.onPageLoad(testErn, testArc, testIndex1, NormalMode)
      }

    }

    "for the TransportUnitGiveMoreInformationChoice (CAM-TU05)" - {

      "must go to CAM-TU06" - {

        "when the answer is Yes" in {
          val userAnswers = emptyUserAnswers.set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)

          navigator.nextPage(TransportUnitGiveMoreInformationChoicePage(testIndex1), NormalMode, userAnswers) mustBe
            transportUnitRoutes.TransportUnitGiveMoreInformationController.onPageLoad(testErn, testArc, testIndex1, NormalMode)
        }
      }

      "must go to CAM-TU07" - {

        "when the answer is No" in {
          val userAnswers = emptyUserAnswers.set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)

          navigator.nextPage(TransportUnitGiveMoreInformationChoicePage(testIndex1), NormalMode, userAnswers) mustBe
            transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(testErn, testArc)
        }
      }
    }

    "for the TransportUnitGiveMoreInformation (CAM-TU06)" - {
      "must go to CAM-TU07" in {

        val userAnswers = emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor)
          .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("answer"))

        navigator.nextPage(TransportUnitGiveMoreInformationPage(testIndex1), NormalMode, userAnswers) mustBe
          transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(testErn, testArc)
      }
    }

    "for the TransportUnitAddToListPage (CAM-TU07)" - {
      "must go to CAM-TU01" - {
        "when user selects yes" in {

          val userAnswers = emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("answer"))
            .set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.Yes)

          navigator.nextPage(TransportUnitsAddToListPage, NormalMode, userAnswers) mustBe
            transportUnitRoutes.TransportUnitTypeController.onPageLoad(testErn, testArc, testIndex2, NormalMode)
        }
      }
      "must go to CAM02" - {
        "when user selects I'll add more later" in {

          val userAnswers = emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("answer"))
            .set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.MoreToCome)

          navigator.nextPage(TransportUnitsAddToListPage, NormalMode, userAnswers) mustBe
            routes.DraftMovementController.onPageLoad(testErn, testArc)
        }

        "when user selects no more" in {

          val userAnswers = emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("answer"))
            .set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.NoMoreToCome)

          navigator.nextPage(TransportUnitsAddToListPage, NormalMode, userAnswers) mustBe
            routes.DraftMovementController.onPageLoad(testErn, testArc)
        }
      }
    }

  }

  "in Check mode" - {
    "must go to CheckYourAnswersTransportUnitController" in {
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
        transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(testErn, testArc)
    }

    "must go to CheckYourAnswersTransportUnitController from TransportSealChoicePage if answer is false" in {
      navigator.nextPage(TransportSealChoicePage(testIndex1), CheckMode, emptyUserAnswers.set(TransportSealChoicePage(testIndex1), false)) mustBe
        transportUnitRoutes.TransportUnitsAddToListController.onPageLoad(testErn, testArc)
    }

    "must go to TransportSealTypePage from TransportSealChoicePage if answer is true" in {
      navigator.nextPage(TransportSealChoicePage(testIndex1), CheckMode, emptyUserAnswers.set(TransportSealChoicePage(testIndex1), true)) mustBe
        transportUnitRoutes.TransportSealTypeController.onPageLoad(testErn, testArc, testIndex1, CheckMode)
    }
  }

  "in Review mode" - {
    "must go to CheckYourAnswers" in {
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
        routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
    }
  }
}
