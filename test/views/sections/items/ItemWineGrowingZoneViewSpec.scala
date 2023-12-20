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
import fixtures.messages.sections.items.ItemWineGrowingZoneMessages
import forms.sections.items.ItemWineGrowingZoneFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemWineGrowingZoneView
import views.{BaseSelectors, ViewBehaviours}

class ItemWineGrowingZoneViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "ItemWineGrowingZone view" - {

    Seq(ItemWineGrowingZoneMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

       lazy val view = app.injector.instanceOf[ItemWineGrowingZoneView]
        val form = app.injector.instanceOf[ItemWineGrowingZoneFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.legend -> messagesForLanguage.heading,
          Selectors.p(1) -> messagesForLanguage.p1,
          Selectors.link(1) -> messagesForLanguage.findOutMoreLink,
          Selectors.radioButton(1) -> messagesForLanguage.a,
          Selectors.radioButton(2) -> messagesForLanguage.b,
          Selectors.radioButton(3) -> messagesForLanguage.ci,
          Selectors.radioButton(4) -> messagesForLanguage.cii,
          Selectors.radioButton(5) -> messagesForLanguage.ciii_a,
          Selectors.radioButton(6) -> messagesForLanguage.ciii_b,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
