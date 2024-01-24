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

package viewmodels.helpers

import models.UserType._
import models.requests.DataRequest
import models.response.{InvalidUserTypeException, MissingMandatoryPage}
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheUk, _}
import pages.sections.consignee.ConsigneeSection
import pages.sections.destination.DestinationSection
import pages.sections.exportInformation.ExportInformationSection
import pages.sections.firstTransporter.FirstTransporterSection
import pages.sections.guarantor.GuarantorSection
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import pages.sections.journeyType.JourneyTypeSection
import pages.sections.movement.MovementSection
import pages.sections.transportArranger.TransportArrangerSection
import pages.sections.transportUnit.TransportUnitsSection
import play.api.i18n.Messages
import utils.Logging
import viewmodels.taskList._

import javax.inject.Inject

class TaskListHelper @Inject()() extends Logging {

  // disable for "line too long" warnings
  // noinspection ScalaStyle
  def heading(implicit request: DataRequest[_], messages: Messages): String =
    (request.userTypeFromErn, request.userAnswers.get(DestinationTypePage)) match {
      case (GreatBritainWarehouseKeeper, Some(GbTaxWarehouse)) =>
        messages("taskList.heading.gbTaxWarehouseTo", messages(Seq(s"taskList.heading.$GbTaxWarehouse", s"destinationType.$GbTaxWarehouse")))

      case (NorthernIrelandWarehouseKeeper, Some(destinationType@(GbTaxWarehouse | EuTaxWarehouse | DirectDelivery | RegisteredConsignee | TemporaryRegisteredConsignee | ExemptedOrganisation | UnknownDestination))) =>
        request.userAnswers.get(DispatchPlacePage) match {
          case Some(value) =>
            messages("taskList.heading.dispatchPlaceTo", messages(s"dispatchPlace.$value"), messages(Seq(s"taskList.heading.$destinationType", s"destinationType.$destinationType")))
          case None =>
            logger.error(s"[heading] Missing mandatory page $DispatchPlacePage for $NorthernIrelandWarehouseKeeper")
            throw MissingMandatoryPage(s"[heading] Missing mandatory page $DispatchPlacePage for $NorthernIrelandWarehouseKeeper")
        }

      case (GreatBritainRegisteredConsignor | NorthernIrelandRegisteredConsignor, Some(destinationType)) =>
        messages("taskList.heading.importFor", messages(s"destinationType.$destinationType"))

      case (GreatBritainWarehouseKeeper | NorthernIrelandWarehouseKeeper, Some(destinationType@(ExportWithCustomsDeclarationLodgedInTheUk | ExportWithCustomsDeclarationLodgedInTheEu))) =>
        messages(s"destinationType.$destinationType")

      case (userType, destinationType) =>
        logger.error(s"[heading] invalid UserType and destinationType combination for CAM journey: $userType | $destinationType")
        throw InvalidUserTypeException(s"[TaskListHelper][heading] invalid UserType and destinationType combination for CAM journey: $userType | $destinationType")
    }

  private[helpers] def movementSection(implicit request: DataRequest[_], messages: Messages): TaskListSection = TaskListSection(
    sectionHeading = messages("taskList.section.movement"),
    rows = Seq(
      TaskListSectionRow(
        taskName = messages("taskList.section.movement.movementDetails"),
        id = "movementDetails",
        link = Some(controllers.sections.movement.routes.MovementIndexController.onPageLoad(request.ern, request.arc).url),
        section = Some(MovementSection),
        status = Some(MovementSection.status)
      )
    )
  )

