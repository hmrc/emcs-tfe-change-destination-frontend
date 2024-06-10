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
import fixtures.messages.sections.consignee.ConsigneeExportInformationMessages
import forms.sections.consignee.ConsigneeExportInformationFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import views.html.sections.consignee.ConsigneeExportInformationView
import views.{BaseSelectors, ViewBehaviours}

class ConsigneeExportInformationViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  val action: Call = controllers.sections.consignee.routes.ConsigneeExportInformationController.onSubmit(testErn, testArc, NormalMode)

  lazy val view = app.injector.instanceOf[ConsigneeExportInformationView]
  val form = app.injector.instanceOf[ConsigneeExportInformationFormProvider].apply()

  //scalastyle:off
  "ConsigneeExportInformationView" - {

    Seq(ConsigneeExportInformationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        implicit val doc: Document = Jsoup.parse(
          view(
            form = form,
            testOnwardRoute
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.consigneeInformationSection,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.checkboxItem(1) -> messagesForLanguage.checkboxItemForVat,
          Selectors.checkboxItem(2) -> messagesForLanguage.checkboxItemForEori,
          Selectors.checkboxDividerItem(3) -> messagesForLanguage.or,
          Selectors.checkboxItem(4) -> messagesForLanguage.checkboxItemForNoInfo,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}

