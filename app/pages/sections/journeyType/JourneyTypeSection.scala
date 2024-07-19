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

package pages.sections.journeyType

import models.Enumerable
import models.requests.DataRequest
import models.sections.journeyType.HowMovementTransported.Other
import pages.sections.Section
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object JourneyTypeSection extends Section[JsObject] with Enumerable.Implicits {
  override val path: JsPath = JsPath \ "journeyType"

  def mustBeFixedTransport(implicit request: DataRequest[_]) =
    DestinationTypePage.isNItoEuMovement && GuarantorRequiredPage.value.contains(false)

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    val pageAnswersExist = List(
      request.userAnswers.get(HowMovementTransportedPage).isDefined,
      request.userAnswers.get(JourneyTimeDaysPage).isDefined || request.userAnswers.get(JourneyTimeHoursPage).isDefined
    ) ++ {
      if (request.userAnswers.get(HowMovementTransportedPage).contains(Other)) {
        // GiveInformationOtherTransportPage is only mandatory when Other is selected
        List(request.userAnswers.get(GiveInformationOtherTransportPage).isDefined)
      } else {
        // if Other isn't selected
        List()
      }
    }

    sectionHasBeenReviewed(JourneyTypeReviewPage) {
      if (pageAnswersExist.forall(identity)) {
        Completed
      } else if (pageAnswersExist.exists(identity)) {
        InProgress
      } else {
        NotStarted
      }
    }
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true
}
