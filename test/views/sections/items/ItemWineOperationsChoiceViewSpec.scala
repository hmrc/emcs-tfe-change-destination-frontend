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
import fixtures.messages.sections.items.ItemWineOperationsChoiceMessages
import forms.sections.items.ItemWineOperationsChoiceFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemWineOperationsChoiceView
import views.{BaseSelectors, ViewBehaviours}

class ItemWineOperationsChoiceViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {
  object Selectors extends BaseSelectors

  //scalastyle:off
  "ItemWineOperationsView" - {
    Seq(ItemWineOperationsChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val view = app.injector.instanceOf[ItemWineOperationsChoiceView]
        val form = app.injector.instanceOf[ItemWineOperationsChoiceFormProvider].apply(testWineOperations)

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, testWineOperations).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.checkboxItem(1) -> messagesForLanguage.checkBoxItem0,
          Selectors.checkboxItem(2) -> messagesForLanguage.checkBoxItem1,
          Selectors.checkboxItem(3) -> messagesForLanguage.checkBoxItem2,
          Selectors.checkboxItem(4) -> messagesForLanguage.checkBoxItem3,
          Selectors.checkboxItem(5) -> messagesForLanguage.checkBoxItem4,
          Selectors.checkboxItem(6) -> messagesForLanguage.checkBoxItem5,
          Selectors.checkboxItem(7) -> messagesForLanguage.checkBoxItem6,
          Selectors.checkboxItem(8) -> messagesForLanguage.checkBoxItem7,
          Selectors.checkboxItem(9) -> messagesForLanguage.checkBoxItem8,
          Selectors.checkboxItem(10) -> messagesForLanguage.checkBoxItem9,
          Selectors.checkboxItem(11) -> messagesForLanguage.checkBoxItem10,
          Selectors.checkboxItem(12) -> messagesForLanguage.checkBoxItem11,
          Selectors.checkboxDividerItem(13) -> messagesForLanguage.checkBoxItem12,
          Selectors.checkboxItem(14) -> messagesForLanguage.checkBoxItem13,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
