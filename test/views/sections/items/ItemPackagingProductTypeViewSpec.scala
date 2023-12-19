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
import fixtures.messages.sections.items.ItemPackagingProductTypeMessages.English
import forms.sections.items.ItemPackagingProductTypeFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemPackagingProductTypeView
import views.{BaseSelectors, ViewBehaviours}

class ItemPackagingProductTypeViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {

  object Selectors extends BaseSelectors

  "ItemPackagingProductType view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

      val view = app.injector.instanceOf[ItemPackagingProductTypeView]
      val form = app.injector.instanceOf[ItemPackagingProductTypeFormProvider].apply()

      implicit val doc: Document = Jsoup.parse(view(form, description = testItemPackagingTypes.head.description, testOnwardRoute).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.subHeadingCaptionSelector -> English.itemSection,
        Selectors.p(1) -> English.p(testItemPackagingTypes.head.description),
        Selectors.strong(1) -> testItemPackagingTypes.head.description,
        Selectors.radioButton(1) -> English.yes,
        Selectors.radioButton(2) -> English.noMoreThanOne,
        Selectors.button -> English.saveAndContinue,
        Selectors.link(1) -> English.returnToDraft
      ))
    }
  }
}
