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

package views.sections.destination

import base.SpecBase
import fixtures.messages.sections.destination.DestinationWarehouseVatMessages
import forms.sections.destination.DestinationWarehouseVatFormProvider
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{RegisteredConsignee, TemporaryCertifiedConsignee}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import views.html.sections.destination.DestinationWarehouseVatView
import views.{BaseSelectors, ViewBehaviours}


class DestinationWarehouseVatViewSpec extends SpecBase with ViewBehaviours {

  lazy val view = app.injector.instanceOf[DestinationWarehouseVatView]
  lazy val form = app.injector.instanceOf[DestinationWarehouseVatFormProvider]

  val skipRoute: Call = Call("GET", "/skip-url")

  object Selectors extends BaseSelectors

  "DestinationWarehouseVatView" - {

    Seq(DestinationWarehouseVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        "when the DestinationType is skippable" - {

          implicit val doc: Document = Jsoup.parse(view(
            form = form(RegisteredConsignee),
            action = testOnwardRoute,
            movementScenario = RegisteredConsignee,
            skipQuestionCall = skipRoute
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(RegisteredConsignee.stringValue),
            Selectors.h1 -> messagesForLanguage.heading(RegisteredConsignee.stringValue),
            Selectors.hint -> messagesForLanguage.hint,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.skipQuestion
          ))
        }

        "when the DestinationType is NOT skippable" - {

          implicit val doc: Document = Jsoup.parse(view(
            form = form(TemporaryCertifiedConsignee),
            action = testOnwardRoute,
            movementScenario = TemporaryCertifiedConsignee,
            skipQuestionCall = skipRoute
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(TemporaryCertifiedConsignee.stringValue),
            Selectors.h1 -> messagesForLanguage.heading(TemporaryCertifiedConsignee.stringValue),
            Selectors.hint -> messagesForLanguage.hint,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.link(1) -> messagesForLanguage.returnToDraft
          ))
        }
      }
    }
  }
}

