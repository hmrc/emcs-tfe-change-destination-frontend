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

package views

import base.SpecBase
import fixtures.messages.NotPermittedMessages
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.NotPermittedView

class NotPermittedViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "NotPermitted view" - {

    Seq(NotPermittedMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        val view = app.injector.instanceOf[NotPermittedView]

        implicit val doc: Document = Jsoup.parse(view().toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.title -> messagesForLanguage.title,
          Selectors.p(1) -> messagesForLanguage.p1,
          Selectors.p(2) -> messagesForLanguage.p2,
          Selectors.p(3) -> messagesForLanguage.p3
        ))
      }
    }
  }


}
