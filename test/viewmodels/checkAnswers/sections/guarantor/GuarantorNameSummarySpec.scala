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
import fixtures.messages.sections.guarantor.GuarantorNameMessages
import fixtures.messages.sections.guarantor.GuarantorNameMessages.ViewMessages
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.{CheckMode, Mode, NormalMode}
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeBusinessNamePage
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorNamePage, GuarantorRequiredPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._

class GuarantorNameSummarySpec extends SpecBase with Matchers {

  "GuarantorNameSummary" - {

    def expectedRow(value: String, showChangeLink: Boolean, mode: Mode = CheckMode)(implicit messagesForLanguage: ViewMessages): Option[SummaryListRow] = {
      Some(
        SummaryListRowViewModel(
          key = Key(Text(messagesForLanguage.cyaLabel)),
          value = Value(Text(value)),
          actions = if (!showChangeLink) Seq() else Seq(ActionItemViewModel(
            content = Text(messagesForLanguage.change),
            href = controllers.sections.guarantor.routes.GuarantorNameController.onPageLoad(testErn, testArc, mode).url,
            id = "changeGuarantorName"
          ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
        )
      )
    }

    Seq(GuarantorNameMessages.English).foreach { implicit messagesForLanguage =>

      s"when language is set to ${messagesForLanguage.lang.code}" - {
        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "and there is no answer for the GuarantorRequiredPage" - {
          "then must not return a row" in {
            implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

            GuarantorNameSummary.row mustBe None
          }
        }

        "and there is a GuarantorRequiredPage answer of `no`" - {
          "then must not return a row" in {
            implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(GuarantorRequiredPage, false))

            GuarantorNameSummary.row mustBe None
          }
        }

        "and there is a GuarantorRequiredPage answer of `yes`" - {

          "and there is a GuarantorArrangerPage answer of `Consignee`" - {
            "and that section hasn't been filled in yet" in {

              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, Consignee)
              )

              GuarantorNameSummary.row mustBe expectedRow(messagesForLanguage.consigneeNameNotProvided, false)
            }

            "and that section has been filled in" in {
              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, Consignee)
                  .set(ConsigneeBusinessNamePage, "consignee name here")
              )

              GuarantorNameSummary.row mustBe expectedRow("consignee name here", false)
            }
          }

          "and there is a GuarantorArrangerPage answer of `Consignor`" - {
            "and that section has been filled in" in {
              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, Consignor)
              )

              GuarantorNameSummary.row mustBe expectedRow(testMinTraderKnownFacts.traderName, false)
            }
          }

          "and there is a GuarantorArrangerPage answer of `GoodsOwner`" - {
            "and that section hasn't been filled in yet" in {

              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, GoodsOwner)
              )

              GuarantorNameSummary.row mustBe expectedRow(messagesForLanguage.notProvided, showChangeLink = true, NormalMode)
            }

            "and that section has been filled in" in {
              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, GoodsOwner)
                  .set(GuarantorNamePage, "guarantor name here")
              )

              GuarantorNameSummary.row mustBe expectedRow("guarantor name here", true)
            }
          }

          "and there is a GuarantorArrangerPage answer of `Transporter`" - {
            "and that section hasn't been filled in yet" in {

              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, Transporter)
              )

              GuarantorNameSummary.row mustBe expectedRow(messagesForLanguage.notProvided, showChangeLink = true, NormalMode)
            }

            "and that section has been filled in" in {
              implicit lazy val request: DataRequest[_] = dataRequest(
                FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorRequiredPage, true)
                  .set(GuarantorArrangerPage, Transporter)
                  .set(GuarantorNamePage, "transporter name here")
              )

              GuarantorNameSummary.row mustBe expectedRow("transporter name here", true)
            }
          }
        }
      }

    }

  }
}
