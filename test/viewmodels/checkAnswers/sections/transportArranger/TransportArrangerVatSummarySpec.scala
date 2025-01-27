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
import fixtures.messages.sections.transportArranger.TransportArrangerVatMessages
import models.{CheckMode, VatNumberModel}
import models.sections.transportArranger.TransportArranger.{Consignor, GoodsOwner, Other}
import org.scalatest.matchers.must.Matchers
import pages.sections.transportArranger.{TransportArrangerPage, TransportArrangerVatPage}
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportArrangerVatSummarySpec extends SpecBase with Matchers {
  "TransportArrangerVatSummary" - {

    Seq(TransportArrangerVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        "when TransportArranger is NOT GoodsOwner or Other" - {

          "must output None" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, Consignor))

            TransportArrangerVatSummary.row(onReviewPage = false) mustBe None
          }
        }

        "when TransportArranger is GoodsOwner or Other" - {

          "when there's no answer" - {

            "must output the expected data (from IE801)" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, GoodsOwner))

              TransportArrangerVatSummary.row(onReviewPage = false) mustBe
                Some(
                  SummaryListRowViewModel(
                    key = messagesForLanguage.cyaInputLabel,
                    value = Value(Text("TransportArrangerTraderVatNumber")),
                    actions = Seq(
                      ActionItemViewModel(
                        content = messagesForLanguage.change,
                        href = controllers.sections.transportArranger.routes.TransportArrangerVatController.onPageLoad(testErn, testArc, CheckMode).url,
                        id = "changeTransportArrangerVat"
                      ).withVisuallyHiddenText(messagesForLanguage.cyaInputChangeHidden)
                    )
                  )
                )
            }
          }

          //NONGBVAT is added when the movement is submitted
          "when the user selected 'No' to VAT number" - {

            "must output the expected data" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(TransportArrangerPage, GoodsOwner)
                .set(TransportArrangerVatPage, VatNumberModel(hasVatNumber = false, None))
              )

              TransportArrangerVatSummary.row(onReviewPage = false) mustBe None
            }
          }

          "when there's an answer" - {

            "when NOT on review page" - {

              "must output the expected row (when the user selected 'Yes' to VAT number)" in {

                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                  .set(TransportArrangerPage, Other)
                  .set(TransportArrangerVatPage, VatNumberModel(hasVatNumber = true, Some(testVatNumber)))
                )

                TransportArrangerVatSummary.row(onReviewPage = false) mustBe
                  Some(
                    SummaryListRowViewModel(
                      key = messagesForLanguage.cyaInputLabel,
                      value = Value(Text(testVatNumber)),
                      actions = Seq(
                        ActionItemViewModel(
                          content = messagesForLanguage.change,
                          href = controllers.sections.transportArranger.routes.TransportArrangerVatController.onPageLoad(testErn, testArc, CheckMode).url,
                          id = "changeTransportArrangerVat"
                        ).withVisuallyHiddenText(messagesForLanguage.cyaInputChangeHidden)
                      )
                    )
                  )
              }
            }

            "when on review page" - {

              "must output the expected row (when the user selected 'Yes' to VAT number)" in {

                implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                  .set(TransportArrangerPage, Other)
                  .set(TransportArrangerVatPage, VatNumberModel(hasVatNumber = true, Some(testVatNumber)))
                )

                TransportArrangerVatSummary.row(onReviewPage = true) mustBe
                  Some(
                    SummaryListRowViewModel(
                      key = messagesForLanguage.cyaInputLabel,
                      value = Value(Text(testVatNumber)),
                      actions = Seq()
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
