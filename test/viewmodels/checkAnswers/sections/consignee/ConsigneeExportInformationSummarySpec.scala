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
import fixtures.messages.sections.consignee.ConsigneeExportInformationMessages
import models.NormalMode
import models.sections.consignee.ConsigneeExportInformation
import models.sections.info.ChangeType
import models.sections.info.movementScenario.DestinationType.Export
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeExportInformationPage
import pages.sections.info.ChangeTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.list

class ConsigneeExportInformationSummarySpec extends SpecBase with Matchers {
  
  val list: list = app.injector.instanceOf[list]
  val summary: ConsigneeExportInformationSummary = new ConsigneeExportInformationSummary(list)

  "ConsigneeExportInformationSummary" - {

    Seq(ConsigneeExportInformationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {
        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there is no answer" - {
          "must output no summary row" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(consigneeTrader = None))
            summary.row mustBe None
          }
        }

        "when there are multiple answers" - {
          "must output the expected row" in {
            implicit lazy val request = dataRequest(
              FakeRequest(),
              emptyUserAnswers
                .set(ConsigneeExportInformationPage, ConsigneeExportInformation.values)
            )

            summary.row mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = ValueViewModel(
                    HtmlContent(list(Seq(
                      Html(messagesForLanguage.cyaValueVatNumber),
                      Html(messagesForLanguage.cyaValueEoriNumber),
                      Html(messagesForLanguage.cyaValueNoInformation)
                    )))
                  ),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url,
                      id = "changeConsigneeExportInformation"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }
        }

        Seq(
          ConsigneeExportInformation.VatNumber -> messagesForLanguage.cyaValueVatNumber,
          ConsigneeExportInformation.EoriNumber -> messagesForLanguage.cyaValueEoriNumber,
          ConsigneeExportInformation.NoInformation -> messagesForLanguage.cyaValueNoInformation,
        ).foreach {
          case (identification, text) =>
            s"when there is only one answer of $identification" - {

              "must output the expected row" in {
                implicit lazy val request = dataRequest(
                  FakeRequest(),
                  emptyUserAnswers
                    .set(ConsigneeExportInformationPage, Set(identification))
                )

                summary.row mustBe
                  Some(
                    SummaryListRowViewModel(
                      key = messagesForLanguage.cyaLabel,
                      value = ValueViewModel(
                        HtmlContent(list(Seq(
                          Html(text)
                        )))
                      ),
                      actions = Seq(
                        ActionItemViewModel(
                          content = messagesForLanguage.change,
                          href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url,
                          id = "changeConsigneeExportInformation"
                        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                      )
                    )
                  )
              }
            }
        }

        "when there's no answer" - {

          "when the Change Type is Consignee" - {

            "must output None (i.e. don't use the IE801 data)" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ChangeTypePage, ChangeType.ChangeConsignee))

              summary.row mustBe None
            }
          }

          "when the Change Type is NOT Consignee" - {

            "must output data from the IE801" - {

              "when VAT and EORI entered" in {

                implicit lazy val request = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers.set(ChangeTypePage, ChangeType.Destination),
                  movementDetails = maxGetMovementResponse.copy(destinationType = Export)
                )

                summary.row mustBe
                  Some(
                    SummaryListRowViewModel(
                      key = messagesForLanguage.cyaLabel,
                      value = ValueViewModel(
                        HtmlContent(list(Seq(
                          Html(messagesForLanguage.cyaValueVatNumber),
                          Html(messagesForLanguage.cyaValueEoriNumber)
                        )))
                      ),
                      actions = Seq(
                        ActionItemViewModel(
                          content = messagesForLanguage.change,
                          href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url,
                          id = "changeConsigneeExportInformation"
                        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                      )
                    )
                  )
              }

              "when only VAT has been entered" in {

                implicit lazy val request = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers.set(ChangeTypePage, ChangeType.Destination),
                  movementDetails = maxGetMovementResponse.copy(destinationType = Export, consigneeTrader = maxGetMovementResponse.consigneeTrader.map(_.copy(eoriNumber = None)))
                )

                summary.row mustBe
                  Some(
                    SummaryListRowViewModel(
                      key = messagesForLanguage.cyaLabel,
                      value = ValueViewModel(
                        HtmlContent(list(Seq(
                          Html(messagesForLanguage.cyaValueVatNumber)
                        )))
                      ),
                      actions = Seq(
                        ActionItemViewModel(
                          content = messagesForLanguage.change,
                          href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url,
                          id = "changeConsigneeExportInformation"
                        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                      )
                    )
                  )
              }

              "when only EORI has been entered" in {

                implicit lazy val request = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers.set(ChangeTypePage, ChangeType.Destination),
                  movementDetails = maxGetMovementResponse.copy(destinationType = Export, consigneeTrader = maxGetMovementResponse.consigneeTrader.map(_.copy(traderExciseNumber = None)))
                )

                summary.row mustBe
                  Some(
                    SummaryListRowViewModel(
                      key = messagesForLanguage.cyaLabel,
                      value = ValueViewModel(
                        HtmlContent(list(Seq(
                          Html(messagesForLanguage.cyaValueEoriNumber)
                        )))
                      ),
                      actions = Seq(
                        ActionItemViewModel(
                          content = messagesForLanguage.change,
                          href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url,
                          id = "changeConsigneeExportInformation"
                        ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                      )
                    )
                  )
              }

              "when nothing has been entered" in {

                implicit lazy val request = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers.set(ChangeTypePage, ChangeType.Destination),
                  movementDetails = maxGetMovementResponse.copy(destinationType = Export, consigneeTrader = maxGetMovementResponse.consigneeTrader.map(_.copy(traderExciseNumber = None, eoriNumber = None)))
                )

                summary.row mustBe
                  Some(
                    SummaryListRowViewModel(
                      key = messagesForLanguage.cyaLabel,
                      value = ValueViewModel(
                        HtmlContent(list(Seq(
                          Html(messagesForLanguage.cyaValueNoInformation)
                        )))
                      ),
                      actions = Seq(
                        ActionItemViewModel(
                          content = messagesForLanguage.change,
                          href = controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url,
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
}
