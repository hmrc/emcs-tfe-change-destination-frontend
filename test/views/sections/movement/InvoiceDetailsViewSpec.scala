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

package views.sections.movement

import base.SpecBase
import fixtures.messages.sections.movement.InvoiceDetailsMessages
import forms.sections.movement.InvoiceDetailsFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.test.FakeRequest
import utils.DateTimeUtils
import views.html.sections.movement.InvoiceDetailsView
import views.{BaseSelectors, ViewBehaviours}

import java.time.LocalDate

class InvoiceDetailsViewSpec extends SpecBase with ViewBehaviours with DateTimeUtils {

  object Selectors extends BaseSelectors

  lazy val view = app.injector.instanceOf[InvoiceDetailsView]
  val form = app.injector.instanceOf[InvoiceDetailsFormProvider].apply()

  "Invoice Details view" - {

    Seq(InvoiceDetailsMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[_] = dataRequest(FakeRequest())

        val currentDate = LocalDate.of(2023, 1, 1).formatDateNumbersOnly()

        implicit val doc: Document = Jsoup.parse(view(
          form = form,
          currentDate = currentDate,
          onSubmitCall = testOnwardRoute
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.movementInformationSection,
          Selectors.p(1) -> messagesForLanguage.text,
          Selectors.label("invoice-reference") -> messagesForLanguage.referenceLabel,
          Selectors.legend -> messagesForLanguage.dateLabel,
          Selectors.hint -> messagesForLanguage.dateHint(currentDate),
          Selectors.button -> messagesForLanguage.saveAndContinue
        ))
      }
    }
  }
}
