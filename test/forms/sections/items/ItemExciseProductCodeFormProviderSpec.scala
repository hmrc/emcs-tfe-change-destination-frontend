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

import fixtures.{BaseFixtures, ItemFixtures}
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class ItemExciseProductCodeFormProviderSpec extends StringFieldBehaviours with BaseFixtures with ItemFixtures {

  val requiredKey = "itemExciseProductCode.error.required"
  val maxLength = 100

  val form = new ItemExciseProductCodeFormProvider().apply(Seq(beerExciseProductCode))

  ".value" - {

    val fieldName = "excise-product-code"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "B000"
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind when the value provided is not in the excise product code list" in {
      val result = form.bind(Map(fieldName -> "B001")).apply(fieldName)
      result.errors mustEqual Seq(FormError(fieldName, requiredKey, Seq()))
    }
  }
}
