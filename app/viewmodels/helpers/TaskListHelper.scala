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

import models.MovementValidationFailure
import models.UserType._
import models.requests.DataRequest
import models.response.{InvalidUserTypeException, MissingMandatoryPage}
import models.sections.info.ChangeType.ReturnToConsignor
import models.sections.info.movementScenario.MovementScenario._
import pages.sections.consignee.ConsigneeSection
import pages.sections.destination.DestinationSection
import pages.sections.exportInformation.ExportInformationSection
import pages.sections.firstTransporter.FirstTransporterSection
import pages.sections.guarantor.GuarantorSection
import pages.sections.info.{ChangeTypePage, DestinationTypePage, DispatchPlacePage}
import pages.sections.journeyType.JourneyTypeSection
import pages.sections.movement.MovementSection
import pages.sections.transportArranger.TransportArrangerSection
import pages.sections.transportUnit.TransportUnitsSection
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.{TaskListItemStatus, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.tasklist.{TaskListItem, TaskListItemTitle}
import utils.Logging
import viewmodels.taskList._
import views.html.components.{list, p}

import javax.inject.Inject

class TaskListHelper @Inject()(list: list, p: p) extends Logging {

  // disable for "line too long" warnings
  // noinspection ScalaStyle
  def heading(implicit request: DataRequest[_], messages: Messages): String =
    (request.userTypeFromErn, request.userAnswers.get(DestinationTypePage)) match {
      case (GreatBritainWarehouseKeeper, Some(UkTaxWarehouse.GB)) =>
        messages("taskList.heading.gbTaxWarehouseTo", messages(Seq(s"taskList.heading.${UkTaxWarehouse.GB}", s"destinationType.${UkTaxWarehouse.GB}")))

      case (GreatBritainWarehouseKeeper, Some(UkTaxWarehouse.NI)) =>
        messages("taskList.heading.gbTaxWarehouseTo", messages(Seq(s"taskList.heading.${UkTaxWarehouse.NI}", s"destinationType.${UkTaxWarehouse.NI}")))

      case (NorthernIrelandWarehouseKeeper, Some(destinationType@(UkTaxWarehouse.GB | UkTaxWarehouse.NI | EuTaxWarehouse | DirectDelivery | RegisteredConsignee | TemporaryRegisteredConsignee | ExemptedOrganisation | UnknownDestination))) =>
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

      case (NorthernIrelandCertifiedConsignor | NorthernIrelandTemporaryCertifiedConsignor, Some(destinationType@(CertifiedConsignee | TemporaryCertifiedConsignee | ReturnToThePlaceOfDispatch))) =>
        messages(s"taskList.heading.dutyPaid.$destinationType")

      case (userType, destinationType) =>
        logger.error(s"[heading] invalid UserType and destinationType combination for CAM journey: $userType | $destinationType")
        throw InvalidUserTypeException(s"[TaskListHelper][heading] invalid UserType and destinationType combination for CAM journey: $userType | $destinationType")
    }

  private[helpers] def movementSection(implicit request: DataRequest[_], messages: Messages): TaskListSection =

    TaskListSection(
      sectionHeading = messages("taskList.section.movement"),
      rows = Seq(
        TaskListItemViewModel(
          TaskListItem(
            title = TaskListItemTitle(Text(messages("taskList.section.movement.movementDetails"))),
            status = TaskListItemStatus(Some(MovementSection.status.toTag)),
            href = Some(controllers.sections.movement.routes.MovementIndexController.onPageLoad(request.ern, request.arc).url)
          ),
          Some(MovementSection)
        )
      )
    )

  //  //noinspection ScalaStyle
  private[helpers] def deliverySection(implicit request: DataRequest[_], messages: Messages): Option[TaskListSection] = {
    if(request.userAnswers.get(ChangeTypePage).contains(ReturnToConsignor)) None else {
      Some(TaskListSection(
        sectionHeading = messages("taskList.section.delivery"),
        rows = Seq(
          Option.when(ConsigneeSection.canBeCompletedForTraderAndDestinationType) {
            TaskListItemViewModel(
              TaskListItem(
                title = TaskListItemTitle(Text(messages("taskList.section.delivery.consignee"))),
                status = TaskListItemStatus(Some(ConsigneeSection.status.toTag)),
                href = Some(controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(request.ern, request.arc).url)
              ),
              Some(ConsigneeSection)
            )
          },
          Option.when(DestinationSection.canBeCompletedForTraderAndDestinationType) {
            TaskListItemViewModel(
              TaskListItem(
                title = TaskListItemTitle(Text(messages("taskList.section.delivery.destination"))),
                status = TaskListItemStatus(Some(DestinationSection.status.toTag)),
                href = Some(controllers.sections.destination.routes.DestinationIndexController.onPageLoad(request.ern, request.arc).url)
              ),
              Some(DestinationSection)
            )
          },
          Option.when(ExportInformationSection.canBeCompletedForTraderAndDestinationType) {
            TaskListItemViewModel(
              TaskListItem(
                title = TaskListItemTitle(Text(messages("taskList.section.delivery.export"))),
                status = TaskListItemStatus(Some(ExportInformationSection.status.toTag)),
                href = Some(controllers.sections.exportInformation.routes.ExportInformationIndexController.onPageLoad(request.ern, request.arc).url)
              ),
              Some(ExportInformationSection)
            )
          }
        ).flatten
      ))
    }
  }

  private[helpers] def guarantorSection(implicit request: DataRequest[_], messages: Messages): TaskListSection =
    TaskListSection(
      sectionHeading = messages("taskList.section.guarantor"),
      rows = Seq(
        TaskListItemViewModel(
          TaskListItem(
            title = TaskListItemTitle(Text(messages("taskList.section.guarantor.guarantor"))),
            status = TaskListItemStatus(Some(GuarantorSection.status.toTag)),
            href = Some(controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(request.ern, request.arc).url)
          ),
          Some(GuarantorSection)
        )
      )
    )

  private[helpers] def transportSection(implicit request: DataRequest[_], messages: Messages): TaskListSection = {
    TaskListSection(
      sectionHeading = messages("taskList.section.transport"),
      rows = Seq(
        TaskListItemViewModel(
          TaskListItem(
            title = TaskListItemTitle(Text(messages("taskList.section.transport.journeyType"))),
            status = TaskListItemStatus(Some(JourneyTypeSection.status.toTag)),
            href = Some(controllers.sections.journeyType.routes.JourneyTypeIndexController.onPageLoad(request.ern, request.arc).url)
          ),
          Some(JourneyTypeSection)
        ),
        TaskListItemViewModel(
          TaskListItem(
            title = TaskListItemTitle(Text(messages("taskList.section.transport.transportArranger"))),
            status = TaskListItemStatus(Some(TransportArrangerSection.status.toTag)),
            href = Some(controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(request.ern, request.arc).url)
          ),
          Some(TransportArrangerSection)
        ),
        TaskListItemViewModel(
          TaskListItem(
            title = TaskListItemTitle(Text(messages("taskList.section.transport.firstTransporter"))),
            status = TaskListItemStatus(Some(FirstTransporterSection.status.toTag)),
            href = Some(controllers.sections.firstTransporter.routes.FirstTransporterIndexController.onPageLoad(request.ern, request.arc).url)
          ),
          Some(FirstTransporterSection)
        ),
        TaskListItemViewModel(
          TaskListItem(
            title = TaskListItemTitle(Text(messages("taskList.section.transport.units"))),
            status = TaskListItemStatus(Some(TransportUnitsSection.status.toTag)),
            href = Some(controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(request.ern, request.arc).url)
          ),
          Some(TransportUnitsSection)
        )
      )
    )
  }

  private def sectionsExceptSubmit(implicit request: DataRequest[_], messages: Messages): Seq[TaskListSection] = Seq(
    Some(movementSection),
    deliverySection,
    Some(guarantorSection),
    Some(transportSection)
  ).flatten

  private[helpers] def submitSection(sectionsExceptSubmit: Seq[TaskListSection])
                                    (implicit request: DataRequest[_], messages: Messages): TaskListSection = {

    val rows: Seq[TaskListItemViewModel] = sectionsExceptSubmit.flatMap(_.rows).filter(_.section.exists(_.canBeCompletedForTraderAndDestinationType))

    val completed: Boolean = rows.nonEmpty && rows.forall(_.section.map(_.status).contains(Completed))

    val status = if (completed) NotStarted else CannotStartYet

    TaskListSection(
      sectionHeading = messages("taskList.section.submit"),
      rows = Seq(TaskListItemViewModel(
        TaskListItem(
          title = TaskListItemTitle(Text(messages("taskList.section.submit"))),
          status = TaskListItemStatus(Some(status.toTag)),
          href = if (completed) Some(controllers.routes.DeclarationController.onPageLoad(request.ern, request.arc).url) else None
        ),
        None
      ))
    )
  }

  def sections(implicit request: DataRequest[_], messages: Messages): Seq[TaskListSection] =
    sectionsExceptSubmit :+ submitSection(sectionsExceptSubmit)

  def validationFailureContent(validationFailures: Seq[MovementValidationFailure])(implicit messages: Messages): HtmlContent = {
    //scalastyle:off magic.number
    val errorTypesWhichAreDuplicatedSoWeReturnTheirOwnContent: Seq[Int] = Seq(12, 13)
    //scalastyle:on magic.number

    val formattedErrorList = list(
      validationFailures.flatMap {
        failure =>
          failure.errorType.flatMap {
            errorType =>
              (errorType match {
                case et if errorTypesWhichAreDuplicatedSoWeReturnTheirOwnContent.contains(et) => failure.errorReason.map(removeAmendEntryMessageFromErrorReason)
                case _ => Some(messages(s"errors.validation.notificationBanner.$errorType.content"))
              }).map {
                pContent =>
                  p()(Html(pContent))
              }
          }
      }
    )
    HtmlContent(HtmlFormat.fill(Seq(
      p("govuk-notification-banner__heading")(Html(messages("errors.validation.notificationBanner.heading"))),
      formattedErrorList
    )))
  }

  private[helpers] def removeAmendEntryMessageFromErrorReason(errorReason: String): String =
    errorReason
      .replaceAll("\\s*Please amend your entry and resubmit\\.*", "")
      .replaceAll("origin type code is .Tax Warehouse.\\.", "origin type code is 'Tax Warehouse' or 'Duty Paid'.")
      .replaceAll("'(Import|Tax Warehouse|Duty Paid|Export)'", "‘$1’")
}
