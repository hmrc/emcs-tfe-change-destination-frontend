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

package forms.sections.documents

import base.SpecBase
import fixtures.messages.sections.documents.DocumentReferenceMessages
import forms.XSS_REGEX
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class DocumentReferenceFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "documentReference.error.required"
  val lengthKey = "documentReference.error.length"
  val xssKey = "documentReference.error.xss"
  val maxLength = 35

  val form = new DocumentReferenceFormProvider()()

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

    behave like fieldWithXSSCharacters(
      form,
      fieldName,
      requiredError = FormError(fieldName, xssKey, Seq(XSS_REGEX))
    )
  }

  "Error Messages" - {

    Seq(DocumentReferenceMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for required" in {

          msgs("documentReference.error.required") mustBe
            messagesForLanguage.errorRequired
        }

        "have the correct error message for length" in {

          msgs("documentReference.error.length") mustBe
            messagesForLanguage.errorLength
        }

        "have the correct error message for xss" in {

          msgs("documentReference.error.xss") mustBe
            messagesForLanguage.errorXss
        }
      }
    }
  }
}
