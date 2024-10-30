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

package views.sections.exportInformation

import base.SpecBase
import fixtures.messages.sections.exportInformation.ExportCustomsOfficeMessages
import forms.sections.exportInformation.ExportCustomsOfficeFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Lang, Messages}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.exportInformation.ExportCustomsOfficeView
import views.{BaseSelectors, ViewBehaviours}

class ExportCustomsOfficeViewSpec extends SpecBase with ViewBehaviours {

  class Fixture(lang: Lang, euExport: Boolean) {

    implicit val msgs: Messages = messages(Seq(lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

   lazy val view = app.injector.instanceOf[ExportCustomsOfficeView]
    val form = app.injector.instanceOf[ExportCustomsOfficeFormProvider].apply()

    implicit val doc: Document = Jsoup.parse(
      view(
        form = form,
        action = testOnwardRoute,
        euExport
      ).toString())
  }

  object Selectors extends BaseSelectors

  "ExportCustomsOfficeView" - {

    Seq(ExportCustomsOfficeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        Seq(true, false).foreach { euExport =>

          s"when EU export is '$euExport'" - new Fixture(messagesForLanguage.lang, euExport) {

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title,
              Selectors.h1 -> messagesForLanguage.heading,
              Selectors.subHeadingCaptionSelector -> messagesForLanguage.exportInformationSection,
              Selectors.p(1) -> messagesForLanguage.p1(euExport),
              Selectors.label("value") -> messagesForLanguage.label,
              Selectors.hint -> messagesForLanguage.hint,
              Selectors.button -> messagesForLanguage.saveAndContinue,
              Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
            ))
          }
        }
      }
    }
  }
}

