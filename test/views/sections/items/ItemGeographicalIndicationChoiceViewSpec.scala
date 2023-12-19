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
import fixtures.messages.sections.items.ItemGeographicalIndicationChoiceMessages
import forms.sections.items.ItemGeographicalIndicationChoiceFormProvider
import models.GoodsType.Wine
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemGeographicalIndicationChoiceView
import views.{BaseSelectors, ViewBehaviours}

class ItemGeographicalIndicationChoiceViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    def radioHint(index: Int) = s".govuk-radios > div:nth-child($index) > div"

    val radioDivider = ".govuk-radios__divider"
  }

  "ItemGeographicalIndicationChoice view" - {

    Seq(ItemGeographicalIndicationChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

       lazy val view = app.injector.instanceOf[ItemGeographicalIndicationChoiceView]
        val form = app.injector.instanceOf[ItemGeographicalIndicationChoiceFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Wine).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(Wine.toSingularOutput()),
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.legend -> messagesForLanguage.heading(Wine.toSingularOutput()),
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.radioButton(1) -> messagesForLanguage.pdoRadioOption,
          Selectors.radioHint(1) -> messagesForLanguage.pdoRadioHint,
          Selectors.radioButton(2) -> messagesForLanguage.pgiRadioOption,
          Selectors.radioHint(2) -> messagesForLanguage.pgiRadioHint,
          Selectors.radioButton(3) -> messagesForLanguage.giRadioOption,
          Selectors.radioHint(3) -> messagesForLanguage.giRadioHint,
          Selectors.radioDivider -> messagesForLanguage.divider,
          Selectors.radioButton(5) -> messagesForLanguage.noRadioOption,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
