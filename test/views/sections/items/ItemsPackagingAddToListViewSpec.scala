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

package views.sections.items

import base.SpecBase
import fixtures.DocumentTypeFixtures
import fixtures.messages.sections.items.ItemsPackagingAddToListMessages
import forms.sections.items.ItemsPackagingAddToListFormProvider
import models.requests.DataRequest
import models.sections.items.ItemPackagingSealTypeModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.items.{ItemPackagingProductTypePage, ItemPackagingQuantityPage, ItemPackagingSealChoicePage, ItemPackagingSealTypePage, ItemPackagingShippingMarksPage, ItemSelectPackagingPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.ItemsPackagingAddToListHelper
import views.html.sections.items.ItemsPackagingAddToListView
import views.{BaseSelectors, ViewBehaviours}

class ItemsPackagingAddToListViewSpec extends SpecBase with ViewBehaviours with DocumentTypeFixtures {

  lazy val view = app.injector.instanceOf[ItemsPackagingAddToListView]
  lazy val form = app.injector.instanceOf[ItemsPackagingAddToListFormProvider].apply()
  lazy val helper = app.injector.instanceOf[ItemsPackagingAddToListHelper]

  object Selectors extends BaseSelectors {
    val returnToDraftLink: String = "#save-and-exit"
    val cardTitle: Int => String = index => s"div.govuk-summary-card:nth-of-type($index) .govuk-summary-card__title"
    val legendQuestion = ".govuk-fieldset__legend.govuk-fieldset__legend--m"
    val errorSummary: Int => String = index => s".govuk-error-summary__list > li:nth-child($index)"
    val errorField: String = "p.govuk-error-message"
    val removeItemLink: Int => String = index => s"#removePackage-$index"
    val editItemLink: Int => String = index => s"#editPackage-$index"
  }

  "ItemsPackagingAddToListView" - {

    Seq(ItemsPackagingAddToListMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in language code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        s"when being rendered for singular package" - {

          val userAnswers = emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "SHIP")
            .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("SEAL", Some("INFO")))

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = Some(form),
            onSubmitCall = testOnwardRoute,
            packages = helper.allPackagesSummary(testIndex1),
            showNoOption = true,
            itemIdx = testIndex1
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 1, itemIdx = testIndex1),
            Selectors.h2(1) -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading(count = 1, itemIdx = testIndex1),
            Selectors.cardTitle(1) -> messagesForLanguage.packageCardTitle(testPackagingIndex1),
            Selectors.removeItemLink(1) -> messagesForLanguage.removePackage(testPackagingIndex1),
            Selectors.legendQuestion -> messagesForLanguage.h2(testIndex1),
            Selectors.hint -> messagesForLanguage.hint,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no1,
            Selectors.radioButton(4) -> messagesForLanguage.moreLater,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
          ))
        }

        s"when being rendered for multiple packages (one incomplete and in progress)" - {

          val userAnswers = emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "SHIP")
            .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("SEAL", Some("INFO")))
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), testPackageBag)

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = Some(form),
            onSubmitCall = testOnwardRoute,
            packages = helper.allPackagesSummary(testIndex1),
            showNoOption = true,
            itemIdx = testIndex1
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 2, itemIdx = testIndex1),
            Selectors.h2(1) -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading(count = 2, itemIdx = testIndex1),
            Selectors.cardTitle(1) -> messagesForLanguage.packageCardTitle(testPackagingIndex1),
            Selectors.removeItemLink(1) -> messagesForLanguage.removePackage(testPackagingIndex1),
            Selectors.cardTitle(2) -> s"${messagesForLanguage.packageCardTitle(testPackagingIndex2)} ${messagesForLanguage.incomplete}",
            Selectors.removeItemLink(2) -> messagesForLanguage.removePackage(testPackagingIndex2),
            Selectors.legendQuestion -> messagesForLanguage.h2(testIndex1),
            Selectors.hint -> messagesForLanguage.hint,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no2,
            Selectors.radioButton(4) -> messagesForLanguage.moreLater,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
          ))
        }

        s"when being rendered with no form" - {

          val userAnswers = emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "SHIP")
            .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("SEAL", Some("INFO")))

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = None,
            onSubmitCall = testOnwardRoute,
            packages = helper.allPackagesSummary(testIndex1),
            showNoOption = true,
            itemIdx = testIndex1
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 1, itemIdx = testIndex1),
            Selectors.h2(1) -> messagesForLanguage.itemSection,
            Selectors.h1 -> messagesForLanguage.heading(count = 1, itemIdx = testIndex1),
            Selectors.cardTitle(1) -> messagesForLanguage.packageCardTitle(testPackagingIndex1),
            Selectors.removeItemLink(1) -> messagesForLanguage.removePackage(testPackagingIndex1),
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
          ))

          behave like pageWithElementsNotPresent(Seq(
            Selectors.legendQuestion,
            Selectors.radioButton(1),
            Selectors.radioButton(2),
            Selectors.radioButton(4)
          ))
        }
      }
    }
  }
}

