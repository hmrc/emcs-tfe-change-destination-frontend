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

package views.sections.transportUnit

import base.SpecBase
import fixtures.messages.sections.transportUnit.{TransportUnitCheckAnswersMessages, TransportUnitTypeMessages}
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType.FixedTransport
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.CheckYourAnswersTransportUnitsHelper
import views.html.sections.transportUnit.TransportUnitCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class TransportUnitCheckAnswersViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors {
    val summaryListKey = ".govuk-summary-list__key"
    val summaryListValue = ".govuk-summary-list__value"
  }

  trait Fixture {

    val messagesForLanguage = TransportUnitCheckAnswersMessages.English
    implicit val msgs: Messages = messages(Seq(TransportUnitTypeMessages.English.lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers.set(TransportUnitTypePage(testIndex1), FixedTransport))
    val summaryListHelper = app.injector.instanceOf[CheckYourAnswersTransportUnitsHelper].summaryList()

    lazy val view = app.injector.instanceOf[TransportUnitCheckAnswersView]

    implicit val doc: Document = Jsoup.parse(view(summaryListHelper).toString())
  }

  "Transport Unit Check Answers view" - {
    s"when being rendered in lang code of 'en'" - new Fixture {
      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.h2(1) -> messagesForLanguage.transportUnitsSection,
        Selectors.hiddenText -> messagesForLanguage.hiddenSectionContent,
        Selectors.title -> messagesForLanguage.title,
        Selectors.h1 -> messagesForLanguage.heading,
        Selectors.summaryListKey -> messagesForLanguage.summaryListKey,
        Selectors.summaryListValue -> TransportUnitTypeMessages.English.addToListValue(FixedTransport),
        Selectors.button -> messagesForLanguage.confirmAnswers
      ))
    }
  }
}
