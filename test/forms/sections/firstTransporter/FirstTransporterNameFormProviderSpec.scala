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

package forms.sections.firstTransporter

import base.SpecBase
import fixtures.messages.sections.firstTransporter.FirstTransporterNameMessages
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class FirstTransporterNameFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "firstTransporterName.error.required"
  val lengthKey = "firstTransporterName.error.length"
  val invalidKey = "firstTransporterName.error.invalidCharacter"

  val maxLength = 182

  val form = new FirstTransporterNameFormProvider()()

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

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error Messages" - {

    Seq(FirstTransporterNameMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message when failed to supply a field value" in {
          msgs(requiredKey) mustBe messagesForLanguage.errorRequired
        }

        "have the correct error message when the field value is too large" in {
          msgs(lengthKey) mustBe messagesForLanguage.errorLength
        }

        "have the correct error message when the field value has invalid characters" in {
          msgs(invalidKey) mustBe messagesForLanguage.errorInvalidCharacter
        }

      }
    }
  }
}
