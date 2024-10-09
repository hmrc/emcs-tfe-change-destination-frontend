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

package viewmodels.checkAnswers.sections.firstTransporter

import base.SpecBase
import fixtures.messages.sections.firstTransporter.FirstTransporterVatMessages
import models.{CheckMode, VatNumberModel}
import org.scalatest.matchers.must.Matchers
import pages.sections.firstTransporter.FirstTransporterVatPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._


class FirstTransporterVatChoiceSummarySpec extends SpecBase with Matchers {
  "FirstTransporterVatChoiceSummary" - {
    Seq(FirstTransporterVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

        "when the answer is 'No' (not on review page)" in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterVatPage, VatNumberModel(false, None)))

          FirstTransporterVatChoiceSummary.row(onReviewPage = false) mustBe Some(SummaryListRowViewModel(
            key = messagesForLanguage.vatChoiceCyaLabel,
            value = Value(Text(messagesForLanguage.no)),
            actions = Seq(
              ActionItemViewModel(
                content = messagesForLanguage.change,
                href = controllers.sections.firstTransporter.routes.FirstTransporterVatController.onPageLoad(testErn, testArc, CheckMode).url,
                id = "changeFirstTransporterVatChoice"
              ).withVisuallyHiddenText(messagesForLanguage.cyaChangeChoiceHidden)
            )
          ))
        }

        "when the answer is 'Yes' (on review page)" in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(FirstTransporterVatPage, VatNumberModel(true, Some(testVatNumber))))

          FirstTransporterVatChoiceSummary.row(onReviewPage = true) mustBe Some(SummaryListRowViewModel(
            key = messagesForLanguage.vatChoiceCyaLabel,
            value = Value(Text(messagesForLanguage.yes)),
            actions = Seq()
          ))
        }

        "when there is no answer (shouldn't happen, as should always come back on IE801)" in {
          implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers, movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None))

          FirstTransporterVatChoiceSummary.row(onReviewPage = false) mustBe None
        }
      }
    }
  }
}
