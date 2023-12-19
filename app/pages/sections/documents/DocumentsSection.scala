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

package pages.sections.documents

import models.requests.DataRequest
import models.sections.documents.DocumentsAddToList
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import queries.DocumentsCount
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object DocumentsSection extends Section[JsObject] {
  override val path: JsPath = JsPath \ "documents"
  val MAX: Int = 9

  override def status(implicit request: DataRequest[_]): TaskListStatus =
    request.userAnswers.get(DocumentsCertificatesPage) match {
      case Some(false) => Completed
      case Some(true) => documentListStatus
      case None => NotStarted
    }

  private def documentListStatus(implicit request: DataRequest[_]): TaskListStatus =
    (request.userAnswers.get(DocumentsCount), request.userAnswers.get(DocumentsAddToListPage)) match {
      case (_, Some(DocumentsAddToList.No)) => DocumentsSectionUnits.status
      case (Some(count), _) if count >= MAX => DocumentsSectionUnits.status
      case _ => InProgress
    }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true
}
