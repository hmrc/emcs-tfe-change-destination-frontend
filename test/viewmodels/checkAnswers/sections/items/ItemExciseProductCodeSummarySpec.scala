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

package viewmodels.checkAnswers.sections.items

import base.SpecBase
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemExciseProductCodeMessages
import models.ReviewMode
import models.requests.DataRequest
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemExciseProductCodePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.p

class ItemExciseProductCodeSummarySpec extends SpecBase with Matchers with ItemFixtures {

  lazy val itemExciseProductCodeSummary: ItemExciseProductCodeSummary = app.injector.instanceOf[ItemExciseProductCodeSummary]
  lazy val p: p = app.injector.instanceOf[p]

  "ItemExciseProductCodeSummary" - {

    Seq(ItemExciseProductCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's an answer" - {

          "must output the expected row" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] =
              dataRequest(FakeRequest(), emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testCommodityCodeWine.exciseProductCode))

            itemExciseProductCodeSummary.row(testIndex1, testCommodityCodeWine, ReviewMode) mustBe
              summaryListRowBuilder(
                key = messagesForLanguage.cyaLabel,
                value = HtmlContent(HtmlFormat.fill(Seq(
                  p()(Html(testCommodityCodeWine.exciseProductCode)),
                  p()(Html(testCommodityCodeWine.exciseProductCodeDescription))
                ))),
                Some(ActionItemViewModel(
                  href = controllers.sections.items.routes.ItemExciseProductCodeController.onPageLoad(request.ern, request.arc, testIndex1, ReviewMode).url,
                  content = messagesForLanguage.change,
                  id = s"changeItemExciseProductCode${testIndex1.displayIndex}"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
              )
          }
        }
      }
    }
  }
}
