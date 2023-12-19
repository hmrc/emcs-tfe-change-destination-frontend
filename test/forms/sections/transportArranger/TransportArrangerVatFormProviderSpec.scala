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

package forms.sections.transportArranger

import forms.ONLY_ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class TransportArrangerVatFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "transportArrangerVat.error.required"
  val lengthKey = "transportArrangerVat.error.length"
  val alphanumericKey = "transportArrangerVat.error.alphanumeric"
  val maxLength = 14

  val form = new TransportArrangerVatFormProvider()()

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

    "only allow alphanumerics" in {
      val boundForm = form.bind(Map("value" -> "ABCD@/"))
      boundForm.errors mustBe Seq(FormError(fieldName, alphanumericKey, Seq(ONLY_ALPHANUMERIC_REGEX)))
    }

    "must allow - and spaces but trim them out" in {
      val boundForm = form.bind(Map("value" -> "GB123 456-178"))
      boundForm.value mustBe Some("GB123456178")
    }
  }
}
