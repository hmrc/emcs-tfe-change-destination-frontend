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
import fixtures.messages.sections.items.ItemGeographicalIndicationMessages
import forms.behaviours.StringFieldBehaviours
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import play.api.data.FormError
import play.api.i18n.Messages

class ItemGeographicalIndicationFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "itemGeographicalIndication.error.required"
  val lengthKey = "itemGeographicalIndication.error.length"
  val xssKey = "itemGeographicalIndication.error.xss"
  val alphanumericKey = "itemGeographicalIndication.error.alphanumeric"
  val maxLength = 350

  val form = new ItemGeographicalIndicationFormProvider()()

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

    behave like fieldWithAtLeastOneAlphanumeric(
      form,
      fieldName,
      error = FormError(fieldName, alphanumericKey, Seq(ALPHANUMERIC_REGEX))
    )
  }

  "Error Messages" - {

    Seq(ItemGeographicalIndicationMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for required" in {

          msgs("itemGeographicalIndication.error.required") mustBe
            messagesForLanguage.errorRequired
        }

        "have the correct error message for length" in {

          msgs("itemGeographicalIndication.error.length") mustBe
            messagesForLanguage.errorLength
        }

        "have the correct error message for xss" in {

          msgs("itemGeographicalIndication.error.xss") mustBe
            messagesForLanguage.errorXss
        }

        "have the correct error message for alphanumeric" in {

          msgs("itemGeographicalIndication.error.alphanumeric") mustBe
            messagesForLanguage.errorAlphanumeric
        }
      }
    }
  }
}
