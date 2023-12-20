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
import models.sections.items.ItemsPackagingAddToList
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import queries.ItemsPackagingCount
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case class ItemsPackagingSection(itemIndex: Index) extends Section[JsObject] {
  override val path: JsPath = ItemsSectionItem(itemIndex).path \ "packaging"
  val MAX: Int = 99

  override def status(implicit request: DataRequest[_]): TaskListStatus =
    request.userAnswers.get(ItemsPackagingAddToListPage(itemIndex)) match {
      case Some(ItemsPackagingAddToList.MoreLater) => InProgress
      case _ => checkIndividualPackagesStatus()
    }

  def checkIndividualPackagesStatus()(implicit request: DataRequest[_]): TaskListStatus =
    request.userAnswers.get(ItemsPackagingCount(itemIndex)) match {
      case Some(0) | None => NotStarted
      case Some(count) =>
        val statuses: Seq[TaskListStatus] = (0 until count).map(ItemsPackagingSectionItems(itemIndex, _).status)

        if (statuses.forall(_ == Completed)) Completed else InProgress
    }

  // $COVERAGE-OFF$
  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    ItemsSection.canBeCompletedForTraderAndDestinationType
}
