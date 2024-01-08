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

package views.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExemptOrganisationMessages
import forms.sections.consignee.ConsigneeExemptOrganisationFormProvider
import models.NormalMode
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.test.FakeRequest
import views.html.sections.consignee.ConsigneeExemptOrganisationView
import views.{BaseSelectors, ViewBehaviours}

class ConsigneeExemptOrganisationViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors


  s"consignee exempt organisation View" - {

    Seq(ConsigneeExemptOrganisationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

       lazy val view = app.injector.instanceOf[ConsigneeExemptOrganisationView]
        val form = app.injector.instanceOf[ConsigneeExemptOrganisationFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          items = Seq(),
          call = controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onSubmit(request.ern, request.arc, NormalMode)).toString()
        )

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.consigneeInformationSection,
          Selectors.label("memberState") -> messagesForLanguage.memberStateLabel,
          Selectors.label("certificateSerialNumber") -> messagesForLanguage.certificateSerialNumberLabel,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}
