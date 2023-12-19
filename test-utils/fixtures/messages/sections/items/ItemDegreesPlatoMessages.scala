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
import forms.sections.items.ItemDegreesPlatoFormProvider

object ItemDegreesPlatoMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(goodsType: String) = s"Is the destination a country that uses Degrees Plato to tax the $goodsType?"
    def title(goodsType: String): String = titleHelper(heading(goodsType))

    val degreesPlatoLabel = "Enter the density in Degrees Plato"
    val detailsSummaryHeading = "Which countries use Degrees Plato"
    val degreesPlatoSuffix = "°P"
    val cyaSuffix = "&deg;P"

    val cyaLabel = "Degrees plato"
    val cyaRadioLabel = "Degrees Plato used by destination country"
    val cyaAmountLabel = "Density in degrees plato"
    val cyaChangeHidden = "density in degrees plato"

    val errorRadioRequired = "Select yes if the destination country uses Degrees Plato"
    val errorDegreesPlatoRequired = "Enter the density in degrees plato"
    val errorDegreesPlatoNonNumeric = "Amount must only include numbers"
    val errorDegreesPlatoOutsideRange = s"Enter an amount with up to ${ItemDegreesPlatoFormProvider.maxDecimalPlacesValue} decimal places between ${ItemDegreesPlatoFormProvider.minValue} and ${ItemDegreesPlatoFormProvider.maxValue}"
    val errorDegreesPlatoTooManyDP = s"Amount can have up to ${ItemDegreesPlatoFormProvider.maxDecimalPlacesValue} decimal places"

  }

  object English extends ViewMessages with BaseEnglish

}
