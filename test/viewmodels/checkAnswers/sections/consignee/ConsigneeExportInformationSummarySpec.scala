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

package viewmodels.checkAnswers.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExportInformationMessages
import models.CheckMode
import models.sections.consignee.ConsigneeExportInformation
import models.sections.consignee.ConsigneeExportInformationType.YesEoriNumber
import models.sections.info.ChangeType
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeExportInformationPage
import pages.sections.info.ChangeTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ConsigneeExportInformationSummarySpec extends SpecBase with Matchers {
  "ConsigneeExportInformationSummary" - {

    Seq(ConsigneeExportInformationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "when the ChangeType is `Consignee`" - {

            "must output None" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ChangeTypePage, ChangeType.ChangeConsignee))

              ConsigneeExportInformationSummary.row mustBe None
            }
          }

          "when the ChangeType is NOT `Consignee`" - {

            "must output data from the IE801 (where present)" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ChangeTypePage, ChangeType.Destination))

              ConsigneeExportInformationSummary.row mustBe
                Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaEoriLabel,
                    value = Value(Text(maxGetMovementResponse.consigneeTrader.get.eoriNumber.get)),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, CheckMode).url,
                        id = "changeConsigneeExportInformation"
                      ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                    )
                  )
                )
            }
          }
        }

        "when there's an answer" - {

          "when the show action link boolean is true" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeExportInformationPage, ConsigneeExportInformation(YesEoriNumber, None, testEori.eoriNumber)))

              ConsigneeExportInformationSummary.row mustBe
                Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaEoriLabel,
                    value = Value(Text(testEori.eoriNumber.get)),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, CheckMode).url,
                        id = "changeConsigneeExportInformation"
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
}
