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

package pages.sections.items

import models.Index
import models.requests.DataRequest
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{NotStarted, TaskListStatus}

// $COVERAGE-OFF$
case class ItemBulkPackagingSection(idx: Index) extends Section[JsObject] {

  // not used as this class is just used to easily remove multiple pages at once
  override def status(implicit request: DataRequest[_]): TaskListStatus = NotStarted

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = ItemsSection.canBeCompletedForTraderAndDestinationType

  override val toString: String = "bulkPackaging"

  override val path: JsPath = ItemsSectionItem(idx).path \ toString
}
