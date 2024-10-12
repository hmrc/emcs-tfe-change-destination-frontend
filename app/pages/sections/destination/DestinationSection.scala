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

import models.Enumerable
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario._
import pages.sections.Section
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{JsObject, JsPath}
import utils.JsonOptionFormatter
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object DestinationSection extends Section[JsObject] with JsonOptionFormatter with Enumerable.Implicits {

  override val path: JsPath = JsPath \ "destination"

  def shouldStartFlowAtDestinationWarehouseExcise(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    (UkTaxWarehouse.values ++ Seq(
      EuTaxWarehouse
    )).contains(destinationTypePageAnswer)

  def shouldStartFlowAtDestinationWarehouseVat(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      RegisteredConsignee,
      TemporaryRegisteredConsignee,
      CertifiedConsignee,
      TemporaryCertifiedConsignee,
      ExemptedOrganisation
    ).contains(destinationTypePageAnswer)

  def shouldStartFlowAtDestinationAddress(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      DirectDelivery
    ).contains(destinationTypePageAnswer)

  def shouldSkipDestinationDetailsChoice(implicit destinationTypePageAnswer: MovementScenario): Boolean =
    Seq(
      CertifiedConsignee,
      TemporaryCertifiedConsignee
    ).contains(destinationTypePageAnswer)

  override def status(implicit request: DataRequest[_]): TaskListStatus =
    request.userAnswers.get(DestinationTypePage) match {
      case Some(value) =>
        implicit val destinationTypePageAnswer: MovementScenario = value
        if (shouldStartFlowAtDestinationWarehouseExcise) {
          startFlowAtDestinationWarehouseExciseStatus
        } else if (shouldStartFlowAtDestinationWarehouseVat) {
          startFlowAtDestinationWarehouseVatStatus
        } else if (shouldStartFlowAtDestinationAddress) {
          startFlowAtDestinationAddressStatus
        } else {
          NotStarted
        }
      case None => NotStarted
    }

  private def startFlowAtDestinationWarehouseExciseStatus(implicit request: DataRequest[_]): TaskListStatus =
    (
      request.userAnswers.get(DestinationWarehouseExcisePage),
      request.userAnswers.get(DestinationConsigneeDetailsPage),
      request.userAnswers.get(DestinationAddressPage)
    ) match {
      case (Some(_), Some(true), _) => Completed
      case (Some(_), Some(false), Some(_)) => Completed
      case (Some(_), Some(_), Some(_)) if isDirectDelivery => Completed
      case (Some(_), Some(false), a) if a.isEmpty => InProgress
      case (Some(_), _, _) => InProgress
      case _ => NotStarted
    }

  //noinspection ScalaStyle
  private def startFlowAtDestinationWarehouseVatStatus(implicit request: DataRequest[_], movementScenario: MovementScenario): TaskListStatus = {

    val destinationDetailsChoice = if (shouldSkipDestinationDetailsChoice) Some(true) else {
      request.userAnswers.get(DestinationDetailsChoicePage)
    }

    (
      destinationDetailsChoice,
      request.userAnswers.get(DestinationConsigneeDetailsPage),
      request.userAnswers.get(DestinationAddressPage)
    ) match {
      case (Some(false), _, _) => Completed
      case (Some(true), _, _) => Completed
      case (Some(true), Some(_), Some(_)) => Completed
      case (Some(true), None, Some(_)) if isDirectDelivery => Completed
      case (Some(_), bn, a) if bn.isEmpty || a.isEmpty => InProgress
      case (Some(_), _, _) if !shouldSkipDestinationDetailsChoice => InProgress
      case _ if request.userAnswers.get(DestinationWarehouseVatPage).nonEmpty => InProgress
      case _ => NotStarted
    }
  }

  private def startFlowAtDestinationAddressStatus(implicit request: DataRequest[_]): TaskListStatus =
    request.userAnswers.get(DestinationAddressPage) match {
      case Some(_) => Completed
      case Some(_) if isDirectDelivery => Completed
      case a if a.isEmpty => NotStarted
      case _ => InProgress
    }

  private def isDirectDelivery(implicit request: DataRequest[_]): Boolean =
    DestinationTypePage.value.exists {
      movementScenario =>
        Seq(
          DirectDelivery
        ).contains(movementScenario)
    }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(DestinationTypePage) match {
      case Some(value) if (UkTaxWarehouse.values ++ Seq(
        EuTaxWarehouse,
        RegisteredConsignee,
        TemporaryRegisteredConsignee,
        CertifiedConsignee,
        TemporaryCertifiedConsignee,
        ExemptedOrganisation,
        DirectDelivery
      )).contains(value) => true
      case _ => false
    }
}
