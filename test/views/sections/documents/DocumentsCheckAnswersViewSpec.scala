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

package views.sections.documents

import base.SpecBase
import fixtures.messages.sections.documents.DocumentsCheckAnswersMessages
import models.CheckMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.documents.DocumentsCertificatesPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.CheckYourAnswersDocumentsHelper
import views.html.sections.documents.DocumentsCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class DocumentsCheckAnswersViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  trait Fixture {

    val messagesForLanguage = DocumentsCheckAnswersMessages.English
    implicit val msgs: Messages = messages(Seq(DocumentsCheckAnswersMessages.English.lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.set(DocumentsCertificatesPage, false))
    val summaryListHelper = app.injector.instanceOf[CheckYourAnswersDocumentsHelper].summaryList()

   lazy val view = app.injector.instanceOf[DocumentsCheckAnswersView]

    implicit val doc: Document = Jsoup.parse(view(summaryListHelper).toString())
  }

  "Documents Check Answers view" - {
    s"when being rendered in lang code of 'en'" - new Fixture {
      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.h2(1) -> messagesForLanguage.documentsSection,
        Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
        Selectors.title -> messagesForLanguage.title,
        Selectors.h1 -> messagesForLanguage.heading,
        Selectors.button -> messagesForLanguage.confirmAnswers
      ))
    }
    "have a link to change customs office code" in new Fixture {
      doc.getElementById("changeDocumentsCertificates").attr("href") mustBe
        controllers.sections.documents.routes.DocumentsCertificatesController.onPageLoad(testErn, testDraftId, CheckMode).url
    }
  }
}

