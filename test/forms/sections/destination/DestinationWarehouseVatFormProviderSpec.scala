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

package forms.sections.destination

import base.SpecBase
import fixtures.messages.sections.destination.DestinationWarehouseVatMessages
import forms.XSS_REGEX
import forms.behaviours.StringFieldBehaviours
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{RegisteredConsignee, TemporaryCertifiedConsignee}
import play.api.data.{Form, FormError}

class DestinationWarehouseVatFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "destinationWarehouseVat.error.required"
  val requiredKeySkippable = "destinationWarehouseVat.error.required.skippable"
  val lengthKey = "destinationWarehouseVat.error.length"
  val invalidCharactersKey = "destinationWarehouseVat.error.invalidCharacters"
  val maxLength = 16

  def form(destinationType: MovementScenario = RegisteredConsignee): Form[String] =
    new DestinationWarehouseVatFormProvider()(destinationType)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form(),
      fieldName,
      "0" * maxLength
    )

    behave like fieldWithMaxLength(
      form(),
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithXSSCharacters(
      form(),
      fieldName,
      FormError(fieldName, invalidCharactersKey, Seq(XSS_REGEX))
    )

    "when skippable" - {
      behave like mandatoryField(
        form(RegisteredConsignee),
        fieldName,
        requiredError = FormError(fieldName, requiredKeySkippable)
      )
    }

    "when NOT skippable" - {
      behave like mandatoryField(
        form(TemporaryCertifiedConsignee),
        fieldName,
        requiredError = FormError(fieldName, requiredKey)
      )
    }
  }

  "Error Message content" - {

    Seq(DestinationWarehouseVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in language code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs = messages(Seq(messagesForLanguage.lang))

        "output the correct error message content" in {
          msgs(requiredKey) mustBe messagesForLanguage.errorRequired
          msgs(requiredKeySkippable) mustBe messagesForLanguage.errorRequiredSkippable
          msgs(lengthKey, maxLength) mustBe messagesForLanguage.errorLength(maxLength)
          msgs(invalidCharactersKey) mustBe messagesForLanguage.errorInvalidCharacters
        }
      }
    }
  }
}
