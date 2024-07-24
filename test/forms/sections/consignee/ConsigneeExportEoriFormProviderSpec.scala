/*
 * Copyright 2024 HM Revenue & Customs
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

package forms.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExportEoriMesssages
import forms.EORI_NUMBER_REGEX
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class ConsigneeExportEoriFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "consigneeExportEori.error.required"
  val lengthKey = "consigneeExportEori.error.length"
  val invalidKey = "consigneeExportEori.error.invalidFormat"

  val maxLength = 17

  val form = new ConsigneeExportEoriFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength,
      FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithEori(
      form = form,
      fieldName = fieldName,
      formatError = FormError(fieldName, invalidKey, Seq(EORI_NUMBER_REGEX))
    )

    "must trim any spaces before validating and accepting" in {
      form.bind(Map(fieldName -> "  GB123  45 6789  ")).value mustBe Some("GB123456789")
    }
  }

  "Error Messages" - {

    Seq(ConsigneeExportEoriMesssages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct required error message" in {

          msgs(requiredKey) mustBe
            messagesForLanguage.errorRequired
        }

        "have the correct length error message" in {

          msgs(lengthKey) mustBe
            messagesForLanguage.errorLength
        }

        "have the correct regex error message" in {

          msgs(invalidKey) mustBe
            messagesForLanguage.errorInvalid
        }

        "must transform the inputted EORI removing any spaces" in {
          val result = form.bind(Map("value" -> "GB 123 456")).get
          result mustBe "GB123456"
        }

        "must transform the inputted EORI into uppercase" in {
          val result = form.bind(Map("value" -> "gb123456")).get
          result mustBe "GB123456"
        }
      }
    }
  }

}
