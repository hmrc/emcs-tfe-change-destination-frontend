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
import models.sections.items.ItemDegreesPlatoModel
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text => playText}

import javax.inject.Inject

class ItemDegreesPlatoFormProvider @Inject() extends Mappings {

  import ItemDegreesPlatoFormProvider._

  def apply(): Form[ItemDegreesPlatoModel] =
    Form(
      mapping(
        hasDegreesPlatoField -> boolean(radioRequired),
        degreesPlatoField -> optional(
          playText()
            .verifying(isDecimal(nonNumericErrorKey))
            .transform[BigDecimal](BigDecimal(_), _.toString())
            .verifying(decimalRange(minValue, maxValue, rangeErrorKey))
            .verifying(maxDecimalPlaces(maxDecimalPlacesValue, maxDecimalPlacesErrorKey))
        )
      )(ItemDegreesPlatoModel.apply)(ItemDegreesPlatoModel.unapply)
        .transform[ItemDegreesPlatoModel](
          model => if(!model.hasDegreesPlato) model.copy(degreesPlato = None) else model, identity
        )
    )
}

object ItemDegreesPlatoFormProvider {

  val maxValue = BigDecimal(999.99)
  val minValue = BigDecimal(0.01)
  val maxDecimalPlacesValue = 2

  val hasDegreesPlatoField: String = "hasDegreesPlato"
  val degreesPlatoField: String = "degreesPlato"

  val radioRequired = "itemDegreesPlato.radio.error.required"
  val requiredErrorKey = "itemDegreesPlato.amount.error.required"
  val nonNumericErrorKey = "itemDegreesPlato.amount.error.nonNumeric"
  val rangeErrorKey = "itemDegreesPlato.amount.error.range"
  val maxDecimalPlacesErrorKey = "itemDegreesPlato.amount.error.decimalPlaces"
}
