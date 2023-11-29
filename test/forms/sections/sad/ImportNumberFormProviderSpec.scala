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

package forms.sections.sad

import base.SpecBase
import fixtures.messages.sections.sad.ImportNumberMessages
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class ImportNumberFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "importNumber.error.required"
  val lengthKey = "importNumber.error.length"
  val xssKey = "importNumber.error.invalidCharacter"
  val alphanumericKey = "importNumber.error.alphanumeric"
  val maxLength = 21

  val form = new ImportNumberFormProvider()()

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

    Seq(ImportNumberMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for required" in {

          msgs("importNumber.error.required") mustBe
            messagesForLanguage.errorRequired
        }

        "have the correct error message for length" in {

          msgs("importNumber.error.length") mustBe
            messagesForLanguage.errorLength
        }

        "have the correct error message for xss" in {

          msgs("importNumber.error.invalidCharacter") mustBe
            messagesForLanguage.errorXss
        }

      }
    }
  }

}
