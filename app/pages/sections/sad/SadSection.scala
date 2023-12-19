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

package pages.sections.sad

import models.requests.DataRequest
import models.sections.sad.SadAddToListModel
import models.{GreatBritainRegisteredConsignor, NorthernIrelandRegisteredConsignor}
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import queries.SadCount
import viewmodels.taskList.{Completed, InProgress, TaskListStatus}


case object SadSection extends Section[JsObject] {
  override val toString: String = "sad"
  override val path: JsPath = JsPath \ toString
  val MAX: Int = 99

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    (SadSectionDocuments.status, request.userAnswers.get(SadAddToListPage), request.userAnswers.get(SadCount)) match {
      case (Completed, _, Some(MAX)) => Completed
      case (Completed, Some(SadAddToListModel.NoMoreToCome), _) => Completed
      case (Completed, Some(SadAddToListModel.Yes) | None, _) => InProgress
      case (InProgress, _, _) => InProgress
      case (status, _, _) => status
    }
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    (request.userTypeFromErn == GreatBritainRegisteredConsignor) || (request.userTypeFromErn == NorthernIrelandRegisteredConsignor)
}
