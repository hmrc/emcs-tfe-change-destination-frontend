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

package views.sections.firstTransporter

import base.SpecBase
import fixtures.messages.sections.firstTransporter.FirstTransporterVatMessages
import forms.sections.firstTransporter.FirstTransporterVatFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Lang, Messages}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.firstTransporter.FirstTransporterVatView
import views.{BaseSelectors, ViewBehaviours}

class FirstTransporterVatViewSpec extends SpecBase with ViewBehaviours {

  class Fixture(lang: Lang) {

    implicit val msgs: Messages = messages(Seq(lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

   lazy val view = app.injector.instanceOf[FirstTransporterVatView]
    val form = app.injector.instanceOf[FirstTransporterVatFormProvider].apply()

    implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute).toString())
  }

  object Selectors extends BaseSelectors

  "FirstTransporterVatView" - {

    Seq(FirstTransporterVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when rendered for Other" - new Fixture(messagesForLanguage.lang) {

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.hint -> messagesForLanguage.hint,
            Selectors.link(1) -> messagesForLanguage.nonGbVatLink,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
          ))
        }
      }
    }
  }
}

