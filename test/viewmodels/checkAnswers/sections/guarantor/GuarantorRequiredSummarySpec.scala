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
import fixtures.messages.sections.guarantor.GuarantorRequiredMessages
import fixtures.messages.sections.guarantor.GuarantorRequiredMessages.ViewMessages
import models.CheckMode
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheUk, UkTaxWarehouse}
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import viewmodels.govuk.summarylist._


class GuarantorRequiredSummarySpec extends SpecBase {

  private def expectedRow(value: String, ern: String = testErn, showChangeLink: Boolean = true)
                         (implicit messagesForLanguage: ViewMessages): SummaryListRow =
    SummaryListRowViewModel(
      key = Key(Text(messagesForLanguage.cyaLabel)),
      value = Value(Text(value)),
      actions = if(!showChangeLink) Seq() else Seq(ActionItemViewModel(
        content = Text(messagesForLanguage.change),
        href = controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(ern, testArc, CheckMode).url,
        id = "changeGuarantorRequired"
      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
    )

  Seq(GuarantorRequiredMessages.English).foreach { implicit messagesForLanguage =>

    s"when language is set to ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      Seq(true, false).foreach { isOnReviewPage =>

        s"when isOnReviewPage is '$isOnReviewPage'" - {

          "when a guarantor is mandatory (i.e. not EUtoNI Energy Fixed transport OR not UKtoUK beer/wine)" - {

            "must render GuarantorRequired as `Yes` with no change link" in {

              implicit lazy val request = dataRequest(
                FakeRequest(),
                emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
              )

              GuarantorRequiredSummary.row(isOnReviewPage) mustBe expectedRow(value = messagesForLanguage.yes, showChangeLink = false)
            }
          }

          "when a guarantor is NOT mandatory (i.e. EUtoNI Energy Fixed transport OR UKtoUK beer/wine)" - {

            val uktoUkUserAnswers = emptyUserAnswers.copy(ern = testGreatBritainWarehouseErn).set(DestinationTypePage, UkTaxWarehouse.GB)

            "and there is no answer for the GuarantorRequiredPage" - {

              "then must return the answer from the IE801" in {

                implicit lazy val request = dataRequest(FakeRequest(), uktoUkUserAnswers, testGreatBritainWarehouseErn)

                GuarantorRequiredSummary.row(isOnReviewPage) mustBe expectedRow(
                  value = messagesForLanguage.yes,
                  ern = testGreatBritainWarehouseErn,
                  showChangeLink = !isOnReviewPage
                )
              }
            }

            "and there is a GuarantorRequiredPage answer of yes" - {

              "then must return a row with the answer of yes " in {

                implicit lazy val request = dataRequest(FakeRequest(), uktoUkUserAnswers.set(GuarantorRequiredPage, true), testGreatBritainWarehouseErn)

                GuarantorRequiredSummary.row(isOnReviewPage) mustBe expectedRow(
                  value = messagesForLanguage.yes,
                  ern = testGreatBritainWarehouseErn,
                  showChangeLink = !isOnReviewPage
                )
              }
            }

            "and there is a GuarantorRequiredPage answer of no" - {

              "then must return a row with the answer " in {

                implicit lazy val request = dataRequest(FakeRequest(), uktoUkUserAnswers.set(GuarantorRequiredPage, false), testGreatBritainWarehouseErn)

                GuarantorRequiredSummary.row(isOnReviewPage) mustBe expectedRow(
                  value = messagesForLanguage.no,
                  ern = testGreatBritainWarehouseErn,
                  showChangeLink = !isOnReviewPage
                )
              }
            }
          }
        }
      }
    }
  }
}
