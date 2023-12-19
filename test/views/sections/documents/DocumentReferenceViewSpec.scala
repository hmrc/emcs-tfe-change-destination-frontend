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
import fixtures.messages.sections.documents.DocumentReferenceMessages.English
import forms.sections.documents.DocumentReferenceFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.documents.DocumentReferenceView
import views.{BaseSelectors, ViewBehaviours}

class DocumentReferenceViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "DocumentReferenceView" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

     lazy val view = app.injector.instanceOf[DocumentReferenceView]
      val form = app.injector.instanceOf[DocumentReferenceFormProvider].apply()

      implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.h2(1) -> English.documentsSection,
        Selectors.hiddenText -> English.hiddenSectionContent,
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.button -> English.saveAndContinue,
        Selectors.link(1) -> English.returnToDraft
      ))
    }
  }
}

