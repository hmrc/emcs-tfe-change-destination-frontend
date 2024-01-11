/*
 * Copyright 2024 HM Revenue & Customs
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

package views.sections.info

import base.SpecBase
import fixtures.messages.sections.info.ChangeDestinationTypeMessages
import forms.sections.info.ChangeDestinationTypeFormProvider
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import viewmodels.checkAnswers.sections.info.ChangeDestinationTypeHelper
import views.html.sections.info.ChangeDestinationTypeView
import views.{BaseSelectors, ViewBehaviours}

class ChangeDestinationTypeViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val summaryListKey = ".govuk-summary-list__key"
    val summaryListValue = ".govuk-summary-list__value"
  }

  lazy val view = app.injector.instanceOf[ChangeDestinationTypeView]
  val form = app.injector.instanceOf[ChangeDestinationTypeFormProvider].apply()
  val summaryListHelper = app.injector.instanceOf[ChangeDestinationTypeHelper]


  Seq(ChangeDestinationTypeMessages.English).foreach { messagesForLanguage =>

    s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

      MovementScenario.values.foreach { movementScenario =>

        s"when movement scenario / destination type is $movementScenario" - {

            implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
            implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(DestinationTypePage, movementScenario))

            implicit val doc: Document = Jsoup.parse(
              view(
                form = form,
                summaryList = summaryListHelper.summaryList(),
                testOnwardRoute
              ).toString())

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title,
              Selectors.h1 -> messagesForLanguage.heading,
              Selectors.subHeadingCaptionSelector -> messagesForLanguage.changeOfDestinationSection(testArc),
              Selectors.summaryListKey -> messagesForLanguage.summaryListKey,
              Selectors.summaryListValue -> movementScenario.stringValue.capitalize,
              Selectors.legend -> messagesForLanguage.legend,
              Selectors.radioButton(1) -> messagesForLanguage.yes,
              Selectors.radioButton(2) -> messagesForLanguage.noOption,
              Selectors.button -> messagesForLanguage.continue
            ))

        }
      }
    }
  }
}
