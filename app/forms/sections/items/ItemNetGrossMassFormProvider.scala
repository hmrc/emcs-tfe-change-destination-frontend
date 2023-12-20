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
import models.sections.items.ItemNetGrossMassModel
import play.api.data.Forms.mapping
import play.api.data.{Form, FormBinding, Mapping}
import play.api.mvc.Request

import javax.inject.Inject


class ItemNetGrossMassFormProvider @Inject() extends Mappings {

  private[items] val maxDecimalPlaces = 6
  private[items] val max = 9999999999999999L
  private[items] val minExclusive = 0L
  private[items] val grossMassField = "grossMass"
  private[items] val netMassField = "netMass"

  private def bigDecimalMapping(field: String): (String, Mapping[BigDecimal]) =
    field -> bigDecimal(s"itemNetGrossMass.$field.error.required", s"itemNetGrossMass.$field.error.invalid")
      .verifying(s"itemNetGrossMass.$field.error.high", _ <= BigDecimal(max))
      .verifying(s"itemNetGrossMass.$field.error.low", _ > BigDecimal(minExclusive))
      .verifying(maxDecimalPlaces(maxDecimalPlaces, s"itemNetGrossMass.$field.error.decimals"))

  val form: Form[ItemNetGrossMassModel] =
    Form(
      mapping(
        bigDecimalMapping(netMassField),
        bigDecimalMapping(grossMassField)
      )(ItemNetGrossMassModel.apply)(ItemNetGrossMassModel.unapply)
    )

  def enhancedBindFromRequest()(implicit request: Request[_], formBinding: FormBinding): Form[ItemNetGrossMassModel] = {
    form.bindFromRequest().fold(identity, {
      case netGrossModel if netGrossModel.grossMass < netGrossModel.netMass  =>
        form.fill(netGrossModel).withError(grossMassField, "itemNetGrossMass.grossMass.error.lessThanNetMass")
      case netGrossMass => form.fill(netGrossMass)
    })
  }
}
