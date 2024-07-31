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

package models.sections.info.movementScenario

import models.requests.DataRequest
import models.response.InvalidUserTypeException
import models.sections.info.movementScenario.MovementScenario.{CertifiedConsignee, DirectDelivery, EuTaxWarehouse, ExemptedOrganisation, ExportWithCustomsDeclarationLodgedInTheEu, ExportWithCustomsDeclarationLodgedInTheUk, RegisteredConsignee, TemporaryCertifiedConsignee, TemporaryRegisteredConsignee}
import models.{Enumerable, UserType, WithName}
import utils.Logging

import scala.language.postfixOps

sealed trait MovementScenario extends Logging {
  def originType(implicit request: DataRequest[_]): OriginType =
    (request.isWarehouseKeeper, request.isRegisteredConsignor, request.isCertifiedConsignor) match {
      case (true, _, _) => OriginType.TaxWarehouse
      case (_, true, _) => OriginType.Imports
      case (_, _, true) => OriginType.DutyPaid
      case _ =>
        logger.error(s"[getOriginType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][getOriginType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

  def destinationType: DestinationType

  def movementType(implicit request: DataRequest[_]): MovementType

  //TODO we should probably change this to use the messages file instead of having the message here directly
  val stringValue: String

  val isExport = Seq(ExportWithCustomsDeclarationLodgedInTheUk, ExportWithCustomsDeclarationLodgedInTheEu).contains(this)

  def isNItoEU(implicit request: DataRequest[_]) = {
    request.isNorthernIrelandErn &&
      Seq(
        DirectDelivery,
        ExemptedOrganisation,
        RegisteredConsignee,
        EuTaxWarehouse,
        TemporaryRegisteredConsignee,
        CertifiedConsignee,
        TemporaryCertifiedConsignee
      ).contains(this)
  }
}

object MovementScenario extends Enumerable.Implicits with Logging {

  //noinspection ScalaStyle - Cyclomatic Complexity
  def getMovementScenarioFromMovement(implicit request: DataRequest[_]): MovementScenario = {
    val movementDetails = request.request.movementDetails
    movementDetails.destinationType match {
      case DestinationType.TaxWarehouse =>
        if (movementDetails.deliveryPlaceTrader.flatMap(_.traderExciseNumber).exists(UserType(_).isGreatBritainErn)) {
          MovementScenario.UkTaxWarehouse.GB
        } else if (movementDetails.deliveryPlaceTrader.flatMap(_.traderExciseNumber).exists(UserType(_).isNorthernIrelandErn)) {
          MovementScenario.UkTaxWarehouse.NI
        } else {
          MovementScenario.EuTaxWarehouse
        }
      case DestinationType.RegisteredConsignee => MovementScenario.RegisteredConsignee
      case DestinationType.TemporaryRegisteredConsignee => MovementScenario.TemporaryRegisteredConsignee
      case DestinationType.DirectDelivery => MovementScenario.DirectDelivery
      case DestinationType.ExemptedOrganisation => MovementScenario.ExemptedOrganisation
      case DestinationType.Export =>
        if (movementDetails.deliveryPlaceCustomsOfficeReferenceNumber.exists(UserType(_).isGreatBritainErn) || movementDetails.deliveryPlaceCustomsOfficeReferenceNumber.exists(UserType(_).isNorthernIrelandErn)) {
          MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk
        } else {
          MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu
        }
      case DestinationType.UnknownDestination => MovementScenario.UnknownDestination
      case DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor => MovementScenario.ReturnToThePlaceOfDispatch
      case DestinationType.CertifiedConsignee => MovementScenario.CertifiedConsignee
      case DestinationType.TemporaryCertifiedConsignee => MovementScenario.TemporaryCertifiedConsignee
    }
  }

  /**
   * emcs: direct_export / import_for_direct_export
   */
  case object ExportWithCustomsDeclarationLodgedInTheUk extends WithName("exportWithCustomsDeclarationLodgedInTheUk") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.Export

    override def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.DirectExport
      case (_, true) => MovementType.ImportDirectExport
      case _ =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "export with customs declaration lodged in the United Kingdom"
  }

  /**
   * emcs: tax_warehouse_uk_to_uk / import_for_taxwarehouse_uk
   */
  object UkTaxWarehouse {

    val toList: Seq[MovementScenario] = Seq(UkTaxWarehouse.GB, UkTaxWarehouse.NI)

    private def _destinationType: DestinationType = DestinationType.TaxWarehouse

    private def _movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToUk
      case (_, true) => MovementType.ImportUk
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }


    case object GB extends WithName("gbTaxWarehouse") with MovementScenario {

      def destinationType: DestinationType = _destinationType

      def movementType(implicit request: DataRequest[_]): MovementType = _movementType

      override val stringValue: String = "tax warehouse in Great Britain"
    }

    case object NI extends WithName("niTaxWarehouse") with MovementScenario {

      def destinationType: DestinationType = _destinationType

      def movementType(implicit request: DataRequest[_]): MovementType = _movementType

      override val stringValue: String = "tax warehouse in Northern Ireland"
    }

    val values: Seq[MovementScenario] = Seq(GB, NI)
  }

