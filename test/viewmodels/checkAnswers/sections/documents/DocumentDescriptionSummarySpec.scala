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
import fixtures.DocumentTypeFixtures
import fixtures.messages.sections.documents.DocumentDescriptionMessages
import fixtures.messages.sections.documents.DocumentDescriptionMessages.English
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.documents.{DocumentDescriptionPage, DocumentTypePage, ReferenceAvailablePage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DocumentDescriptionSummarySpec extends SpecBase with Matchers with DocumentTypeFixtures {

  "DocumentDescriptionSummary" - {

    implicit val msgs: Messages = messages(Seq(DocumentDescriptionMessages.English.lang))

    "when there's no answer" - {

      "must output no row" in {

        implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

        DocumentDescriptionSummary.row(0) mustBe None
      }
    }

    "when there's an answer" - {

      val answer = "description"

      "must output the expected row WITH a change link when the document IS Completed" in {

        implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DocumentTypePage(0), documentTypeOtherModel)
          .set(ReferenceAvailablePage(0), false)
          .set(DocumentDescriptionPage(0), answer)
        )

        DocumentDescriptionSummary.row(0) mustBe
          Some(
            SummaryListRowViewModel(
              key = English.cyaLabel,
              value = Value(Text(answer)),
              actions = Seq(
                ActionItemViewModel(
                  content = English.change,
                  href = controllers.sections.documents.routes.DocumentDescriptionController.onPageLoad(testErn, testDraftId, 0, CheckMode).url,
                  id = "changeDocumentDescription-1"
                ).withVisuallyHiddenText(English.cyaChangeHidden)
              )
            )
          )
      }

      "must output the expected row WITHOUT a change link when the document is NOT Completed" in {

        implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DocumentDescriptionPage(0), answer)
        )

        DocumentDescriptionSummary.row(0) mustBe
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
