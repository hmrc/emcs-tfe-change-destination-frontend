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

package views

import base.SpecBase
import fixtures.messages.ContinueDraftMessages
import forms.ContinueDraftFormProvider
import models.requests.OptionalDataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ContinueDraftView

class ContinueDraftViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val insetText = "main .govuk-inset-text p"
  }

  lazy val view: ContinueDraftView = app.injector.instanceOf[ContinueDraftView]
  lazy val form: Form[Boolean] = app.injector.instanceOf[ContinueDraftFormProvider].apply()

  "ContinueDraft View" - {

    Seq(ContinueDraftMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: OptionalDataRequest[AnyContentAsEmpty.type] = optionalDataRequest(FakeRequest(), Some(emptyUserAnswers))

        implicit val doc: Document = Jsoup.parse(view(
          form,
          controllers.routes.IndexController.onSubmit(testErn, testArc),
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.changeOfDestinationSection(testArc),
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.p(1) -> messagesForLanguage.p1,
          Selectors.insetText -> messagesForLanguage.insetText,
          Selectors.legend -> messagesForLanguage.label,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.continue
        ))
      }
    }
  }
}
