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

package viewmodels.checkAnswers.sections.documents

import base.SpecBase
import controllers.sections.documents.routes
import fixtures.DocumentTypeFixtures
import fixtures.messages.sections.documents.DocumentReferenceMessages
import fixtures.messages.sections.documents.DocumentReferenceMessages.English
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.documents.{DocumentReferencePage, DocumentTypePage, ReferenceAvailablePage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DocumentReferenceSummarySpec extends SpecBase with Matchers with DocumentTypeFixtures {

  "DocumentReferenceSummary" - {

    implicit val msgs: Messages = messages(Seq(DocumentReferenceMessages.English.lang))

    "when there's no answer" - {

      "must output no row" in {

        implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

        DocumentReferenceSummary.row(0) mustBe None
      }
    }

    "when there's an answer" - {

      val answer = "reference"

      "must output the expected row WITH a change link when the document IS Completed" in {

        implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(ReferenceAvailablePage(0), true)
          .set(DocumentReferencePage(0), answer)
        )

        DocumentReferenceSummary.row(0) mustBe
          Some(
            SummaryListRowViewModel(
              key = English.cyaLabel,
              value = Value(Text(answer)),
              actions = Seq(
                ActionItemViewModel(
                  content = English.change,
                  href = routes.DocumentReferenceController.onPageLoad(testErn, testDraftId, 0, CheckMode).url,
                  id = "changeDocumentReference-1"
                ).withVisuallyHiddenText(English.cyaChangeHidden)
              )
            )
          )
      }

      "must output the expected row WITHOUT a change link when the document is NOT Completed" in {

        implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DocumentReferencePage(0), answer)
        )

        DocumentReferenceSummary.row(0) mustBe
          Some(
            SummaryListRowViewModel(
              key = English.cyaLabel,
              value = Value(Text(answer))
            )
          )
      }
    }
  }
}
