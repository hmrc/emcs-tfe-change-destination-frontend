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
import fixtures.messages.sections.items.ItemDensityMessages
import forms.sections.items.ItemDensityFormProvider
import models.GoodsType.Energy
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemDensityView
import views.{BaseSelectors, ViewBehaviours}

class ItemDensityViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "Item Alcohol Strength view" - {

    Seq(ItemDensityMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

       lazy val view = app.injector.instanceOf[ItemDensityView]
        val form = app.injector.instanceOf[ItemDensityFormProvider].apply(Energy)

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Energy).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(Energy.toSingularOutput()),
          Selectors.h1 -> messagesForLanguage.heading(Energy.toSingularOutput()),
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.inputSuffix -> messagesForLanguage.suffix,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
