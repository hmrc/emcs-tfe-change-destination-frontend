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
import fixtures.messages.sections.documents.DocumentTypeMessages.English
import forms.sections.documents.DocumentTypeFormProvider
import models.requests.DataRequest
import models.sections.documents.DocumentType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.SelectItemHelper
import views.html.sections.documents.DocumentTypeView
import views.{BaseSelectors, ViewBehaviours}

class DocumentTypeViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors {
    def selectOption(nthChild: Int) = s"#document-type > option:nth-child($nthChild)"
  }

  "Document Type view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

     lazy val view = app.injector.instanceOf[DocumentTypeView]
      val form = app.injector.instanceOf[DocumentTypeFormProvider].apply(Seq.empty)

      val selectOptions = SelectItemHelper.constructSelectItems(
        selectOptions = Seq(DocumentType("code", "First selection")),
        defaultTextMessageKey = "documentType.select.defaultValue"
      )

      implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, selectOptions).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.h2(1) -> English.documentsSection,
        Selectors.hiddenText -> English.hiddenSectionContent,
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.selectOption(1) -> English.defaultSelectOption,
        Selectors.selectOption(2) -> English.firstSelectionOption,
        Selectors.button -> English.saveAndContinue,
        Selectors.link(1) -> English.returnToDraft
      ))
    }
  }
}

