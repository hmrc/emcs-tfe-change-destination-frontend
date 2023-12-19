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
import fixtures.DocumentTypeFixtures
import fixtures.messages.sections.documents.DocumentsAddToListMessages.English
import forms.sections.documents.DocumentsAddToListFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.documents.{DocumentDescriptionPage, DocumentReferencePage, DocumentTypePage, ReferenceAvailablePage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.DocumentsAddToListHelper
import views.html.sections.documents.DocumentsAddToListView
import views.{BaseSelectors, ViewBehaviours}

class DocumentsAddToListViewSpec extends SpecBase with ViewBehaviours with DocumentTypeFixtures {

  object Selectors extends BaseSelectors {
    val returnToDraftLink: String = "#save-and-exit"
    val cardTitle: String = ".govuk-summary-card__title-wrapper > .govuk-summary-card__title"
    val legendQuestion = ".govuk-fieldset__legend.govuk-fieldset__legend--m"
    val errorSummary: Int => String = index => s".govuk-error-summary__list > li:nth-child($index)"
    val errorField: String = "p.govuk-error-message"
    val removeItemLink: Int => String = index => s"#removeDocuments-$index"
    val editItemLink: Int => String = index => s"#editDocuments-$index"
  }

  "DocumentsAddToListView" - {

    s"when being rendered for singular item" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      val userAnswers = emptyUserAnswers
        .set(DocumentTypePage(0), documentTypeOtherModel)
        .set(ReferenceAvailablePage(0), false)
        .set(DocumentDescriptionPage(0), "description")

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

     lazy val view = app.injector.instanceOf[DocumentsAddToListView]
      val form = app.injector.instanceOf[DocumentsAddToListFormProvider].apply()
      val helper = app.injector.instanceOf[DocumentsAddToListHelper].allDocumentsSummary()

      implicit val doc: Document = Jsoup.parse(view(
        formOpt = Some(form),
        onSubmitCall = testOnwardRoute,
        documents = helper,
        showNoOption = true
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.h2(1) -> English.documentsSection,
        Selectors.hiddenText -> English.hiddenSectionContent,
        Selectors.title -> English.title(1),
        Selectors.h1 -> English.heading(1),
        Selectors.cardTitle -> English.documentCardTitle(0),
        Selectors.removeItemLink(1) -> English.removeDocument(0),
        Selectors.legendQuestion -> English.h2,
        Selectors.radioButton(1) -> English.yes,
        Selectors.radioButton(2) -> English.no1,
        Selectors.radioButton(4) -> English.moreLater,
        Selectors.button -> English.saveAndContinue,
        Selectors.returnToDraftLink -> English.returnToDraft
      ))
    }

    s"when being rendered for multiple items" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      val userAnswers = emptyUserAnswers
        .set(DocumentTypePage(0), documentTypeModel)
        .set(DocumentReferencePage(0), "reference1")
        .set(DocumentTypePage(1), documentTypeModel)
        .set(ReferenceAvailablePage(1), true)
        .set(DocumentReferencePage(1), "reference1")

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

     lazy val view = app.injector.instanceOf[DocumentsAddToListView]
      val form = app.injector.instanceOf[DocumentsAddToListFormProvider].apply()
      val helper = app.injector.instanceOf[DocumentsAddToListHelper].allDocumentsSummary()

      implicit val doc: Document = Jsoup.parse(view(
        formOpt = Some(form),
        onSubmitCall = testOnwardRoute,
        documents = helper,
        showNoOption = true
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title(2),
        Selectors.h1 -> English.heading(2),
//        Selectors.cardTitle -> s"${English.documentCardTitle(0)} ${English.incomplete}",
        Selectors.cardTitle -> English.documentCardTitle(0),
        Selectors.removeItemLink(1) -> English.removeDocument(0),
        Selectors.legendQuestion -> English.h2,
        Selectors.radioButton(1) -> English.yes,
        Selectors.radioButton(2) -> English.no2,
        Selectors.radioButton(4) -> English.moreLater,
        Selectors.button -> English.saveAndContinue,
        Selectors.returnToDraftLink -> English.returnToDraft
      ))
    }

    s"when being rendered with no form" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      val userAnswers = emptyUserAnswers
        .set(DocumentTypePage(0), documentTypeModel)
        .set(DocumentReferencePage(0), "reference1")
        .set(DocumentTypePage(1), documentTypeOtherModel)
        .set(ReferenceAvailablePage(1), false)
        .set(DocumentDescriptionPage(1), "description2")

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

     lazy val view = app.injector.instanceOf[DocumentsAddToListView]
      val helper = app.injector.instanceOf[DocumentsAddToListHelper].allDocumentsSummary()

      implicit val doc: Document = Jsoup.parse(view(
        formOpt = None,
        onSubmitCall = testOnwardRoute,
        documents = helper,
        showNoOption = true
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title(2),
        Selectors.h1 -> English.heading(2),
        Selectors.cardTitle -> English.documentCardTitle(0),
        Selectors.removeItemLink(1) -> English.removeDocument(0),
        Selectors.button -> English.saveAndContinue,
        Selectors.returnToDraftLink -> English.returnToDraft
      ))

      behave like pageWithElementsNotPresent(Seq(
        Selectors.legendQuestion,
        Selectors.radioButton(1),
        Selectors.radioButton(2),
        Selectors.radioButton(4)
      ))
    }

    s"when being rendered for an InProgress item" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      val userAnswers = emptyUserAnswers
        .set(DocumentDescriptionPage(0), "description")

      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

     lazy val view = app.injector.instanceOf[DocumentsAddToListView]
      val form = app.injector.instanceOf[DocumentsAddToListFormProvider].apply()
      val helper = app.injector.instanceOf[DocumentsAddToListHelper].allDocumentsSummary()

      implicit val doc: Document = Jsoup.parse(view(
        formOpt = Some(form),
        onSubmitCall = testOnwardRoute,
        documents = helper,
        showNoOption = false
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.h2(1) -> English.documentsSection,
        Selectors.hiddenText -> English.hiddenSectionContent,
        Selectors.title -> English.title(1),
        Selectors.h1 -> English.heading(1),
        Selectors.cardTitle -> s"${English.documentCardTitle(0)} ${English.incomplete}",
        Selectors.removeItemLink(1) -> English.removeDocument(0),
        Selectors.editItemLink(1) -> English.editDocument(0),
        Selectors.legendQuestion -> English.h2,
        Selectors.radioButton(1) -> English.yes,
        Selectors.radioButton(2) -> English.moreLater,
        Selectors.button -> English.saveAndContinue,
        Selectors.returnToDraftLink -> English.returnToDraft
      ))
    }
  }
}

