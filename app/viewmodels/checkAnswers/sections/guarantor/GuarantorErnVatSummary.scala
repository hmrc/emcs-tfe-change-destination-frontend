/*
 * Copyright 2024 HM Revenue & Customs
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

package viewmodels.checkAnswers.sections.guarantor

import models.CheckMode
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheEu, ExportWithCustomsDeclarationLodgedInTheUk, TemporaryCertifiedConsignee, TemporaryRegisteredConsignee}
import pages.sections.consignee.{ConsigneeExcisePage, ConsigneeExportVatPage}
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage, GuarantorVatPage}
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object GuarantorErnVatSummary {

  def rows(onReviewPage: Boolean = false)(implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {

    val guarantorArrangerAnswer = GuarantorArrangerPage.value
    val guarantorRequiredAnswerIsTrue = GuarantorRequiredPage.value.contains(true)

    (guarantorArrangerAnswer, guarantorRequiredAnswerIsTrue) match {
      case (Some(arranger), true) => renderRows(arranger, onReviewPage)
      case _ => Seq.empty
    }
  }

  def renderRows(arranger: GuarantorArranger, onReviewPage: Boolean)(implicit request: DataRequest[_], messages: Messages): Seq[SummaryListRow] = {

    val rows = arranger match {
      case Consignor => Seq(getConsignorSummary())
      case Consignee => Seq(getConsigneeSummary())
      case _ => getGuarantorVatSummary()
    }

    val changeAction = arranger match {
      case GoodsOwner | Transporter =>
        Seq(
          ActionItemViewModel(
            "site.change",
            controllers.sections.guarantor.routes.GuarantorVatController.onPageLoad(request.ern, request.arc, CheckMode).url,
            "changeGuarantorVat"
          ).withVisuallyHiddenText(messages("guarantorVat.checkYourAnswers.change.hidden"))
        )
      case _ =>
        Seq().empty
    }

    rows.map { case (key, value) =>
      SummaryListRowViewModel(key, ValueViewModel(value), if(onReviewPage) Seq() else changeAction)
    }
  }

  private def getConsignorSummary()(implicit request: DataRequest[_]): (String, String) =
    "guarantorErn.checkYourAnswers.label" -> request.ern

  private def getConsigneeSummary()(implicit request: DataRequest[_], messages: Messages): (String, String) =
    (
      DestinationTypePage.value,
      ConsigneeExcisePage.value,
      ConsigneeExportVatPage.value
    ) match {

      case (Some(TemporaryRegisteredConsignee), maybeErn, _) =>
        "consigneeExcise.checkYourAnswersLabel.ernForTemporaryRegisteredConsignee" ->
          maybeErn.map(HtmlFormat.escape(_).toString()).getOrElse(messages("guarantorErn.checkYourAnswers.notProvided", messages(s"guarantorArranger.$Consignee")))

      case (Some(TemporaryCertifiedConsignee), maybeErn, _) =>
        "consigneeExcise.checkYourAnswersLabel.ernForTemporaryCertifiedConsignee" ->
          maybeErn.map(HtmlFormat.escape(_).toString()).getOrElse(messages("guarantorErn.checkYourAnswers.notProvided", messages(s"guarantorArranger.$Consignee")))

      case (Some(ExportWithCustomsDeclarationLodgedInTheUk) | Some(ExportWithCustomsDeclarationLodgedInTheEu), _, maybeVat) =>
        "consigneeExportVat.checkYourAnswersLabel" ->
          maybeVat.map(HtmlFormat.escape(_).toString()).getOrElse(messages("guarantorErn.checkYourAnswers.notProvided", messages(s"guarantorArranger.$Consignee")))

      case (_, Some(ern), _) =>
        "guarantorErn.checkYourAnswers.label" -> HtmlFormat.escape(ern).toString()

      case (_, _, Some(vatNumber)) =>
        "guarantorVat.checkYourAnswers.label" -> HtmlFormat.escape(vatNumber).toString()

      case _ =>
        "guarantorErn.checkYourAnswers.label" -> messages("guarantorErn.checkYourAnswers.notProvided", messages(s"guarantorArranger.$Consignee"))
    }

  private def getGuarantorVatSummary()(implicit request: DataRequest[_]): Seq[(String, String)] =
    GuarantorVatPage.value match {
      case Some(vatNumber) =>
        Seq(
          "guarantorVat.checkYourAnswers.label" -> HtmlFormat.escape(vatNumber).toString()
        )
      case None =>
        Seq("guarantorVat.checkYourAnswers.label" -> "site.notProvided")
    }

}