  //noinspection ScalaStyle
  private[helpers] def deliverySection(implicit request: DataRequest[_], messages: Messages): TaskListSection = {
    TaskListSection(
      sectionHeading = messages("taskList.section.delivery"),
      rows = Seq(
        if (ConsigneeSection.canBeCompletedForTraderAndDestinationType) {
          Some(TaskListSectionRow(
            taskName = messages("taskList.section.delivery.consignee"),
            id = "consignee",
            link = Some(controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(request.ern, request.arc).url),
            section = Some(ConsigneeSection),
            status = Some(ConsigneeSection.status)
          ))
        } else {
          None
        },
        if (DestinationSection.canBeCompletedForTraderAndDestinationType) {
          Some(TaskListSectionRow(
            taskName = messages("taskList.section.delivery.destination"),
            id = "destination",
            link = Some(controllers.sections.destination.routes.DestinationIndexController.onPageLoad(request.ern, request.arc).url),
            section = Some(DestinationSection),
            status = Some(DestinationSection.status)
          ))
        } else {
          None
        },
        if (ExportInformationSection.canBeCompletedForTraderAndDestinationType) {
          Some(TaskListSectionRow(
            taskName = messages("taskList.section.delivery.export"),
            id = "export",
            link = Some(controllers.sections.exportInformation.routes.ExportInformationIndexController.onPageLoad(request.ern, request.arc).url),
            section = Some(ExportInformationSection),
            status = Some(ExportInformationSection.status)
          ))
        } else {
          None
        }
      ).flatten
    )
  }



  private[helpers] def guarantorSection(implicit request: DataRequest[_], messages: Messages): Option[TaskListSection] =
    Option.when(GuarantorSection.requiresGuarantorToBeProvided) {
      TaskListSection(
        sectionHeading = messages("taskList.section.guarantor"),
        rows = Seq(
          Some(TaskListSectionRow(
            taskName = messages("taskList.section.guarantor.guarantor"),
            id = "guarantor",
            link = Some(controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(request.ern, request.arc).url),
            section = Some(GuarantorSection),
            status = Some(GuarantorSection.status)
          ))
        ).flatten
      )
    }

  private[helpers] def transportSection(implicit request: DataRequest[_], messages: Messages): TaskListSection = {
    TaskListSection(
      sectionHeading = messages("taskList.section.transport"),
      rows = Seq(
        Some(TaskListSectionRow(
          taskName = messages("taskList.section.transport.journeyType"),
          id = "journeyType",
          link = Some(controllers.sections.journeyType.routes.JourneyTypeIndexController.onPageLoad(request.ern, request.arc).url),
          section = Some(JourneyTypeSection),
          status = Some(JourneyTypeSection.status)
        )),
        Some(TaskListSectionRow(
          taskName = messages("taskList.section.transport.transportArranger"),
          id = "transportArranger",
          link = Some(controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(request.ern, request.arc).url),
          section = Some(TransportArrangerSection),
          status = Some(TransportArrangerSection.status)
        )),
        Some(TaskListSectionRow(
          taskName = messages("taskList.section.transport.firstTransporter"),
          id = "firstTransporter",
          link = Some(controllers.sections.firstTransporter.routes.FirstTransporterIndexController.onPageLoad(request.ern, request.arc).url),
          section = Some(FirstTransporterSection),
          status = Some(FirstTransporterSection.status)
        )),
        Some(TaskListSectionRow(
          taskName = messages("taskList.section.transport.units"),
          id = "units",
          link = Some(controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(request.ern, request.arc).url),
          section = Some(TransportUnitsSection),
          status = Some(TransportUnitsSection.status)
        ))
      ).flatten
    )
  }

  private def sectionsExceptSubmit(implicit request: DataRequest[_], messages: Messages): Seq[TaskListSection] = Seq(
    Some(movementSection),
    Some(deliverySection),
    guarantorSection,
    Some(transportSection)
  ).flatten

  private[helpers] def submitSection(sectionsExceptSubmit: Seq[TaskListSection])
                                          (implicit request: DataRequest[_], messages: Messages): TaskListSection = {

    val rows: Seq[TaskListSectionRow] = sectionsExceptSubmit.flatMap(_.rows).filter(_.section.exists(_.canBeCompletedForTraderAndDestinationType))

    val completed: Boolean = rows.nonEmpty && rows.forall(_.status.contains(Completed))

    TaskListSection(
      sectionHeading = messages("taskList.section.submit"),
      rows = Seq(TaskListSectionRow(
        taskName = messages("taskList.section.submit"),
        id = "submit",
        link = if (completed) Some(controllers.routes.DeclarationController.onPageLoad(request.ern, request.arc).url) else None,
        section = None,
        status = if (completed) None else Some(CannotStartYet)
      ))
    )
  }

  def sections(implicit request: DataRequest[_], messages: Messages): Seq[TaskListSection] =
    sectionsExceptSubmit :+ submitSection(sectionsExceptSubmit)

}
