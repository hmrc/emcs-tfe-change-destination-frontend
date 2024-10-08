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
import fixtures.messages.sections.consignee.ConsigneeExciseMessages.English
import forms.sections.consignee.ConsigneeExciseFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.consignee.ConsigneeExcisePage
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.consignee.ConsigneeExciseView
import views.{BaseSelectors, ViewBehaviours}

class ConsigneeExciseViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "Consignee Excise view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

      lazy val view = app.injector.instanceOf[ConsigneeExciseView]
      val form = app.injector.instanceOf[ConsigneeExciseFormProvider].apply(None)

      implicit val doc: Document = Jsoup.parse(
        view(
          form,
          testOnwardRoute,
          isNorthernIrishTemporaryRegisteredConsignee = false,
          isNorthernIrishTemporaryCertifiedConsignee = false
        ).toString()
      )

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.consigneeInformationSection,
        Selectors.hint -> English.hint,
        Selectors.button -> English.saveAndContinue
      ))
    }

    s"when being rendered in lang code of '${English.lang.code}' when isNorthernIrishTemporaryRegisteredConsignee is true" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

      lazy val view = app.injector.instanceOf[ConsigneeExciseView]
      val form = app.injector.instanceOf[ConsigneeExciseFormProvider].apply(None)

      implicit val doc: Document = Jsoup.parse(
        view(
          form,
          testOnwardRoute,
          isNorthernIrishTemporaryRegisteredConsignee = true,
          isNorthernIrishTemporaryCertifiedConsignee = false
        ).toString()
      )

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.temporaryRegisteredConsigneeTitle,
        Selectors.h1 -> English.temporaryRegisteredConsigneeHeading,
        Selectors.h2(1) -> English.consigneeInformationSection,
        Selectors.hint -> English.temporaryRegisteredConsigneeHint,
        Selectors.button -> English.saveAndContinue
      ))
    }

    s"when being rendered in lang code of '${English.lang.code}' when isNorthernIrishTemporaryCertifiedConsignee is true" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

      lazy val view = app.injector.instanceOf[ConsigneeExciseView]
      val form = app.injector.instanceOf[ConsigneeExciseFormProvider].apply(None)

      implicit val doc: Document = Jsoup.parse(
        view(
          form,
          testOnwardRoute,
          isNorthernIrishTemporaryRegisteredConsignee = false,
          isNorthernIrishTemporaryCertifiedConsignee = true
        ).toString()
      )

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.temporaryCertifiedConsigneeTitle,
        Selectors.h1 -> English.temporaryCertifiedConsigneeHeading,
        Selectors.h2(1) -> English.consigneeInformationSection,
        Selectors.hint -> English.temporaryCertifiedConsigneeHint,
        Selectors.button -> English.saveAndContinue
      ))
    }
  }
}
