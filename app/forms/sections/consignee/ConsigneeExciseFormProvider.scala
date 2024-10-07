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

import config.Constants
import forms.{ALPHANUMERIC_REGEX, EXCISE_NUMBER_REGEX}
import forms.mappings.Mappings
import models.CountryModel
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{TemporaryCertifiedConsignee, TemporaryRegisteredConsignee, UkTaxWarehouse}
import pages.sections.consignee.ConsigneeExcisePage
import pages.sections.info.DestinationTypePage
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject

class ConsigneeExciseFormProvider @Inject() extends Mappings {

  def apply(memberStates: Option[Seq[CountryModel]])(implicit request: DataRequest[_]): Form[String] = {

    val keyPrefix = request.isNorthernIrelandErn -> DestinationTypePage.value match {
      case true -> Some(TemporaryRegisteredConsignee) => "consigneeExcise.temporaryRegisteredConsignee"
      case true -> Some(TemporaryCertifiedConsignee) => "consigneeExcise.temporaryCertifiedConsignee"
      case _ => "consigneeExcise"
    }

    Form(
      "value" -> text(s"$keyPrefix.error.noInput")
        .transform[String](_.toUpperCase.replace(" ", ""), identity)
        .verifying(firstError(
          fixedLength(13, s"$keyPrefix.error.length"),
          regexpUnlessEmpty(ALPHANUMERIC_REGEX, s"$keyPrefix.error.invalidCharacters"),
          regexpUnlessEmpty(EXCISE_NUMBER_REGEX, s"$keyPrefix.error.format"),
          validateErn(memberStates)
        ))
    )
  }

  //noinspection ScalaStyle
  private def validateErn(memberStates: Option[Seq[CountryModel]])(implicit request: DataRequest[_]): Constraint[String] =
    Constraint {
      case ern if DestinationTypePage.value.contains(UkTaxWarehouse.GB) =>
        if (Seq(Constants.GBWK_PREFIX, Constants.XIWK_PREFIX).exists(ern.startsWith)) Valid else Invalid("consigneeExcise.error.mustStartWithGBWKOrXIWK")

      case ern if DestinationTypePage.value.contains(UkTaxWarehouse.NI) =>
        if (ern.startsWith(Constants.XIWK_PREFIX)) Valid else Invalid("consigneeExcise.error.mustStartWithXIWK")

      case ern if DestinationTypePage.isNItoEuMovement =>
        if (ern.startsWith(Constants.NI_PREFIX) || ern.startsWith(Constants.GB_PREFIX)) Invalid("consigneeExcise.error.mustNotStartWithGBOrXI") else {
          memberStates.map(_.map(_.code)) match {
            case Some(codes) if codes.contains(ern.take(2)) => Valid
            case Some(_) => Invalid("consigneeExcise.error.invalidMemberState")
            case _ => Valid
          }
        }

      case _ =>
        Valid
    }


}
