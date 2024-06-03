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

case class TraderModel(traderExciseNumber: Option[String],
                       traderName: Option[String],
                       address: Option[AddressModel],
                       vatNumber: Option[String],
                       eoriNumber: Option[String])

object TraderModel extends ModelConstructorHelpers {

  def applyConsignee(implicit request: DataRequest[_]): TraderModel =
    TraderModel(
      // Consignee section has multiple entry points.
      // If the ConsigneeExcisePage is defined, use that, otherwise use the VAT number entered on the ConsigneeExportInformationPage.
      traderExciseNumber = (
        request.userAnswers.get(ConsigneeExcisePage),
        request.userAnswers.get(ConsigneeExportInformationPage).flatMap(_.vatNumber), //TODO: remove in ETFE-3250
        request.userAnswers.get(ConsigneeExportVatPage)
      ) match {
        case (ern@Some(_), _, _) => ern
        case (_, ern@Some(_), _) => ern
        case (_, _, ern@Some(_)) => ern
        case _ => None
      },
      traderName = Some(mandatoryPage(ConsigneeBusinessNamePage)),
      address = Some(AddressModel.fromUserAddress(mandatoryPage(ConsigneeAddressPage))),
      vatNumber = None,
      eoriNumber = request.userAnswers.get(ConsigneeExportInformationPage).flatMap(_.eoriNumber) //TODO: replace with EORI page in ETFE-3250
    )

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
            traderName = consigneeTrader.traderName,
            address = consigneeTrader.address,
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

        val giveAddressAndBusinessName: Boolean =
          if (DestinationSection.shouldSkipDestinationDetailsChoice(movementScenario)) true
          else mandatoryPage(DestinationDetailsChoicePage)

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

  def applyTransportArranger(implicit request: DataRequest[_]): TraderModel =
    mandatoryPage(TransportArrangerPage) match {
      case TransportArranger.Consignor => TraderModel.applyConsignor
      case TransportArranger.Consignee => TraderModel.applyConsignee
      case _ => TraderModel(
        traderExciseNumber = None,
        traderName = Some(mandatoryPage(TransportArrangerNamePage)),
        address = Some(AddressModel.fromUserAddress(mandatoryPage(TransportArrangerAddressPage))),
        vatNumber = Some(mandatoryPage(TransportArrangerVatPage)),
        eoriNumber = None
      )
    }

  def applyFirstTransporter(implicit request: DataRequest[_]): TraderModel =
    TraderModel(
      traderExciseNumber = None,
      traderName = Some(mandatoryPage(FirstTransporterNamePage)),
      address = Some(AddressModel.fromUserAddress(mandatoryPage(FirstTransporterAddressPage))),
      vatNumber = Some(mandatoryPage(FirstTransporterVatPage)),
      eoriNumber = None
    )

  def applyGuarantor(guarantorArranger: GuarantorArranger)(implicit request: DataRequest[_]): TraderModel = {
    guarantorArranger match {
      case GuarantorArranger.Consignor => applyConsignor.copy(traderExciseNumber = None)
      case GuarantorArranger.Consignee =>
        val consigneeTrader = applyConsignee
        TraderModel(
          traderExciseNumber = None,
          traderName = consigneeTrader.traderName,
          address = consigneeTrader.address,
          vatNumber = request.userAnswers.get(ConsigneeExportInformationPage).flatMap(_.vatNumber),
          eoriNumber = None
        )
      case _ => TraderModel(
        traderExciseNumber = None,
        traderName = Some(mandatoryPage(GuarantorNamePage)),
        address = Some(AddressModel.fromUserAddress(mandatoryPage(GuarantorAddressPage))),
        vatNumber = Some(mandatoryPage(GuarantorVatPage)),
        eoriNumber = None
      )
    }
  }

  implicit val fmt: Format[TraderModel] = Json.format
}
