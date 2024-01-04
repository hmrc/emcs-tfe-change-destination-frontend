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

package pages.sections.transportUnit

import base.SpecBase
import models.Index
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.transportUnit.TransportUnitType.{Container, Tractor}
import models.sections.transportUnit.TransportUnitsAddToListModel.{MoreToCome, NoMoreToCome}
import play.api.test.FakeRequest
import viewmodels.taskList.{Completed, InProgress, NotStarted}

class TransportUnitsSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when all items are finished" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
              .set(TransportUnitTypePage(testIndex2), Container)
              .set(TransportUnitIdentityPage(testIndex2), "")
              .set(TransportSealChoicePage(testIndex2), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex2), false)
              .set(TransportUnitsAddToListPage, NoMoreToCome)
              .set(TransportUnitsReviewPage, ChangeAnswers)
          )
        TransportUnitsSection.isCompleted mustBe true
      }

      "keep answers has been selected" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitsReviewPage, KeepAnswers)
          )
        TransportUnitsSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when empty user answers" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers.set(TransportUnitsReviewPage, ChangeAnswers))
        TransportUnitsSection.isCompleted mustBe false
      }
      "when there is somehow a transport unit with nothing in it" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers.set(TransportUnitsReviewPage, ChangeAnswers), movementDetails = maxGetMovementResponse.copy(transportDetails = Seq.empty))
        TransportUnitsSection.isCompleted mustBe false
      }
      "when at least one section is unfinished" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
              .set(TransportUnitTypePage(testIndex2), Container)
              .set(TransportUnitIdentityPage(testIndex2), "")
              .set(TransportSealChoicePage(testIndex2), false)
              .set(TransportUnitsReviewPage, ChangeAnswers)
          )
        TransportUnitsSection.isCompleted mustBe false
      }
    }
  }

  "status" - {
    "must return completed" - {
      "when all sections are completed and add to list is no more" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
              .set(TransportUnitTypePage(testIndex2), Container)
              .set(TransportUnitIdentityPage(testIndex2), "")
              .set(TransportSealChoicePage(testIndex2), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex2), false)
              .set(TransportUnitsAddToListPage, NoMoreToCome)
              .set(TransportUnitsReviewPage, ChangeAnswers)
          )

        TransportUnitsSection.status mustBe Completed
      }

      "when max units added and all complete" in {
        val fullUserAnswers = (0 until 99).foldLeft(emptyUserAnswers)((answers, int) => answers
          .set(TransportUnitTypePage(Index(int)), Tractor)
          .set(TransportUnitIdentityPage(Index(int)), "")
          .set(TransportSealChoicePage(Index(int)), false)
          .set(TransportUnitGiveMoreInformationChoicePage(Index(int)), false)
          .set(TransportUnitsReviewPage, ChangeAnswers)
        )

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), fullUserAnswers)

        TransportUnitsSection.status mustBe Completed
      }
    }
    "must return in progress" - {
      "when all completed and add to list is more to come" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
              .set(TransportUnitTypePage(testIndex2), Container)
              .set(TransportUnitIdentityPage(testIndex2), "")
              .set(TransportSealChoicePage(testIndex2), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex2), false)
              .set(TransportUnitsAddToListPage, MoreToCome)
              .set(TransportUnitsReviewPage, ChangeAnswers)
          )

        TransportUnitsSection.status mustBe InProgress
      }
      "when at least one section is in progress" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
              .set(TransportUnitTypePage(testIndex2), Container)
              .set(TransportUnitIdentityPage(testIndex2), "")
              .set(TransportUnitsReviewPage, ChangeAnswers)
          )

        TransportUnitsSection.status mustBe InProgress
      }
      "when at least one section is in progress and add to list is No more to come" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Container)
              .set(TransportUnitIdentityPage(testIndex1), "")
              .set(TransportSealChoicePage(testIndex1), false)
              .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), false)
              .set(TransportUnitTypePage(testIndex2), Container)
              .set(TransportUnitIdentityPage(testIndex2), "")
              .set(TransportUnitsAddToListPage, NoMoreToCome)
              .set(TransportUnitsReviewPage, ChangeAnswers)
          )

        TransportUnitsSection.status mustBe InProgress
      }
    }

    "must return not started" - {
      "when empty user answers" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitsReviewPage, ChangeAnswers))

        TransportUnitsSection.status mustBe NotStarted
      }
    }
  }
}
