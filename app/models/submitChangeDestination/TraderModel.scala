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

package models.submitChangeDestination

import models.UserAddress
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.sections.info.movementScenario.MovementScenario
import models.sections.transportArranger.TransportArranger
import pages.sections.consignee._
import pages.sections.consignor._
import pages.sections.destination._
import pages.sections.firstTransporter._
import pages.sections.guarantor._
import pages.sections.transportArranger._
import play.api.libs.json.{Format, Json}
import utils.ModelConstructorHelpers

//TODO: REFACTOR FOR ACTUAL COD SUBMISSION
case class TraderModel(traderExciseNumber: Option[String],
                       traderName: Option[String],
                       address: Option[AddressModel],
                       vatNumber: Option[String],
                       eoriNumber: Option[String])

object TraderModel extends ModelConstructorHelpers {

  def applyConsignee(implicit request: DataRequest[_]): Option[TraderModel] = {
    if (ConsigneeSection.canBeCompletedForTraderAndDestinationType) {
      val consigneeAddress: UserAddress = mandatoryPage(ConsigneeAddressPage)

      Some(TraderModel(
        // Consignee section has multiple entry points.
        // If the ConsigneeExcisePage is defined, use that, otherwise use the VAT number entered on the ConsigneeExportVatPage.
        traderExciseNumber = (request.userAnswers.get(ConsigneeExcisePage), request.userAnswers.get(ConsigneeExportVatPage).flatMap(_.vatNumber)) match {
          case (Some(ern), _) => Some(ern)
          case (_, Some(ern)) => Some(ern)
          case _ => None
        },
        traderName = Some(mandatoryPage(ConsigneeBusinessNamePage)),
        address = Some(AddressModel.fromUserAddress(consigneeAddress)),
        vatNumber = None,
        eoriNumber = request.userAnswers.get(ConsigneeExportVatPage).flatMap(_.eoriNumber)
      ))
    } else {
      None
    }
  }

  def applyConsignor(implicit request: DataRequest[_]): TraderModel = {
    val consignorAddress: UserAddress = mandatoryPage(ConsignorAddressPage)
    TraderModel(
      traderExciseNumber = Some(request.ern),
      traderName = Some(request.traderKnownFacts.traderName),
      address = Some(AddressModel.fromUserAddress(consignorAddress)),
      vatNumber = None,
      eoriNumber = None
    )
  }

  //noinspection ScalaStyle
  def applyDeliveryPlace(movementScenario: MovementScenario)(implicit request: DataRequest[_]): Option[TraderModel] = {
    if (DestinationSection.canBeCompletedForTraderAndDestinationType) {
      if (DestinationSection.shouldStartFlowAtDestinationWarehouseExcise(movementScenario)) {
        val exciseId: String = mandatoryPage(DestinationWarehouseExcisePage)
        val useConsigneeDetails: Boolean = mandatoryPage(DestinationConsigneeDetailsPage)

        if (useConsigneeDetails) {
          val consigneeTrader = applyConsignee
          Some(TraderModel(
            traderExciseNumber = Some(exciseId),
            traderName = consigneeTrader.flatMap(_.traderName),
            address = consigneeTrader.flatMap(_.address),
            vatNumber = None,
            eoriNumber = None
          ))
        } else {
          Some(TraderModel(
            traderExciseNumber = Some(exciseId),
            traderName = Some(mandatoryPage(DestinationBusinessNamePage)),
            address = Some(AddressModel.fromUserAddress(mandatoryPage(DestinationAddressPage))),
            vatNumber = None,
            eoriNumber = None
          ))
        }
      } else if (DestinationSection.shouldStartFlowAtDestinationWarehouseVat(movementScenario)) {
        val exciseId: Option[String] = request.userAnswers.get(DestinationWarehouseVatPage)
        val giveAddressAndBusinessName: Boolean = mandatoryPage(DestinationDetailsChoicePage)

        if (giveAddressAndBusinessName) {
          Some(TraderModel(
            traderExciseNumber = exciseId,
            traderName = request.userAnswers.get(DestinationBusinessNamePage),
            address = request.userAnswers.get(DestinationAddressPage).map(AddressModel.fromUserAddress),
            vatNumber = None,
            eoriNumber = None
          ))
        } else {
          Some(TraderModel(
            traderExciseNumber = exciseId,
            traderName = None,
            address = None,
            vatNumber = None,
            eoriNumber = None
          ))
        }
      } else {
        Some(TraderModel(
          traderExciseNumber = None,
          traderName = request.userAnswers.get(DestinationBusinessNamePage),
          address = request.userAnswers.get(DestinationAddressPage).map(AddressModel.fromUserAddress),
          vatNumber = None,
          eoriNumber = None
        ))
      }
    } else {
      None
    }
  }

  def applyTransportArranger(implicit request: DataRequest[_]): Option[TraderModel] = {
    val transportArranger: TransportArranger = mandatoryPage(TransportArrangerPage)

    transportArranger match {
      case TransportArranger.Consignor => None
      case TransportArranger.Consignee => None
      case _ => Some(TraderModel(
        traderExciseNumber = None,
        traderName = Some(mandatoryPage(TransportArrangerNamePage)),
        address = Some(AddressModel.fromUserAddress(mandatoryPage(TransportArrangerAddressPage))),
        vatNumber = Some(mandatoryPage(TransportArrangerVatPage)),
        eoriNumber = None
      ))
    }
  }

  def applyFirstTransporter(implicit request: DataRequest[_]): Option[TraderModel] = {
    Some(TraderModel(
      traderExciseNumber = None,
      traderName = Some(mandatoryPage(FirstTransporterNamePage)),
      address = Some(AddressModel.fromUserAddress(mandatoryPage(FirstTransporterAddressPage))),
      vatNumber = Some(mandatoryPage(FirstTransporterVatPage)),
      eoriNumber = None
    ))
  }

  def applyGuarantor(guarantorArranger: GuarantorArranger)(implicit request: DataRequest[_]): Option[TraderModel] = {
    guarantorArranger match {
      case GuarantorArranger.Consignor => Some(applyConsignor.copy(traderExciseNumber = None))
      case GuarantorArranger.Consignee =>
        val consigneeTrader = applyConsignee
        Some(TraderModel(
          traderExciseNumber = None,
          traderName = consigneeTrader.flatMap(_.traderName),
          address = consigneeTrader.flatMap(_.address),
          vatNumber = request.userAnswers.get(ConsigneeExportVatPage).flatMap(_.vatNumber),
          eoriNumber = None
        ))
      case _ => Some(TraderModel(
        traderExciseNumber = None,
        traderName = Some(mandatoryPage(GuarantorNamePage)),
        address = Some(AddressModel.fromUserAddress(mandatoryPage(GuarantorAddressPage))),
        vatNumber = Some(mandatoryPage(GuarantorVatPage)),
        eoriNumber = None
      ))
    }
  }

  implicit val fmt: Format[TraderModel] = Json.format
}
