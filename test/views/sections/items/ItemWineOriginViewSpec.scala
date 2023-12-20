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
import fixtures.messages.sections.items.ItemWineOriginMessages
import forms.sections.items.ItemWineOriginFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemWineOriginView
import views.{BaseSelectors, ViewBehaviours}

class ItemWineOriginViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    def selectOption(nthChild: Int) = s"#country > option:nth-child($nthChild)"
  }

  private val countries = Seq(countryModelAU, countryModelBR)

  "ItemWineOrigin view" - {

    Seq(ItemWineOriginMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())
        val selectItems = SelectItemHelper.constructSelectItems(countries, "itemWineOrigin.select.defaultValue")

        lazy val view = app.injector.instanceOf[ItemWineOriginView]
        val form = app.injector.instanceOf[ItemWineOriginFormProvider].apply(countries)

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, selectItems).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.selectOption(1) -> messagesForLanguage.defaultSelectOption,
          Selectors.selectOption(2) -> messagesForLanguage.auSelectOption,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
