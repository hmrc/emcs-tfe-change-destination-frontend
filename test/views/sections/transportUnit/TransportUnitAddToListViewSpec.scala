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

package views.sections.transportUnit

import base.SpecBase
import fixtures.messages.sections.transportUnit.TransportUnitAddToListMessages
import forms.sections.transportUnit.TransportUnitsAddToListFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.transportUnit.{TransportSealTypeModel, TransportUnitsAddToListModel}
import models.sections.transportUnit.TransportUnitType.{FixedTransport, Tractor}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.transportUnit._
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.TransportUnitsAddToListHelper
import views.html.sections.transportUnit.TransportUnitsAddToListView
import views.{BaseSelectors, ViewBehaviours}

class TransportUnitAddToListViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors {
    val returnToDraftLink: String = "#save-and-exit"
    val cardTitle: String = ".govuk-summary-card__title-wrapper > .govuk-summary-card__title"
    val legendQuestion = ".govuk-fieldset__legend.govuk-fieldset__legend--m"
    val errorSummary: Int => String = index => s".govuk-error-summary__list > li:nth-child(${index})"
    val errorField: String = "p.govuk-error-message"
    val removeItemLink: Int => String =  index => s"#removeTransportUnit$index"
  }

  lazy val view: TransportUnitsAddToListView = app.injector.instanceOf[TransportUnitsAddToListView]
  val form: Form[TransportUnitsAddToListModel] = app.injector.instanceOf[TransportUnitsAddToListFormProvider].apply()
  val helper: TransportUnitsAddToListHelper = app.injector.instanceOf[TransportUnitsAddToListHelper]

  "TransportUnitAddToListView" - {

    Seq(TransportUnitAddToListMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        s"for singular item" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          val userAnswers = emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitIdentityPage(testIndex1), "wee")
            .set(TransportSealChoicePage(testIndex1), true)
            .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal Type", Some("more seal info")))
            .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more information for transport unit"))

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(
            view(
              optionalForm = Some(form),
              transportUnits = helper.allTransportUnitsSummary(onReviewPage = false),
              mode = NormalMode
            ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.removeItemLink(1) -> messagesForLanguage.removeLink1WithHiddenText,
            Selectors.cardTitle -> messagesForLanguage.transportUnit1,
            Selectors.legendQuestion -> messagesForLanguage.question,
            Selectors.radioButton(1) -> messagesForLanguage.yesOption,
            Selectors.radioButton(2) -> messagesForLanguage.noOptionSingular,
            Selectors.radioButton(4) -> messagesForLanguage.laterOption,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
          ))
        }

        s"for singular item with error" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          val userAnswers = emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitIdentityPage(testIndex1), "wee")
            .set(TransportSealChoicePage(testIndex1), true)
            .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal Type", Some("more seal info")))
            .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more information for transport unit"))

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(
            view(
              optionalForm = Some(form.bind(Map("value" -> ""))),
              transportUnits = helper.allTransportUnitsSummary(onReviewPage = false),
              mode = NormalMode
            ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.errorMessageHelper(messagesForLanguage.title),
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.removeItemLink(1) -> messagesForLanguage.removeLink1WithHiddenText,
            Selectors.cardTitle -> messagesForLanguage.transportUnit1,
            Selectors.legendQuestion -> messagesForLanguage.question,
            Selectors.errorSummary(1) -> messagesForLanguage.errorMessage,
            Selectors.errorField -> messagesForLanguage.errorMessageHelper(messagesForLanguage.errorMessage),
            Selectors.radioButton(1) -> messagesForLanguage.yesOption,
            Selectors.radioButton(2) -> messagesForLanguage.noOptionSingular,
            Selectors.radioButton(4) -> messagesForLanguage.laterOption,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
          ))
        }

        s"for multiple items" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          val userAnswers = emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitIdentityPage(testIndex1), "wee")
            .set(TransportSealChoicePage(testIndex1), true)
            .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal Type", Some("more seal info")))
            .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more information for transport unit"))
            .set(TransportUnitTypePage(testIndex2), FixedTransport)
            .set(TransportUnitIdentityPage(testIndex2), "wee2")
            .set(TransportSealChoicePage(testIndex2), true)
            .set(TransportSealTypePage(testIndex2), TransportSealTypeModel("seal Type", Some("more seal info 2")))
            .set(TransportUnitGiveMoreInformationChoicePage(testIndex2), true)
            .set(TransportUnitGiveMoreInformationPage(testIndex2), Some("more information for transport unit 2"))

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)


          implicit val doc: Document = Jsoup.parse(
            view(
              optionalForm = Some(form),
              transportUnits = helper.allTransportUnitsSummary(onReviewPage = false),
              mode = NormalMode
            ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleMultiple,
            Selectors.h1 -> messagesForLanguage.headingMultiple,
            Selectors.removeItemLink(1) -> messagesForLanguage.removeLink1WithHiddenText,
            Selectors.removeItemLink(2) -> messagesForLanguage.removeLink2WithHiddenText,
            Selectors.cardTitle -> messagesForLanguage.transportUnit1,
            Selectors.legendQuestion -> messagesForLanguage.question,
            Selectors.radioButton(1) -> messagesForLanguage.yesOption,
            Selectors.radioButton(2) -> messagesForLanguage.noOptionPlural,
            Selectors.radioButton(4) -> messagesForLanguage.laterOption,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
          ))
        }

        s"for multiple items and no form" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          val userAnswers = emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), Tractor)
            .set(TransportUnitIdentityPage(testIndex1), "wee")
            .set(TransportSealChoicePage(testIndex1), true)
            .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal Type", Some("more seal info")))
            .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more information for transport unit"))
            .set(TransportUnitTypePage(testIndex2), FixedTransport)
            .set(TransportUnitIdentityPage(testIndex2), "wee2")
            .set(TransportSealChoicePage(testIndex2), true)
            .set(TransportSealTypePage(testIndex2), TransportSealTypeModel("seal Type", Some("more seal info 2")))
            .set(TransportUnitGiveMoreInformationChoicePage(testIndex2), true)
            .set(TransportUnitGiveMoreInformationPage(testIndex2), Some("more information for transport unit 2"))

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(
            view(
              optionalForm = None,
              transportUnits = helper.allTransportUnitsSummary(onReviewPage = false),
              mode = NormalMode
            ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleMultiple,
            Selectors.h1 -> messagesForLanguage.headingMultiple,
            Selectors.removeItemLink(1) -> messagesForLanguage.removeLink1WithHiddenText,
            Selectors.cardTitle -> messagesForLanguage.transportUnit1,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
          ))

          behave like pageWithElementsNotPresent(
            Seq(
              Selectors.legendQuestion,
              Selectors.radioButton(1),
              Selectors.radioButton(2),
              Selectors.radioButton(4)
            )
          )
        }

        s"when an item is incomplete" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          val userAnswers = emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(
            view(
              optionalForm = Some(form),
              transportUnits = helper.allTransportUnitsSummary(onReviewPage = false),
              mode = NormalMode
            ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.removeItemLink(1) -> messagesForLanguage.removeLink1WithHiddenText,
            Selectors.cardTitle -> messagesForLanguage.incompleteMessageHelper(messagesForLanguage.transportUnit1),
            Selectors.legendQuestion -> messagesForLanguage.question,
            Selectors.radioButton(1) -> messagesForLanguage.yesOption,
            Selectors.radioButton(2) -> messagesForLanguage.laterOption,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
          ))
        }
      }
    }
  }
}

