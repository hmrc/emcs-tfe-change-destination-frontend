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

package viewmodels.checkAnswers.sections.transportArranger

import base.SpecBase
import fixtures.messages.sections.transportArranger.TransportArrangerAddressMessages
import models.CheckMode
import models.sections.transportArranger.TransportArranger.{Consignee, Consignor, GoodsOwner, Other}
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.transportArranger.{TransportArrangerAddressPage, TransportArrangerPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportArrangerAddressSummarySpec extends SpecBase {

  "TransportArrangerAddressSummary" - {

    Seq(TransportArrangerAddressMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when the TransportArranger is GoodsOwner or Other" - {

          "when there's no answer" - {

            "must output the expected data" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, GoodsOwner))

              TransportArrangerAddressSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.notProvided)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportArranger.routes.TransportArrangerAddressController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeTransportArrangerAddress"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
            }
          }

          "when there's an answer" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(TransportArrangerPage, Other)
                .set(TransportArrangerAddressPage, testUserAddress)
              )

              TransportArrangerAddressSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(
                    HtmlFormat.fill(Seq(
                      Html(testUserAddress.property.fold("")(_ + " ") + testUserAddress.street + "<br>"),
                      Html(testUserAddress.town + "<br>"),
                      Html(testUserAddress.postcode),
                    ))
                  )),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportArranger.routes.TransportArrangerAddressController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeTransportArrangerAddress"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
            }
          }
        }

        "when the TransportArranger is Consignor" - {

          "when there's no answer for the ConsignorAddressPage" - {

            "must output the expected data" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, Consignor))

              TransportArrangerAddressSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.sectionNotComplete("Consignor"))),
                  actions = Seq()
                )
            }
          }

          "when there's an answer for the ConsignorAddressPage" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(TransportArrangerPage, Consignor)
                .set(ConsignorAddressPage, testUserAddress)
              )

              TransportArrangerAddressSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(
                    HtmlFormat.fill(Seq(
                      Html(testUserAddress.property.fold("")(_ + " ") + testUserAddress.street + "<br>"),
                      Html(testUserAddress.town + "<br>"),
                      Html(testUserAddress.postcode),
                    ))
                  )),
                  actions = Seq()
                )
            }
          }
        }

        "when the TransportArranger is Consignee" - {

          "when there's no answer for the ConsigneeAddressPage" - {

            "must output the expected data" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, Consignee))

              TransportArrangerAddressSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.sectionNotComplete("Consignee"))),
                  actions = Seq()
                )
            }
          }

          "when there's an answer for the ConsigneeAddressPage" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(TransportArrangerPage, Consignee)
                .set(ConsigneeAddressPage, testUserAddress)
              )

              TransportArrangerAddressSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(
                    HtmlFormat.fill(Seq(
                      Html(testUserAddress.property.fold("")(_ + " ") + testUserAddress.street + "<br>"),
                      Html(testUserAddress.town + "<br>"),
                      Html(testUserAddress.postcode),
                    ))
                  )),
                  actions = Seq()
                )
            }
          }
        }
      }
    }
  }
}
