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
import fixtures.messages.UnitOfMeasureMessages
import fixtures.messages.sections.items.ItemQuantityMessages
import models.requests.DataRequest
import models.{CheckMode, UnitOfMeasure, UserAnswers}
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemQuantityPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemQuantitySummarySpec extends SpecBase with Matchers {

  class Test(val userAnswers: UserAnswers) {
    implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
  }

  "ItemQuantitySummary" - {

    Seq(ItemQuantityMessages.English -> UnitOfMeasureMessages.English).foreach { case (messagesForLanguage, unitOfMeasure) =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "if provided" - {
          "must return a row" in new Test(
            emptyUserAnswers
              .set(ItemQuantityPage(testIndex1), BigDecimal(1.23))
          ) {
            ItemQuantitySummary.row(
              idx = testIndex1,
              unitOfMeasure = UnitOfMeasure.Kilograms
            ) mustBe
              Some(summaryListRowBuilder(
                key = messagesForLanguage.cyaLabel,
                value = s"1.23 ${UnitOfMeasure.Kilograms.toShortFormatMessage()}",
                changeLink = Some(ActionItemViewModel(
                  href = controllers.sections.items.routes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  content = messagesForLanguage.change,
                  id = s"changeItemQuantity${testIndex1.displayIndex}"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden))
              ))
          }
        }
        "if not provided" - {
          "must not return a row" in new Test(emptyUserAnswers) {
            ItemQuantitySummary.row(
              idx = testIndex1,
              unitOfMeasure = UnitOfMeasure.Kilograms
            ) mustBe None
          }
        }
      }
    }
  }
}
