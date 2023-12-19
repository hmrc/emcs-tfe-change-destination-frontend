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

package views.sections.info

import base.SpecBase
import fixtures.messages.sections.info.LocalReferenceNumberMessages
import forms.sections.info.LocalReferenceNumberFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.info.LocalReferenceNumberView
import views.{BaseSelectors, ViewBehaviours}

class LocalReferenceNumberViewSpec extends SpecBase with ViewBehaviours {

 lazy val view = app.injector.instanceOf[LocalReferenceNumberView]
  val form = app.injector.instanceOf[LocalReferenceNumberFormProvider]

  object Selectors extends BaseSelectors

  "LocalReferenceNumber view" - {

    Seq(LocalReferenceNumberMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        "when movement is Deferred" - {

          implicit val doc: Document = Jsoup.parse(view(
            isDeferred = true, form(isDeferred = true), controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftSubmit(testErn, NormalMode)
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.deferredTitle,
            Selectors.h2(1) -> messagesForLanguage.movementInformationSection,
            Selectors.h1 -> messagesForLanguage.deferredHeading,
            Selectors.p(1) -> messagesForLanguage.deferredP1,
            Selectors.button -> messagesForLanguage.continue
          ))
        }

        "when movement is NOT Deferred (new)" - {

          implicit val doc: Document = Jsoup.parse(view(
            isDeferred = false, form(isDeferred = false), controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftSubmit(testErn, NormalMode)
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.newTitle,
            Selectors.h2(1) -> messagesForLanguage.movementInformationSection,
            Selectors.h1 -> messagesForLanguage.newHeading,
            Selectors.p(1) -> messagesForLanguage.newP1,
            Selectors.p(2) -> messagesForLanguage.newP2,
            Selectors.button -> messagesForLanguage.continue
          ))
        }
      }
    }
  }
}
