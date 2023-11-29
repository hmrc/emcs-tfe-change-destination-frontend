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
import fixtures.messages.sections.destination.DestinationWarehouseExciseMessages.English
import forms.XSS_REGEX
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class DestinationWarehouseExciseFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "destinationWarehouseExcise.error.required"
  val lengthKey = "destinationWarehouseExcise.error.length"
  val maxLength = 16
  val invalidCharactersKey = "destinationWarehouseExcise.error.invalidCharacter"

  val form = new DestinationWarehouseExciseFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "0" * maxLength
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithXSSCharacters(
      form,
      fieldName,
      FormError(fieldName, invalidCharactersKey, Seq(XSS_REGEX))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error Messages" - {

    implicit val msgs: Messages = messages(Seq(English.lang))

    s"when output for language code '${English.lang.code}'" - {

      "have the correct required error message" in {

        msgs(requiredKey) mustBe
          English.errorRequired
      }

      "have the correct length error message" in {

        msgs(lengthKey) mustBe
          English.errorLength
      }

      "have the correct invalidCharacters error message" in {

        msgs(invalidCharactersKey) mustBe
          English.errorInvalidCharacters
      }
    }
  }
}
