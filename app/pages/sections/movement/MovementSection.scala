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

package pages.sections.movement

import models.requests.DataRequest
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{Completed, InProgress, TaskListStatus}

object MovementSection extends Section[JsObject] {

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    sectionHasBeenReviewed(MovementReviewPage) {
      Completed //always complete, because invoice number always exists on EAD from IE801
    }
  }

  override val path: JsPath = JsPath \ "info"

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true

}
