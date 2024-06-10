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

package pages.sections.consignee

import models.Enumerable
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation, VatNumber}
import models.sections.info.ChangeType.ChangeConsignee
import models.sections.info.movementScenario.MovementScenario.UnknownDestination
import pages.QuestionPage
import pages.sections.Section
import pages.sections.info.{ChangeTypePage, DestinationTypePage}
import play.api.libs.json.{Format, JsObject, JsPath, Reads}
import viewmodels.taskList._

import scala.annotation.unused
import scala.collection.Seq

case object ConsigneeSection extends Section[JsObject] with Enumerable.Implicits {
  override val path: JsPath = JsPath \ "consignee"

  override def status(implicit request: DataRequest[_]): TaskListStatus = {
    lazy val exportVatPageAnswer = request.userAnswers.get(ConsigneeExportVatPage)
    lazy val exportEoriPageAnswer = request.userAnswers.get(ConsigneeExportEoriPage)
    val pagesToCheck = (
      request.userAnswers.get(ConsigneeExportInformationPage),
      request.userAnswers.get(ConsigneeExcisePage),
      request.userAnswers.get(ConsigneeExemptOrganisationPage)
    ) match {
      case (Some(value), _, _) =>
        value.toList match {
          case NoInformation :: Nil => Seq(Some(NoInformation.toString))
          case VatNumber :: Nil => Seq(exportVatPageAnswer)
          case EoriNumber :: Nil => Seq(exportEoriPageAnswer)
          //Both VAT and EORI selected - check both answers exist
          case _ => Seq(exportVatPageAnswer, exportEoriPageAnswer)
        }
      case (_, Some(value), _) => Seq(Some(value))
      case (_, _, Some(value)) => Seq(Some(value).map(_.certificateSerialNumber))
      case _ => Seq(None)
    }
    checkBusinessNameAndAddressBothExistWithPage(pagesToCheck)
  }

  /**
   * @param pageGetResults result from page answer retrievals
   * @param request       DataRequest
   * @param rds           unused, but required to ensure that the value passed in is readable (as opposed to something like Some(ConsigneeExportInformationPage)
   * @tparam A            type used for pageGetResult and rds
   * @return
   */
  private def checkBusinessNameAndAddressBothExistWithPage[A](pageGetResults: Seq[Option[A]])
                                                             (implicit request: DataRequest[_], @unused rds: Reads[A]): TaskListStatus = {
    val pages: Seq[Option[_]] = Seq(
      request.userAnswers.get(ConsigneeBusinessNamePage),
      request.userAnswers.get(ConsigneeAddressPage)
    ) ++ pageGetResults

    if (pages.forall(_.nonEmpty)) {
      Completed
    } else if (pages.exists(_.nonEmpty)) {
      InProgress
    } else {
      NotStarted
    }
  }

  def hasChanged(implicit request: DataRequest[_]): Boolean =
    request.userAnswers.get(ChangeTypePage).contains(ChangeConsignee)

  override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean =
    hasChanged && !request.userAnswers.get(DestinationTypePage).contains(UnknownDestination)

  def hasConsigneeChanged(implicit request: DataRequest[_]): Boolean = {

    def isDifferentFromIE801[A](page: QuestionPage[A])(implicit format: Format[A]) =
      request.userAnswers.get(page) != page.getValueFromIE801

    Seq(
      isDifferentFromIE801(ConsigneeExcisePage),
      isDifferentFromIE801(ConsigneeExemptOrganisationPage),
      isDifferentFromIE801(ConsigneeBusinessNamePage),
      isDifferentFromIE801(ConsigneeAddressPage),
      isDifferentFromIE801(ConsigneeExportVatPage),
      isDifferentFromIE801(ConsigneeExportEoriPage)
    ).contains(true)
  }
}
