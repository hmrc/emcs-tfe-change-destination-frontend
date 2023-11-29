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

package forms.sections.exportInformation

import base.SpecBase
import fixtures.messages.sections.exportInformation.ExportCustomsOfficeMessages
import forms.behaviours.StringFieldBehaviours
import forms.{CUSTOMS_OFFICE_CODE_REGEX, XSS_REGEX}
import play.api.data.FormError
import play.api.i18n.Messages

class ExportCustomsOfficeFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "exportCustomsOffice.error.required"
  val lengthKey = "exportCustomsOffice.error.length"
  val xssKey = "exportCustomsOffice.error.invalidCharacter"
  val regexKey = "exportCustomsOffice.error.customOfficeRegex"
  val requiredLength = 8

  val form = new ExportCustomsOfficeFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithFixedLength(
      form,
      fieldName,
      lengthError = FormError(fieldName, lengthKey, Seq(requiredLength)),
      requiredLength = requiredLength
    )

    s"not bind a value that contains XSS chars" in {

      val boundForm = form.bind(Map(fieldName -> "<1234567"))
      boundForm.errors mustBe Seq(FormError(fieldName, xssKey, Seq(XSS_REGEX)))
    }

    s"not bind a value that doesn't start with two alpha chars" in {

      val boundForm = form.bind(Map(fieldName -> "12345678"))
      boundForm.errors mustBe Seq(FormError(fieldName, regexKey, Seq(CUSTOMS_OFFICE_CODE_REGEX)))
    }

    s"bind a value that meets the expected regex" in {

      val boundForm = form.bind(Map(fieldName -> "GB345678"))
      boundForm.errors mustBe Seq()
      boundForm.value mustBe Some("GB345678")
    }
  }

  "Error Messages" - {

    Seq(ExportCustomsOfficeMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct required error message" in {

          msgs("exportCustomsOffice.error.required") mustBe
            messagesForLanguage.errorRequired
        }

        "have the correct length error message" in {

          msgs("exportCustomsOffice.error.length", requiredLength) mustBe
            messagesForLanguage.errorLength(requiredLength)
        }

        "have the correct invalidCharacter error message" in {

          msgs("exportCustomsOffice.error.invalidCharacter") mustBe
            messagesForLanguage.errorInvalidCharacter
        }

        "have the correct customsOfficeRegex error message" in {

          msgs("exportCustomsOffice.error.customOfficeRegex") mustBe
            messagesForLanguage.errorCustomOfficeRegex
        }
      }
    }
  }
}
