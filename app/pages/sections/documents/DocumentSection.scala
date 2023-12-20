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

import models.Index
import models.requests.DataRequest
import pages.sections.Section
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList._

case class DocumentSection(idx: Index) extends Section[JsObject] {

  override val path: JsPath = DocumentsSectionUnits.path \ idx.position

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    (
      request.userAnswers.get(DocumentTypePage(idx)),
      request.userAnswers.get(ReferenceAvailablePage(idx)),
      request.userAnswers.get(DocumentReferencePage(idx)),
      request.userAnswers.get(DocumentDescriptionPage(idx))
    ) match {
      case (Some(docType), Some(true), Some(_), None) if docType.typeIsOther => Completed
      case (Some(docType), Some(false), None, Some(_)) if docType.typeIsOther => Completed
      case (Some(_), None, Some(_), None) => Completed
      case (None, None, None, None) => NotStarted
      case _ => InProgress
    }
  }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    DocumentsSection.canBeCompletedForTraderAndDestinationType
}
