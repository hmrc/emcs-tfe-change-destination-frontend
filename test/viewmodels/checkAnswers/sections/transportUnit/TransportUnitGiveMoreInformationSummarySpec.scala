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
import fixtures.messages.sections.transportUnit.TransportUnitGiveMoreInformationMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.transportUnit.TransportUnitGiveMoreInformationPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Text, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportUnitGiveMoreInformationSummarySpec extends SpecBase with Matchers {
  "TransportUnitGiveMoreInformationSummary" - {

    implicit val link = app.injector.instanceOf[views.html.components.link]

    Seq(TransportUnitGiveMoreInformationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected row - when not on review page" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(transportDetails = Seq.empty))

            TransportUnitGiveMoreInformationSummary.row(testIndex1, hideChangeLinks = false) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(
                  HtmlContent(link(
                    controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(testErn, testArc, testIndex1, CheckMode).url,
                    messagesForLanguage.valueWhenAnswerNotPresent))
                ),
                actions = Seq()
              )
          }

          "must output the expected row - when on review page" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(transportDetails = Seq.empty))

            TransportUnitGiveMoreInformationSummary.row(testIndex1, hideChangeLinks = true) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text(messagesForLanguage.notProvided)),
                actions = Seq()
              )
          }
        }

        "when there's an answer" - {

          "must output the expected row - with a change link when not on review page" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitGiveMoreInformationPage(testIndex1), Some("value")))

            TransportUnitGiveMoreInformationSummary.row(testIndex1, hideChangeLinks = false) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text("value")),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.transportUnit.routes.TransportUnitGiveMoreInformationController.onPageLoad(testErn, testArc, testIndex1, CheckMode).url,
                    id = "changeTransportUnitMoreInformation1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden(testIndex1))
                )
              )
          }

          "must output the expected row - with no change link when on review page" in {
            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitGiveMoreInformationPage(testIndex1), Some("value")))

            TransportUnitGiveMoreInformationSummary.row(testIndex1, hideChangeLinks = true) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(Text("value")),
                actions = Seq()
              )
          }
        }
      }
    }
  }
}
