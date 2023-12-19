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
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemExciseProductCodeMessages
import forms.sections.items.ItemExciseProductCodeFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemExciseProductCodeView
import views.{BaseSelectors, ViewBehaviours}

class ItemExciseProductCodeViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {

  object Selectors extends BaseSelectors {
    def selectOption(nthChild: Int) = s"#excise-product-code > option:nth-child($nthChild)"
  }

  "Item Excise Product Code view" - {

    Seq(ItemExciseProductCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        lazy val view = app.injector.instanceOf[ItemExciseProductCodeView]
        val selectOptions = SelectItemHelper.constructSelectItems(
          selectOptions = Seq(beerExciseProductCode),
          defaultTextMessageKey = "itemExciseProductCode.select.defaultValue"
        )
        val form = app.injector.instanceOf[ItemExciseProductCodeFormProvider].apply(Seq(beerExciseProductCode))

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, selectOptions).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.h2(1) -> messagesForLanguage.itemInformationSection,
          Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.selectOption(1) -> messagesForLanguage.defaultSelectOption,
          Selectors.selectOption(2) -> messagesForLanguage.beerSelectOption,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
