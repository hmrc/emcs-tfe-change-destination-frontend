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

package views.sections.importInformation

import base.SpecBase
import fixtures.messages.sections.importInformation.ImportCustomsOfficeCodeMessages
import forms.sections.importInformation.ImportCustomsOfficeCodeFormProvider
import models.requests.DataRequest
import models.{GreatBritainRegisteredConsignor, NorthernIrelandRegisteredConsignor, UserType}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Lang, Messages}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.importInformation.ImportCustomsOfficeCodeView
import views.{BaseSelectors, ViewBehaviours}

class ImportCustomsOfficeCodeViewSpec extends SpecBase with ViewBehaviours {

  class Fixture(lang: Lang, userType: UserType) {

    implicit val msgs: Messages = messages(Seq(lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

   lazy val view = app.injector.instanceOf[ImportCustomsOfficeCodeView]
    val form = app.injector.instanceOf[ImportCustomsOfficeCodeFormProvider].apply()

    implicit val doc: Document = Jsoup.parse(
      view(
        form = form,
        action = testOnwardRoute,
        userType,
      ).toString())
  }

  object Selectors extends BaseSelectors

  "ImportCustomsOfficeCodeView" - {

    Seq(ImportCustomsOfficeCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        Seq(GreatBritainRegisteredConsignor, NorthernIrelandRegisteredConsignor).foreach { userType =>

          s"when user type is '$userType'" - new Fixture(messagesForLanguage.lang, userType) {

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title(userType),
              Selectors.h1 -> messagesForLanguage.heading(userType),
              Selectors.subHeadingCaptionSelector -> messagesForLanguage.importInformationSection,
              Selectors.p(1) -> messagesForLanguage.paragraph(userType),
              Selectors.button -> messagesForLanguage.saveAndContinue,
              Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
            ))
          }
        }
      }
    }
  }
}
