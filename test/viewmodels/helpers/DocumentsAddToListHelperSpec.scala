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

package viewmodels.helpers

import base.SpecBase
import controllers.sections.documents.routes
import fixtures.DocumentTypeFixtures
import fixtures.messages.sections.documents.DocumentsAddToListMessages.English
import models.{NormalMode, UserAnswers}
import pages.sections.documents.{DocumentDescriptionPage, DocumentReferencePage, DocumentTypePage, ReferenceAvailablePage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.documents.{DocumentDescriptionSummary, DocumentReferenceSummary, DocumentTypeSummary, ReferenceAvailableSummary}
import views.html.components.{link, span, tag}

class DocumentsAddToListHelperSpec extends SpecBase with DocumentTypeFixtures {

  class Setup(userAnswers: UserAnswers = emptyUserAnswers) {
    implicit lazy val link = app.injector.instanceOf[link]
    implicit lazy val request = dataRequest(FakeRequest(), userAnswers)
    implicit lazy val span = app.injector.instanceOf[span]
    implicit lazy val tag = app.injector.instanceOf[tag]

    lazy val helper: DocumentsAddToListHelper = app.injector.instanceOf[DocumentsAddToListHelper]
  }

  "DocumentsAddToListHelper" - {

    "return nothing" - {

      s"when no answers specified for '${English.lang.code}'" in new Setup() {

        implicit lazy val msgs: Messages = messages(Seq(English.lang))

        helper.allDocumentsSummary() mustBe Nil
      }
    }

    "return required rows when all answers filled out" - {

      s"when the row is Complete and the DocumentType is NOT Other" in new Setup(emptyUserAnswers
        .set(DocumentTypePage(0), documentTypeModel)
        .set(DocumentReferencePage(0), "reference")
      ) {

        implicit lazy val msgs: Messages = messages(Seq(English.lang))

        helper.allDocumentsSummary() mustBe Seq(
          SummaryList(
            card = Some(Card(
              title = Some(CardTitle(HtmlContent(span(English.documentCardTitle(0))))),
              actions = Some(Actions(items = Seq(
                ActionItem(
                  href = routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, 0).url,
                  content = Text(English.remove),
                  visuallyHiddenText = Some(English.documentCardTitle(0)),
                  attributes = Map("id" -> "removeDocuments-1")
                )
              )))
            )),
            rows = Seq(
              DocumentTypeSummary.row(0).get,
              DocumentReferenceSummary.row(0).get
            )
          )
        )
      }

      s"when the row is Complete with DocumentType is Other and ReferenceAvailable is true" in new Setup(emptyUserAnswers
        .set(DocumentTypePage(0), documentTypeOtherModel)
        .set(ReferenceAvailablePage(0), true)
        .set(DocumentReferencePage(0), "reference")
      ) {

        implicit lazy val msgs: Messages = messages(Seq(English.lang))

        helper.allDocumentsSummary() mustBe Seq(
          SummaryList(
            card = Some(Card(
              title = Some(CardTitle(HtmlContent(span(English.documentCardTitle(0))))),
              actions = Some(Actions(items = Seq(
                ActionItem(
                  href = routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, 0).url,
                  content = Text(English.remove),
                  visuallyHiddenText = Some(English.documentCardTitle(0)),
                  attributes = Map("id" -> "removeDocuments-1")
                )
              )))
            )),
            rows = Seq(
              DocumentTypeSummary.row(0).get,
              ReferenceAvailableSummary.row(0).get,
              DocumentReferenceSummary.row(0).get
            )
          )
        )
      }

      s"when the row is Complete with DocumentType is Other and ReferenceAvailable is false" in new Setup(emptyUserAnswers
        .set(DocumentTypePage(0), documentTypeOtherModel)
        .set(ReferenceAvailablePage(0), false)
        .set(DocumentDescriptionPage(0), "description")
      ) {

        implicit lazy val msgs: Messages = messages(Seq(English.lang))

        helper.allDocumentsSummary() mustBe Seq(
          SummaryList(
            card = Some(Card(
              title = Some(CardTitle(HtmlContent(span(English.documentCardTitle(0))))),
              actions = Some(Actions(items = Seq(
                ActionItem(
                  href = routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, 0).url,
                  content = Text(English.remove),
                  visuallyHiddenText = Some(English.documentCardTitle(0)),
                  attributes = Map("id" -> "removeDocuments-1")
                )
              )))
            )),
            rows = Seq(
              DocumentTypeSummary.row(0).get,
              ReferenceAvailableSummary.row(0).get,
              DocumentDescriptionSummary.row(0).get
            )
          )
        )
      }

      s"when all answers entered and there is both a Completed and an InProgress row" in new Setup(emptyUserAnswers
        .set(DocumentTypePage(0), documentTypeOtherModel)
        .set(ReferenceAvailablePage(0), false)
        .set(DocumentDescriptionPage(0), "description")
        .set(ReferenceAvailablePage(1), true)
      ) {

        implicit lazy val msgs: Messages = messages(Seq(English.lang))

        helper.allDocumentsSummary() mustBe Seq(
          SummaryList(
            card = Some(Card(
              title = Some(CardTitle(HtmlContent(span(English.documentCardTitle(0))))),
              actions = Some(Actions(items = Seq(
                ActionItem(
                  href = routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, 0).url,
                  content = Text(English.remove),
                  visuallyHiddenText = Some(English.documentCardTitle(0)),
                  attributes = Map("id" -> "removeDocuments-1")
                )
              )))
            )),
            rows = Seq(
              DocumentTypeSummary.row(0).get,
              ReferenceAvailableSummary.row(0).get,
              DocumentDescriptionSummary.row(0).get
            )
          ),
          SummaryList(
            card = Some(Card(
              title = Some(CardTitle(HtmlContent(HtmlFormat.fill(Seq(
                span(English.documentCardTitle(1), Some("govuk-!-margin-right-2")),
                tag(English.incomplete, "red")
              ))))),
              actions = Some(Actions(items = Seq(
                ActionItem(
                  href = routes.DocumentTypeController.onPageLoad(testErn, testDraftId, 1, NormalMode).url,
                  content = Text(English.continueEditing),
                  visuallyHiddenText = Some(English.documentCardTitle(1)),
                  attributes = Map("id" -> "editDocuments-2")
                ),
                ActionItem(
                  href = routes.DocumentsRemoveFromListController.onPageLoad(testErn, testDraftId, 1).url,
                  content = Text(English.remove),
                  visuallyHiddenText = Some(English.documentCardTitle(1)),
                  attributes = Map("id" -> "removeDocuments-2")
                )
              )))
            )),
            rows = Seq(
              ReferenceAvailableSummary.row(1).get
            )
          )
        )
      }
    }
  }
}
