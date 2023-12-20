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

package views.sections.consignor

import base.SpecBase
import fixtures.messages.sections.consignor.CheckYourAnswersConsignorMessages
import models.CheckMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.consignor.CheckYourAnswersConsignorView
import views.{BaseSelectors, ViewBehaviours}

class CheckYourAnswersConsignorViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    def govukSummaryListKey(id: Int) = s".govuk-summary-list__row:nth-of-type($id) .govuk-summary-list__key"
    val govukSummaryListChangeLink = ".govuk-summary-list__actions .govuk-link"
  }

  "CheckYourAnswersConsignor view" - {

    Seq(CheckYourAnswersConsignorMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

       lazy val view = app.injector.instanceOf[CheckYourAnswersConsignorView]

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.consignor.routes.CheckYourAnswersConsignorController.onSubmit(testErn, testDraftId),
          testErn,
          testDraftId,
          testUserAddress,
          testMinTraderKnownFacts
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.traderName,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.ern,
          Selectors.govukSummaryListKey(3) -> messagesForLanguage.address,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))

        "have a link to change details" in {

          doc.select(Selectors.govukSummaryListChangeLink).attr("href") mustBe
            controllers.sections.consignor.routes.ConsignorAddressController.onPageLoad(testErn, testDraftId, CheckMode).url
        }
      }
    }
  }

}
