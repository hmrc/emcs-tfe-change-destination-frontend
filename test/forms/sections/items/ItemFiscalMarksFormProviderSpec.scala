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

import forms.behaviours.StringFieldBehaviours
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import play.api.data.FormError

class ItemFiscalMarksFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "itemFiscalMarks.error.required"
  val xssKey = "itemFiscalMarks.error.xss"
  val alphaNumericKey = "itemFiscalMarks.error.character"
  val lengthKey = "itemFiscalMarks.error.length"
  val maxLength = 350

  val form = new ItemFiscalMarksFormProvider()()

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
      FormError(fieldName, xssKey, Seq(XSS_REGEX))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind when alpha numeric data isn't used" in {
      val data = Map("value" -> "..")
      val result = form.bind(data)

      result.errors must contain only FormError("value", alphaNumericKey, Seq(ALPHANUMERIC_REGEX))
    }
  }
}