  /**
   * emcs: direct_delivery / import_for_direct_delivery
   */
  case object DirectDelivery extends WithName("directDelivery") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.DirectDelivery

    override def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "direct delivery"
  }

  /**
   * emcs: tax_warehouse_uk_to_eu / import_for_taxwarehouse_eu
   */
  case object EuTaxWarehouse extends WithName("euTaxWarehouse") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.TaxWarehouse

    override def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "tax warehouse in the European Union"
  }

  /**
   * emcs: exempted_organisation / import_for_exempted_organisation
   */
  case object ExemptedOrganisation extends WithName("exemptedOrganisation") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.ExemptedOrganisation

    override def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "exempted organisation"
  }

  /**
   * emcs: indirect_export / import_for_indirect_export
   */
  case object ExportWithCustomsDeclarationLodgedInTheEu extends WithName("exportWithCustomsDeclarationLodgedInTheEu") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.Export

    override def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.IndirectExport
      case (_, true) => MovementType.ImportIndirectExport
      case _ =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "export with customs declaration lodged in the European Union"
  }

  /**
   * emcs: registered_consignee / import_for_registered_consignee
   */
  case object RegisteredConsignee extends WithName("registeredConsignee") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.RegisteredConsignee

    override def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "registered consignee"
  }

  /**
   * emcs: temp_registered_consignee / import_for_temp_registered_consignee
   */
  case object TemporaryRegisteredConsignee extends WithName("temporaryRegisteredConsignee") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.TemporaryRegisteredConsignee

    override def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "temporary registered consignee"

  }

  /**
   * emcs: certified_consignee / import_for_certified_consignee
   */
  case object CertifiedConsignee extends WithName("certifiedConsignee") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.CertifiedConsignee

    override def movementType(implicit request: DataRequest[_]): MovementType = request.isCertifiedConsignor match {
      case true => MovementType.UkToEu
      case false =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "certified consignee"
  }

  /**
   * emcs: temp_certified_consignee / import_for_temp_certified_consignee
   */
  case object TemporaryCertifiedConsignee extends WithName("temporaryCertifiedConsignee") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.TemporaryCertifiedConsignee

    override def movementType(implicit request: DataRequest[_]): MovementType = request.isCertifiedConsignor match {
      case true => MovementType.UkToEu
      case false =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "temporary certified consignee"

  }

  case object ReturnToThePlaceOfDispatch extends WithName("returnToThePlaceOfDispatch") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor

    override def movementType(implicit request: DataRequest[_]): MovementType = request.isCertifiedConsignor match {
      case true => MovementType.UkToEu
      case false =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "return to the place of dispatch of the consignor"
  }

  /**
   * emcs: unknown_destination / import_for_unknown_destination
   */
  case object UnknownDestination extends WithName("unknownDestination") with MovementScenario {

    override def destinationType: DestinationType = DestinationType.UnknownDestination

    override def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportUnknownDestination
      case _ =>
        logger.error(s"[movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for COD journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "unknown destination"
  }

  def valuesExportUkAndUkTaxWarehouse: Seq[MovementScenario] = Seq(
    ExportWithCustomsDeclarationLodgedInTheUk
  ) ++ UkTaxWarehouse.values

  def valuesEu: Seq[MovementScenario] = Seq(
    DirectDelivery,
    ExemptedOrganisation,
    ExportWithCustomsDeclarationLodgedInTheEu,
    ExportWithCustomsDeclarationLodgedInTheUk,
    RegisteredConsignee,
    EuTaxWarehouse,
    UkTaxWarehouse.GB,
    UkTaxWarehouse.NI,
    TemporaryRegisteredConsignee
  )

  def valuesForDutyPaidTraders: Seq[MovementScenario] = Seq(
    CertifiedConsignee,
    TemporaryCertifiedConsignee,
    ReturnToThePlaceOfDispatch
  )

  val values: Seq[MovementScenario] = (valuesExportUkAndUkTaxWarehouse ++ valuesEu ++ valuesForDutyPaidTraders :+ UnknownDestination).distinct

  implicit val enumerable: Enumerable[MovementScenario] = Enumerable(values.map(v => v.toString -> v): _*)
}
