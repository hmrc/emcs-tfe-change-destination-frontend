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

package forms.sections.items

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import forms.{ALPHANUMERIC_REGEX, TEXTAREA_MAX_LENGTH, XSS_REGEX}
import play.api.data.FormError
import fixtures.messages.sections.items.ItemWineMoreInformationMessages
import play.api.i18n.Messages

class ItemWineMoreInformationFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "itemWineMoreInformation.error.required"
  val lengthKey = "itemWineMoreInformation.error.length"
  val xssKey = "itemWineMoreInformation.error.invalidCharacter"
  val alphanumericKey = "itemWineMoreInformation.error.alphanumeric"
  val maxLength = 350

  val form = new ItemWineMoreInformationFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "a" * maxLength
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
      FormError(fieldName, xssKey, Seq(XSS_REGEX))
    )

    behave like fieldWithAtLeastOneAlphanumeric(
      form,
      fieldName,
      FormError(fieldName, alphanumericKey, Seq(ALPHANUMERIC_REGEX))
    )
  }

  "Error Messages" - {

    Seq(ItemWineMoreInformationMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for alphanumeric" in {

          msgs(alphanumericKey) mustBe messagesForLanguage.errorAlphanumeric
        }

        "have the correct error message for length" in {

          msgs(lengthKey, TEXTAREA_MAX_LENGTH) mustBe messagesForLanguage.errorLength
        }

        "have the correct error message for xss" in {

          msgs(xssKey) mustBe messagesForLanguage.errorInvalidCharacter
        }
      }
    }
  }
}
