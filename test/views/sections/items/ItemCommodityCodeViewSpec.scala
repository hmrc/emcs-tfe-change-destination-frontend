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
import fixtures.messages.sections.items.ItemCommodityCodeMessages.English
import forms.sections.items.ItemCommodityCodeFormProvider
import models.GoodsType.Beer
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.items.ItemCommodityCodeView
import views.{BaseSelectors, ViewBehaviours}

class ItemCommodityCodeViewSpec extends SpecBase with ViewBehaviours with ItemFixtures {
  object Selectors extends BaseSelectors {
    def selectOption(nthChild: Int) = s"#item-commodity-code > option:nth-child($nthChild)"
  }

  "Item Commodity Code view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

     lazy val view = app.injector.instanceOf[ItemCommodityCodeView]
      val form = app.injector.instanceOf[ItemCommodityCodeFormProvider].apply()
      val submitRoute = controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)

      val testCommodityCode1 = testCommodityCodeTobacco

      val testCommodityCode2 = testCommodityCodeWine

      implicit val doc: Document = Jsoup.parse(view(form, submitRoute, Beer, Seq(testCommodityCode1, testCommodityCode2)).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.subHeadingCaptionSelector -> English.itemSection,
        Selectors.h1 -> English.heading,
        Selectors.p(1) -> English.p,
        Selectors.link(1) -> English.link,
        Selectors.selectOption(1) -> English.defaultItem,
        Selectors.selectOption(2) -> s"${testCommodityCode1.cnCode}: ${testCommodityCode1.cnCodeDescription}",
        Selectors.selectOption(3) -> s"${testCommodityCode2.cnCode}: ${testCommodityCode2.cnCodeDescription}",
        Selectors.button -> English.saveAndContinue,
        Selectors.saveAndExitLink -> English.returnToDraft
      ))

      "must have the correct link to lookup commodity codes" in {
        doc.select(Selectors.link(1)).attr("href") mustBe "https://www.trade-tariff.service.gov.uk/find_commodity"
      }
    }
  }
}

