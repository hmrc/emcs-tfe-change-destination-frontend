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
import fixtures.TransportUnitFixtures
import fixtures.messages.sections.transportUnit.TransportSealChoiceMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.transportUnit.TransportSealChoicePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class TransportSealChoiceSummarySpec extends SpecBase with Matchers with TransportUnitFixtures {

  "TransportSealChoiceSummary" - {

    Seq(TransportSealChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output a row with not provided" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            TransportSealChoiceSummary.row(testIndex1) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text(messagesForLanguage.notProvided)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = transportUnitRoutes.TransportSealChoiceController.onPageLoad(testErn, testArc, testIndex1, CheckMode).url,
                      id = "changeTransportSealChoice1"
                    ).withVisuallyHiddenText(messagesForLanguage.moreInfoCyaChangeHidden)
                  )
                )
              )
          }
        }

        "when there's an answer" - {

          s"must output the expected row for TransportSealChoice" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportSealChoicePage(testIndex1), true))

            TransportSealChoiceSummary.row(testIndex1) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(Text("Yes")),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = transportUnitRoutes.TransportSealChoiceController.onPageLoad(testErn, testArc, testIndex1, CheckMode).url,
                      id = "changeTransportSealChoice1"
                    ).withVisuallyHiddenText(messagesForLanguage.moreInfoCyaChangeHidden)
                  )
                )
              )
          }
        }
      }
    }
  }
}
