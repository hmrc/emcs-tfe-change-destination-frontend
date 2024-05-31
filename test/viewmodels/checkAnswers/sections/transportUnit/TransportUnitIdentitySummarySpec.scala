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

package viewmodels.checkAnswers.sections.transportUnit

import base.SpecBase
import controllers.sections.transportUnit.{routes => transportUnitRoutes}
import fixtures.messages.sections.transportUnit.TransportUnitIdentityMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.transportUnit.TransportUnitIdentityPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportUnitIdentitySummarySpec extends SpecBase with Matchers {

  "TransportUnitIdentitySummary" - {

    Seq(TransportUnitIdentityMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output row with answer not provided" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(transportDetails = Seq.empty))

            TransportUnitIdentitySummary.row(testIndex1, onReviewPage = false) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = transportUnitRoutes.TransportUnitIdentityController.onPageLoad(testErn, testArc, testIndex1, CheckMode).url,
                    id = "changeTransportUnitIdentity1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
          }

          "must output row with answer not provided with no change link - when on review page" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(transportDetails = Seq.empty))

            TransportUnitIdentitySummary.row(testIndex1, onReviewPage = true) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq()
              )
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "testName"))

            TransportUnitIdentitySummary.row(testIndex1, onReviewPage = false) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text("testName")),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = transportUnitRoutes.TransportUnitIdentityController.onPageLoad(testErn, testArc, testIndex1, CheckMode).url,
                    id = "changeTransportUnitIdentity1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
          }

          "must output the expected row with no change link - when on review page" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitIdentityPage(testIndex1), "testName"))

            TransportUnitIdentitySummary.row(testIndex1, onReviewPage = true) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text("testName")),
                actions = Seq()
              )
          }
        }
      }
    }
  }
}
