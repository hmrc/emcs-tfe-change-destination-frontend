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
import fixtures.messages.CountryMessages
import fixtures.messages.sections.items.ItemDegreesPlatoMessages
import forms.sections.items.ItemDegreesPlatoFormProvider
import models.GoodsType.Beer
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemDegreesPlatoView
import views.{BaseSelectors, ViewBehaviours}

class ItemDegreesPlatoViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "ItemDegreesPlato view" - {

    Seq(ItemDegreesPlatoMessages.English -> CountryMessages.English).foreach { case (messagesForLanguage, countryMessages) =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

       lazy val view = app.injector.instanceOf[ItemDegreesPlatoView]
        val form = app.injector.instanceOf[ItemDegreesPlatoFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, Beer).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title(Beer.toSingularOutput()),
          Selectors.h1 -> messagesForLanguage.heading(Beer.toSingularOutput()),
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.itemSection,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.label(ItemDegreesPlatoFormProvider.degreesPlatoField) -> messagesForLanguage.degreesPlatoLabel,
          Selectors.inputSuffix -> messagesForLanguage.degreesPlatoSuffix,
          //Note, this is radio button 2 but index is 3 due to hidden HTML conditional content for radio 1
          Selectors.radioButton(3) -> messagesForLanguage.no,
          Selectors.summary(1) -> messagesForLanguage.detailsSummaryHeading,
          Selectors.bullet(1)  -> countryMessages.austria,
          Selectors.bullet(2)  -> countryMessages.belgium,
          Selectors.bullet(3)  -> countryMessages.bulgaria,
          Selectors.bullet(4)  -> countryMessages.czechia,
          Selectors.bullet(5)  -> countryMessages.germany,
          Selectors.bullet(6)  -> countryMessages.greece,
          Selectors.bullet(7)  -> countryMessages.spain,
          Selectors.bullet(8)  -> countryMessages.italy,
          Selectors.bullet(9)  -> countryMessages.luxembourg,
          Selectors.bullet(10) -> countryMessages.malta,
          Selectors.bullet(11) -> countryMessages.netherlands,
          Selectors.bullet(12) -> countryMessages.poland,
          Selectors.bullet(13) -> countryMessages.portugal,
          Selectors.bullet(14) -> countryMessages.romania,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
