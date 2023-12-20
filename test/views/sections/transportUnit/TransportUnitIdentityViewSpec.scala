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

package views.sections.transportUnit

import base.SpecBase
import fixtures.messages.sections.transportUnit.TransportUnitIdentityMessages
import forms.sections.transportUnit.TransportUnitIdentityFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.transportUnit.TransportUnitIdentityView
import views.{BaseSelectors, ViewBehaviours}

class TransportUnitIdentityViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors {
    val errorSummary: Int => String = index => s".govuk-error-summary__list > li:nth-child(${index})"
    val errorField: String = "p.govuk-error-message"
    val returnToDraftLink: String = "#save-and-exit"
  }

  "TransportUnitIdentityView" - {
    Seq(
      ("FixedTransport", TransportUnitType.FixedTransport),
      ("Container", TransportUnitType.Container),
      ("Tractor", TransportUnitType.Tractor),
      ("Trailer", TransportUnitType.Trailer),
      ("Vehicle", TransportUnitType.Vehicle)
    ).foreach {
      case (name, transportUnitType) =>
        Seq(TransportUnitIdentityMessages.English).foreach { messagesForLanguage =>

          s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for $name" - {

            val userAnswers = emptyUserAnswers.set(TransportUnitTypePage(testIndex1), transportUnitType)

            implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

           lazy val view = app.injector.instanceOf[TransportUnitIdentityView]
            val form = app.injector.instanceOf[TransportUnitIdentityFormProvider].apply(transportUnitType)

            implicit val doc: Document = Jsoup.parse(
              view(
                form = form,
                transportUnitType = transportUnitType,
                idx = testIndex1,
                mode = NormalMode
              ).toString())

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title(transportUnitType),
              Selectors.h1 -> messagesForLanguage.heading(transportUnitType),
              Selectors.button -> messagesForLanguage.saveAndContinue,
              Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
            ))
          }

          Seq(
            ("value not provided", "", messagesForLanguage.errorEmpty(transportUnitType)),
            ("value too long", "0" * 36, messagesForLanguage.errorInputTooLong(transportUnitType)),
            ("value with invalid characters", "Weird*>Ch", messagesForLanguage.errorInputDisallowedCharacters(transportUnitType)),
          ) foreach {
            case (reason, input, errorMessage) =>
              s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for $name with error - $reason" - {

                val userAnswers = emptyUserAnswers.set(TransportUnitTypePage(testIndex1), transportUnitType)

                implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
                implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)

               lazy val view = app.injector.instanceOf[TransportUnitIdentityView]
                val form = app.injector.instanceOf[TransportUnitIdentityFormProvider]
                  .apply(transportUnitType)
                  .bind(Map("value" -> input))


                implicit val doc: Document = Jsoup.parse(
                  view(
                    form = form,
                    transportUnitType = transportUnitType,
                    idx = testIndex1,
                    mode = NormalMode
                  ).toString())

                behave like pageWithExpectedElementsAndMessages(Seq(
                  Selectors.title -> messagesForLanguage.errorMessageHelper(messagesForLanguage.title(transportUnitType)),
                  Selectors.h1 -> messagesForLanguage.heading(transportUnitType),
                  Selectors.errorSummary(1) -> errorMessage,
                  Selectors.errorField -> messagesForLanguage.errorMessageHelper(errorMessage),
                  Selectors.button -> messagesForLanguage.saveAndContinue,
                  Selectors.returnToDraftLink -> messagesForLanguage.returnToDraft
                ))
              }
          }
        }
    }
  }
}

