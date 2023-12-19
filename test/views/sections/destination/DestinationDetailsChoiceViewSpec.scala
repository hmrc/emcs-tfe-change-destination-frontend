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
import fixtures.messages.sections.destination.DestinationDetailsChoiceMessages
import forms.sections.destination.DestinationDetailsChoiceFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.RegisteredConsignee
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.destination.DestinationDetailsChoiceView
import views.{BaseSelectors, ViewBehaviours}

class DestinationDetailsChoiceViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "Destination Details Choice view" - {

    Seq(DestinationDetailsChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

       lazy val view = app.injector.instanceOf[DestinationDetailsChoiceView]
        val form = app.injector.instanceOf[DestinationDetailsChoiceFormProvider].apply(RegisteredConsignee)

        implicit val doc: Document =
          Jsoup.parse(view(
            form,
            controllers.sections.destination.routes.DestinationDetailsChoiceController.onSubmit(request.ern, request.arc, NormalMode),
            RegisteredConsignee
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.destinationSection,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
