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

package viewmodels.taskList

import pages.sections.Section
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{Tag, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.tasklist.TaskListItem

sealed trait TaskListStatus {
  val msgKey: String
  val tagClass: Option[String] = None

  def toTag(implicit messages: Messages) = Tag(Text(messages(msgKey)), tagClass.getOrElse(""))
}

case object Completed extends TaskListStatus {
  override val msgKey: String = "taskListStatus.completed"
}

case object InProgress extends TaskListStatus {
  override val msgKey: String = "taskListStatus.inProgress"
  override val tagClass = Some("govuk-tag--blue")
}

case object NotStarted extends TaskListStatus {
  override val msgKey: String = "taskListStatus.notStarted"
  override val tagClass = Some("govuk-tag--grey")
}

case object Review extends TaskListStatus {
  override val msgKey: String = "taskListStatus.review"
  override val tagClass = Some("govuk-tag--grey")
}

case object CannotStartYet extends TaskListStatus {
  override val msgKey: String = "taskListStatus.cannotStartYet"
  override val tagClass = Some("govuk-tag--grey")
}

case object UpdateNeeded extends TaskListStatus {
  override val msgKey: String = "taskListStatus.updateNeeded"
  override val tagClass = Some("govuk-tag--orange")
}

case class TaskListSection(sectionHeading: String,
                           rows: Seq[TaskListItemViewModel])

case class TaskListItemViewModel(row: TaskListItem,
                                 section: Option[Section[_]])
