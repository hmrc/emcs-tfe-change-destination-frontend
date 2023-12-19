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
import models.{Enumerable, WithName}
import utils.Logging

sealed trait MovementScenario {
  def originType(implicit request: DataRequest[_]): OriginType

  def destinationType: DestinationType

  def movementType(implicit request: DataRequest[_]): MovementType

  //TODO we should probably change this to use the messages file instead of having the message here directly
  val stringValue: String
}

object MovementScenario extends Enumerable.Implicits with Logging {

  private def getOriginType()(implicit request: DataRequest[_]): OriginType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
    case (true, _) => OriginType.TaxWarehouse
    case (_, true) => OriginType.Imports
    case _ =>
      logger.error(s"[getOriginType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
      throw InvalidUserTypeException(s"[MovementScenario][getOriginType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
  }

  /**
   * emcs: direct_export / import_for_direct_export
   */
  case object ExportWithCustomsDeclarationLodgedInTheUk extends WithName("exportWithCustomsDeclarationLodgedInTheUk") with MovementScenario {

    def originType(implicit request: DataRequest[_]): OriginType = getOriginType()

    def destinationType: DestinationType = DestinationType.Export

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.DirectExport
      case (_, true) => MovementType.ImportDirectExport
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "export with customs declaration lodged in the United Kingdom"
  }

  /**
   * emcs: tax_warehouse_uk_to_uk / import_for_taxwarehouse_uk
   */
  case object GbTaxWarehouse extends WithName("gbTaxWarehouse") with MovementScenario {

    def originType(implicit request: DataRequest[_]): OriginType = getOriginType()

    def destinationType: DestinationType = DestinationType.TaxWarehouse

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToUk
      case (_, true) => MovementType.ImportUk
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "tax warehouse in Great Britain"
  }

  /**
   * emcs: direct_delivery / import_for_direct_delivery
   */
  case object DirectDelivery extends WithName("directDelivery") with MovementScenario {

    def originType(implicit request: DataRequest[_]): OriginType = getOriginType()

    def destinationType: DestinationType = DestinationType.DirectDelivery

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "direct delivery"
  }

  /**
   * emcs: tax_warehouse_uk_to_eu / import_for_taxwarehouse_eu
   */
  case object EuTaxWarehouse extends WithName("euTaxWarehouse") with MovementScenario {

    def originType(implicit request: DataRequest[_]): OriginType = getOriginType()

    def destinationType: DestinationType = DestinationType.TaxWarehouse

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "tax warehouse in the European Union"
  }

  /**
   * emcs: exempted_organisation / import_for_exempted_organisation
   */
  case object ExemptedOrganisation extends WithName("exemptedOrganisation") with MovementScenario {

    def originType(implicit request: DataRequest[_]): OriginType = getOriginType()

    def destinationType: DestinationType = DestinationType.ExemptedOrganisation

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "exempted organisation"
  }

  /**
   * emcs: indirect_export / import_for_indirect_export
   */
  case object ExportWithCustomsDeclarationLodgedInTheEu extends WithName("exportWithCustomsDeclarationLodgedInTheEu") with MovementScenario {

    def originType(implicit request: DataRequest[_]): OriginType = getOriginType()

    def destinationType: DestinationType = DestinationType.Export

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.IndirectExport
      case (_, true) => MovementType.ImportIndirectExport
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "export with customs declaration lodged in the European Union"
  }

  /**
   * emcs: registered_consignee / import_for_registered_consignee
   */
  case object RegisteredConsignee extends WithName("registeredConsignee") with MovementScenario {

    def originType(implicit request: DataRequest[_]): OriginType = getOriginType()

    def destinationType: DestinationType = DestinationType.RegisteredConsignee

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "registered consignee"
  }

  /**
   * emcs: temp_registered_consignee / import_for_temp_registered_consignee
   */
  case object TemporaryRegisteredConsignee extends WithName("temporaryRegisteredConsignee") with MovementScenario {

    def originType(implicit request: DataRequest[_]): OriginType = getOriginType()

    def destinationType: DestinationType = DestinationType.TemporaryRegisteredConsignee

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "temporary registered consignee"

  }

  /**
   * emcs: unknown_destination / import_for_unknown_destination
   */
  case object UnknownDestination extends WithName("unknownDestination") with MovementScenario {

    def originType(implicit request: DataRequest[_]): OriginType = getOriginType()

    def destinationType: DestinationType = DestinationType.UnknownDestination

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportUnknownDestination
      case _ =>
        logger.error(s"[movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for CAM journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "unknown destination"
  }

  def valuesUk: Seq[MovementScenario] = Seq(
    ExportWithCustomsDeclarationLodgedInTheUk,
    GbTaxWarehouse
  )

  def valuesEu: Seq[MovementScenario] = Seq(
    DirectDelivery,
    ExemptedOrganisation,
    ExportWithCustomsDeclarationLodgedInTheEu,
    ExportWithCustomsDeclarationLodgedInTheUk,
    RegisteredConsignee,
    EuTaxWarehouse,
    GbTaxWarehouse,
    TemporaryRegisteredConsignee,
    UnknownDestination
  )

  val values: Seq[MovementScenario] = (valuesUk ++ valuesEu).distinct

  implicit val enumerable: Enumerable[MovementScenario] = Enumerable(values.map(v => v.toString -> v): _*)
}
