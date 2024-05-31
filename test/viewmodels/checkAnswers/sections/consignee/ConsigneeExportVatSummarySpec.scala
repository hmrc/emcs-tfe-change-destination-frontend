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

package viewmodels.checkAnswers.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExportVatMessages
import models.CheckMode
import models.sections.info.ChangeType
import models.sections.info.movementScenario.DestinationType.Export
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeExportVatPage
import pages.sections.info.ChangeTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ConsigneeExportVatSummarySpec extends SpecBase with Matchers {
  "ConsigneeExportVatSummary" - {

    Seq(ConsigneeExportVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "when the Change Type is Consignee" - {

            "must output None (i.e. don't use the IE801 data)" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ChangeTypePage, ChangeType.ChangeConsignee))

              ConsigneeExportVatSummary.row mustBe None
            }
          }

          "when the Change Type is NOT Consignee and destination type is Export" - {

            "must output data from the IE801" in {

              implicit lazy val request = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers.set(ChangeTypePage, ChangeType.Destination),
                movementDetails = maxGetMovementResponse.copy(destinationType = Export)
              )

              ConsigneeExportVatSummary.row mustBe
                Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaLabel,
                    value = Value(Text("ConsigneeTraderId")),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(testErn, testArc, CheckMode).url,
                        id = "changeConsigneeExportVat"
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                    )
                  )
                )
            }
          }
        }

        "when there's an answer" - {


          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExportVatPage, testVatNumber))

            ConsigneeExportVatSummary.row mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(testVatNumber)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.consignee.routes.ConsigneeExportVatController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeConsigneeExportVat"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }
        }
      }
    }
  }
}
