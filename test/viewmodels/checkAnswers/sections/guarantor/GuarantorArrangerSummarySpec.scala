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

package viewmodels.checkAnswers.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorArrangerMessages
import fixtures.messages.sections.guarantor.GuarantorArrangerMessages.ViewMessages
import models.CheckMode
import models.response.emcsTfe.{GuarantorType, MovementGuaranteeModel}
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.sections.info.movementScenario.MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk
import pages.sections.guarantor.GuarantorArrangerPage
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._

class GuarantorArrangerSummarySpec extends SpecBase {
  private def expectedRow(value: String, ern: String = testErn)(implicit messagesForLanguage: ViewMessages): SummaryListRow =
    SummaryListRowViewModel(
      key = Key(Text(messagesForLanguage.cyaLabel)),
      value = Value(Text(value)),
      actions = Seq(ActionItemViewModel(
        content = Text(messagesForLanguage.change),
        href = controllers.sections.guarantor.routes.GuarantorArrangerController.onPageLoad(ern, testArc, CheckMode).url,
        id = "changeGuarantorArranger"
      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
    )

  Seq(GuarantorArrangerMessages.English).foreach { implicit messagesForLanguage =>
    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      "and there is no answer for the GuarantorArrangerPage (requires new guarantor details)" in {
        implicit lazy val request = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
          ern = testGreatBritainWarehouseErn,
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(GuarantorType.Consignee, None))
        )

        GuarantorArrangerSummary.row() mustBe expectedRow(messagesForLanguage.notProvided, testGreatBritainWarehouseErn)
      }

      Seq(
        (Consignee, messagesForLanguage.consigneeRadioOption),
        (Consignor, messagesForLanguage.consignorRadioOption),
        (GoodsOwner, messagesForLanguage.goodsOwnerRadioOption),
        (Transporter, messagesForLanguage.transporterRadioOption)
      ).foreach {
        case (arranger, expectedMessage) =>
          s"and there is a GuarantorArrangerPage answer of `${arranger.getClass.getSimpleName.stripSuffix("$")}`" in {
            implicit lazy val request = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                .set(GuarantorArrangerPage, arranger)
            )

            GuarantorArrangerSummary.row() mustBe expectedRow(expectedMessage)
          }
      }

    }
  }
}
