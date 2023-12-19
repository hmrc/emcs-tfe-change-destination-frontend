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

import forms.mappings.Mappings
import models.GoodsType
import play.api.data.Form
import play.api.i18n.Messages

import javax.inject.Inject

class ItemPackagingQuantityFormProvider @Inject() extends Mappings {

  import ItemPackagingQuantityFormProvider._

  def apply(goodsType: GoodsType)(implicit messages: Messages): Form[String] =
    Form(
      fieldName -> bigInt(
        requiredKey = requiredErrorKey(goodsType),
        wholeNumberKey = decimalPlacesErrorKey,
        nonNumericKey = invalidCharactersErrorKey
      ).transform[String](_.toString(), BigInt(_))
        .verifying(maxLength(maxDigits, maxLengthErrorKey))
    )
}

object ItemPackagingQuantityFormProvider {

  val maxDigits = 15
  val fieldName = "value"
  val maxDecimalPlacesValue = 0

  def requiredErrorKey(goodsType: GoodsType)(implicit messages: Messages): String =
    messages("itemPackagingQuantity.error.required", goodsType.toSingularOutput())

  val invalidCharactersErrorKey = "itemPackagingQuantity.error.invalidCharacters"
  val decimalPlacesErrorKey = "itemPackagingQuantity.error.decimalPlaces"
  val maxLengthErrorKey = "itemPackagingQuantity.error.tooLong"

}
