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

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class ItemQuantityFormProvider @Inject() extends Mappings {

  import ItemQuantityFormProvider._

  def apply(): Form[BigDecimal] =
    Form(
      fieldName -> text(requiredErrorKey)
        .verifying(isDecimal(nonNumericErrorKey))
        .transform[BigDecimal](BigDecimal(_), _.toString())
        .verifying(decimalRange(minValue, maxValue, rangeErrorKey))
        .verifying(maxDecimalPlaces(maxDecimalPlacesValue, maxDecimalPlacesErrorKey))
    )
}

object ItemQuantityFormProvider {

  val maxDigits = 15
  val minValue = BigDecimal(0.001)
  val maxValue = BigDecimal("9" * maxDigits)

  val maxDecimalPlacesValue = 3

  val fieldName = "value"

  val requiredErrorKey = "itemQuantity.error.required"
  val nonNumericErrorKey = "itemQuantity.error.nonNumeric"
  val rangeErrorKey = "itemQuantity.error.range"
  val maxDecimalPlacesErrorKey = "itemQuantity.error.decimalPlaces"

}
