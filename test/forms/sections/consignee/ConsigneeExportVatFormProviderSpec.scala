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

package forms.sections.consignee

import forms.ONLY_ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ConsigneeExportVatFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "consigneeExportVat.error.required"
  val lengthKey = "consigneeExportVat.error.length"
  val invalidKey = "consigneeExportVat.error.invalid"
  val maxLength = 16

  val form = new ConsigneeExportVatFormProvider()()

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

    "when the value contains invalid characters" - {
      "must error with the expected msg key" in {
        val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+"))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, invalidKey, Seq(ONLY_ALPHANUMERIC_REGEX)))
      }
    }
  }
}
