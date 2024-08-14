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

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorVatMessages
import fixtures.messages.sections.guarantor.GuarantorVatMessages.ViewMessages
import models.CheckMode
import models.requests.DataRequest
import models.response.emcsTfe.{GuarantorType, MovementGuaranteeModel}
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheUk, UkTaxWarehouse}
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage, GuarantorVatPage}
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import viewmodels.govuk.summarylist._

class GuarantorEnVatSummarySpec extends SpecBase with Matchers {

  "GuarantorErnVatSummary" - {

    def expectedRow(key: String = "VAT registration number", value: String, showChangeLink: Boolean)(implicit messagesForLanguage: ViewMessages): Seq[SummaryListRow] = {
      Seq(
        SummaryListRowViewModel(
          key = Key(Text(key)),
          value = Value(Text(value)),
          actions = if (!showChangeLink) Seq() else Seq(ActionItemViewModel(
            content = Text(messagesForLanguage.change),
            href = controllers.sections.guarantor.routes.GuarantorVatController.onPageLoad(testErn, testArc, CheckMode).url,
            id = "changeGuarantorVat"
          ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
        )
      )
    }

    Seq(GuarantorVatMessages.English).foreach { implicit messagesForLanguage =>

      s"when language is set to ${messagesForLanguage.lang.code}" - {
        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "and there is a GuarantorArrangerPage answer of `Consignee`" - {
          "then must return not complete" in {
            implicit lazy val request: DataRequest[_] = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                .set(GuarantorArrangerPage, Consignee)
            )

            GuarantorErnVatSummary.rows() mustBe expectedRow(key = messagesForLanguage.cyaVatNumberForExports,value = messagesForLanguage.sectionNotComplete("Consignee"), showChangeLink = false)
          }

          "then must return the consignee ERN" in {
            implicit lazy val request: DataRequest[_] = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(DestinationTypePage, UkTaxWarehouse.GB)
                .set(GuarantorRequiredPage, true)
                .set(GuarantorArrangerPage, Consignee)
                .set(ConsigneeExcisePage, "XIRC123456789"),
            )

            GuarantorErnVatSummary.rows() mustBe expectedRow(messagesForLanguage.cyaErnLabel, request.ern, false)
          }
        }

        "and there is a GuarantorArrangerPage answer of `Consignor`" - {
          "then must return a ERN row" in {
            implicit lazy val request: DataRequest[_] = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                .set(GuarantorArrangerPage, Consignor)
            )

            GuarantorErnVatSummary.rows() mustBe expectedRow(messagesForLanguage.cyaErnLabel, testErn, false)
          }
        }

        "and there is a GuarantorArrangerPage answer of `GoodsOwner`" - {
          "and there is no answer for GuarantorVatPage in 801 or user answers" - {
            "then must render not provided row with change link" in {
              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                  .set(GuarantorArrangerPage, GoodsOwner),
                movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(GuarantorType.Owner, None))
              )

              GuarantorErnVatSummary.rows() mustBe expectedRow(value = messagesForLanguage.notProvided, showChangeLink = true)
            }
          }

          "and there is a GuarantorVatPage answer" - {
            "then must render row with value and change link" in {
              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                  .set(GuarantorArrangerPage, GoodsOwner)
                  .set(GuarantorVatPage,"VAT123")
              )

              GuarantorErnVatSummary.rows() mustBe expectedRow(key = messagesForLanguage.cyaVatInputLabel, value = "VAT123", showChangeLink = true)
            }
          }
        }

        "and there is a GuarantorArrangerPage answer of `Transporter`" - {
          "and there is no answer for GuarantorVatPage in 801 or user answers" - {
            "then must render not provided row with change link" in {
              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                  .set(GuarantorArrangerPage, Transporter),
                movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(GuarantorType.Transporter, None))

              )

              GuarantorErnVatSummary.rows() mustBe expectedRow(value = messagesForLanguage.notProvided, showChangeLink =  true)
            }
          }

          "and there is a GuarantorVatPage answer" - {
            "then must render row with value and change link" in {
              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                  .set(GuarantorArrangerPage, Transporter)
                  .set(GuarantorVatPage, "TRAN123")
              )

              GuarantorErnVatSummary.rows() mustBe expectedRow(key = messagesForLanguage.cyaVatInputLabel, value = "TRAN123", showChangeLink = true)
            }
          }
        }
      }
    }
  }
}