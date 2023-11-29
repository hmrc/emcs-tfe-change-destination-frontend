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

package pages.sections.destination

import models.requests.DataRequest
import models.movementScenario.MovementScenario._
import pages.sections.Section
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{JsObject, JsPath}
import utils.JsonOptionFormatter
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object DestinationSection extends Section[JsObject] with JsonOptionFormatter {

  override val path: JsPath = JsPath \ "destination"

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    (exciseVatAndDetailsChoicePagesComplete, detailsPagesComplete) match {
      case (NotStarted, _) => NotStarted
      case (Completed, Completed) => Completed
      case _ => InProgress
    }
  }

  private def exciseVatAndDetailsChoicePagesComplete(implicit request: DataRequest[_]): TaskListStatus = {
    (
      request.userAnswers.get(DestinationWarehouseExcisePage),
      request.userAnswers.get(DestinationWarehouseVatPage),
      request.userAnswers.get(DestinationDetailsChoicePage)
    ) match {
      case (Some(_), _, _) => Completed
      case (_, Some(_), None) => InProgress
      case (_, _, Some(_)) => Completed
      case _ => NotStarted
    }
  }

  private def detailsPagesComplete(implicit request: DataRequest[_]): TaskListStatus = {
    if (request.userAnswers.get(DestinationDetailsChoicePage).contains(false)) Completed else (
      request.userAnswers.get(DestinationConsigneeDetailsPage),
      request.userAnswers.get(DestinationBusinessNamePage),
      request.userAnswers.get(DestinationAddressPage)
    ) match {
      case (Some(false), Some(_), Some(_)) => Completed
      case (Some(true), _, _) => Completed
      case _ => InProgress
    }
  }

  //noinspection ScalaStyle
  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(DestinationTypePage) match {
      case Some(value) if Seq(GbTaxWarehouse, EuTaxWarehouse, RegisteredConsignee, TemporaryRegisteredConsignee, ExemptedOrganisation, DirectDelivery).contains(value) => true
      case _ => false
    }
}
