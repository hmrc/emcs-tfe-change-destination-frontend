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

package views.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExportVatMessages
import forms.sections.consignee.ConsigneeExportVatFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import views.html.sections.consignee.ConsigneeExportVatView
import views.{BaseSelectors, ViewBehaviours}

class ConsigneeExportVatViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  val submitCall: Call = controllers.sections.consignee.routes.ConsigneeExportVatController.onSubmit(testErn, testArc, NormalMode)

  "ConsigneeExportVatView" - {

    Seq(ConsigneeExportVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        lazy val view = app.injector.instanceOf[ConsigneeExportVatView]
        val form = app.injector.instanceOf[ConsigneeExportVatFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(
          view(
            form = form,
            action = submitCall
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.consigneeInformationSection,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}

