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

import models.Index
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType
import pages.sections.Section
import play.api.libs.json.{JsArray, JsPath}
import queries.TransportUnitsCount
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object TransportUnitsSectionUnits extends Section[JsArray] {
  override val toString: String = "units"
  override val path: JsPath = TransportUnitsSection.path \ toString

  override def status(implicit request: DataRequest[_]): TaskListStatus = request.userAnswers.get(TransportUnitsCount) match {
    case Some(0) | None => NotStarted
    case Some(count) =>
      val statuses: Seq[TaskListStatus] = (0 until count).map(value => TransportUnitSection(Index(value)).status)

      if (statuses.forall(_ == Completed)) {
        Completed
      } else {
        InProgress
      }
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    TransportUnitsSection.canBeCompletedForTraderAndDestinationType

  def onlyContainsOrIsEmpty(transportUnitType: TransportUnitType*)(implicit request: DataRequest[_]): Boolean =
    (0 until request.userAnswers.get(TransportUnitsCount).getOrElse(0)).forall { idx =>
      TransportUnitTypePage(idx).value.exists(transportUnitType.contains)
    }
}
