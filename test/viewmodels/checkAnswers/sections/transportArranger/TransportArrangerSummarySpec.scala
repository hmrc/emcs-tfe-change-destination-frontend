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
import fixtures.messages.sections.transportArranger.TransportArrangerMessages
import models.CheckMode
import models.sections.transportArranger.TransportArranger.GoodsOwner
import org.scalatest.matchers.must.Matchers
import pages.sections.transportArranger.TransportArrangerPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportArrangerSummarySpec extends SpecBase with Matchers {

  "TransportArrangerSummary" - {

    Seq(TransportArrangerMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer in the user answers (defaulting to 801)" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            TransportArrangerSummary.row(onReviewPage = false) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.consigneeRadioOption)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportArranger.routes.TransportArrangerController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeTransportArranger"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
          }
        }

        "when there's an answer" - {

          "and the user is on the review page" - {

            "must output a row with no change link" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, GoodsOwner))

              TransportArrangerSummary.row(onReviewPage = true) mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.goodsOwnerRadioOption)),
                  actions = Seq()
                )
              )
            }
          }

          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportArrangerPage, GoodsOwner))

            TransportArrangerSummary.row(onReviewPage = false) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.goodsOwnerRadioOption)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportArranger.routes.TransportArrangerController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeTransportArranger"
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
