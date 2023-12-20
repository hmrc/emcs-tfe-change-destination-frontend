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

package views.sections.sad

import base.SpecBase
import fixtures.messages.sections.sad.SadAddToListMessages
import forms.sections.sad.SadAddToListFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.sad._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.SadAddToListHelper
import views.html.sections.sad.SadAddToListView
import views.{BaseSelectors, ViewBehaviours}

class SadAddToListViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors {
    val returnToDraftLink: String = "#save-and-exit"
    val cardTitle: String = ".govuk-summary-card__title-wrapper > .govuk-summary-card__title"
    val legendQuestion = ".govuk-fieldset__legend.govuk-fieldset__legend--m"
    val errorSummary: Int => String = index => s".govuk-error-summary__list > li:nth-child(${index})"
    val errorField: String = "p.govuk-error-message"
    val removeItemLink: Int => String =  index => s"#removeSad$index"
  }

  "SadAddToListView" - {

    Seq(SadAddToListMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for singular item" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        val userAnswers = emptyUserAnswers
          .set(ImportNumberPage(testIndex1), "wee")


        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

       lazy val view = app.injector.instanceOf[SadAddToListView]
        val form = app.injector.instanceOf[SadAddToListFormProvider].apply()
        val helper = app.injector.instanceOf[SadAddToListHelper].allSadSummary()

        implicit val doc: Document = Jsoup.parse(
          view(
            optionalForm = Some(form),
            sadDocuments = helper,
            mode = NormalMode
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.removeItemLink(1) -> messagesForLanguage.removeLink1WithHiddenText,
          Selectors.cardTitle -> messagesForLanguage.sad1,
          Selectors.legendQuestion -> messagesForLanguage.question,
          Selectors.radioButton(1) -> messagesForLanguage.yesOption,
          Selectors.radioButton(2) -> messagesForLanguage.noOption,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
        ))
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for singular item with error" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        val userAnswers = emptyUserAnswers
          .set(ImportNumberPage(testIndex1), "wee")


        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

       lazy val view = app.injector.instanceOf[SadAddToListView]
        val form = app.injector.instanceOf[SadAddToListFormProvider].apply()
        val helper = app.injector.instanceOf[SadAddToListHelper].allSadSummary()

        implicit val doc: Document = Jsoup.parse(
          view(
            optionalForm = Some(form.bind(Map("value" -> ""))),
            sadDocuments = helper,
            mode = NormalMode
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.errorMessageHelper(messagesForLanguage.title),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.removeItemLink(1) -> messagesForLanguage.removeLink1WithHiddenText,
          Selectors.cardTitle -> messagesForLanguage.sad1,
          Selectors.legendQuestion -> messagesForLanguage.question,
          Selectors.errorSummary(1) -> messagesForLanguage.errorMessage,
          Selectors.errorField -> messagesForLanguage.errorMessageHelper(messagesForLanguage.errorMessage),
          Selectors.radioButton(1) -> messagesForLanguage.yesOption,
          Selectors.radioButton(2) -> messagesForLanguage.noOption,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
        ))
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for multiple items" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        val userAnswers = emptyUserAnswers
          .set(ImportNumberPage(testIndex1), "wee")
          .set(ImportNumberPage(testIndex2), "wee2")


        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

       lazy val view = app.injector.instanceOf[SadAddToListView]
        val form = app.injector.instanceOf[SadAddToListFormProvider].apply()
        val helper = app.injector.instanceOf[SadAddToListHelper].allSadSummary()

        implicit val doc: Document = Jsoup.parse(
          view(
            optionalForm = Some(form),
            sadDocuments = helper,
            mode = NormalMode
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.titleMultiple,
          Selectors.h1 -> messagesForLanguage.headingMultiple,
          Selectors.removeItemLink(1) -> messagesForLanguage.removeLink1WithHiddenText,
          Selectors.removeItemLink(2) -> messagesForLanguage.removeLink2WithHiddenText,
          Selectors.cardTitle -> messagesForLanguage.sad1,
          Selectors.legendQuestion -> messagesForLanguage.question,
          Selectors.radioButton(1) -> messagesForLanguage.yesOption,
          Selectors.radioButton(2) -> messagesForLanguage.noOption,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
        ))
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for multiple items and no form" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        val userAnswers = emptyUserAnswers
          .set(ImportNumberPage(testIndex1), "wee")
          .set(ImportNumberPage(testIndex2), "wee2")

        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

       lazy val view = app.injector.instanceOf[SadAddToListView]
        val helper = app.injector.instanceOf[SadAddToListHelper].allSadSummary()

        implicit val doc: Document = Jsoup.parse(
          view(
            optionalForm = None,
            sadDocuments = helper,
            mode = NormalMode
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.titleMultiple,
          Selectors.h1 -> messagesForLanguage.headingMultiple,
          Selectors.removeItemLink(1) -> messagesForLanguage.removeLink1WithHiddenText,
          Selectors.cardTitle -> messagesForLanguage.sad1,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
        ))

        behave like pageWithElementsNotPresent(
          Seq(
            Selectors.legendQuestion,
            Selectors.radioButton(1),
            Selectors.radioButton(2)
          )
        )
      }
    }
  }
}

