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

package pages.sections.guarantor

import models.Enumerable
import models.requests.DataRequest
import models.response.emcsTfe.GuarantorType.NoGuarantor
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor}
import models.sections.info.movementScenario.DestinationType.Export
import models.sections.info.movementScenario.MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk
import models.sections.info.movementScenario.MovementType.UkToEu
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import pages.sections.Section
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.libs.json.{JsObject, JsPath}
import viewmodels.taskList.{Completed, InProgress, NotStarted, TaskListStatus}

case object GuarantorSection extends Section[JsObject] with Enumerable.Implicits {
  override val path: JsPath = JsPath \ "guarantor"

  def requiresGuarantorToBeProvided(implicit request: DataRequest[_]): Boolean = {

    val euNoGuarantorRequired =
      request.isNorthernIrelandErn &&
        DestinationTypePage.isUktoEuMovement &&
        request.userAnswers.get(HowMovementTransportedPage).contains(FixedTransportInstallations) &&
        request.movementDetails.items.forall(_.isEnergy)

    val ukNoGuarantorRequired =
      DestinationTypePage.isUktoUkMovement && request.movementDetails.items.forall(_.isBeerOrWine)

    println("==================")
    println("euNoGuarantorRequired -> " + euNoGuarantorRequired)
    println("ukNoGuarantorRequired -> " + ukNoGuarantorRequired)
    println("DestinationTypePage.isUktoUkMovement -> " + DestinationTypePage.isUktoUkMovement)
    println("request.movementDetails.items.forall(_.isBeerOrWine) -> " + request.movementDetails.items.forall(_.isBeerOrWine))
    println("required guarantor -> " + !(ukNoGuarantorRequired || euNoGuarantorRequired))
    println("==================")
    !(ukNoGuarantorRequired || euNoGuarantorRequired)
  }

  //If this movement now requires a guarantor, and the original movement doesn't have one, then it's not possible to have a Review status.
  private def reviewGuard(f: => TaskListStatus)(implicit request: DataRequest[_]): TaskListStatus =
    if(requiresGuarantorToBeProvided && request.movementDetails.movementGuarantee.guarantorTypeCode == NoGuarantor) {
      f
    } else {
      sectionHasBeenReviewed(GuarantorReviewPage)(f)
    }

  override def status(implicit request: DataRequest[_]): TaskListStatus =
    reviewGuard {
      request.userAnswers.get(GuarantorArrangerPage) match {
        case Some(Consignee) | Some(Consignor) => Completed
        case Some(_) =>
          if (
            request.userAnswers.get(GuarantorNamePage).nonEmpty &&
              request.userAnswers.get(GuarantorVatPage).nonEmpty &&
              request.userAnswers.get(GuarantorAddressPage).nonEmpty) {
            Completed
          } else {
            InProgress
          }
        case None =>
          NotStarted
      }
    }

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true
}
