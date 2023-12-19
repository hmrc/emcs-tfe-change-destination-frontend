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

package fixtures.messages.sections.items

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}
import forms.sections.items.ItemAlcoholStrengthFormProvider._

object ItemAlcoholStrengthMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(goodsType: String) = s"What is the alcohol strength by volume (ABV) for the $goodsType?"
    def title(goodsType: String): String = titleHelper(heading(goodsType))

    val hint = "Enter the alcoholic strength as a percentage (by volume at 20°C)."
    val suffix = "%"

    val cyaLabel = "Alcohol by volume (ABV)"
    val cyaSuffix = "%"
    val cyaChangeHidden = "alcohol strength"

    val errorRadioRequired = "Enter the alcoholic strength"
    val errorBrandNameRequired = "Amount must only include numbers"
    val errorBrandNameInvalid = s"Enter an amount with up to $maxDecimalPlacesValue decimal places between $minValue and $maxValue"
    val errorBrandNameLength = s"Amount can have up to $maxDecimalPlacesValue decimal places"
  }

  object English extends ViewMessages with BaseEnglish

}
