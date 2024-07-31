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

package forms.sections.destination

import forms.mappings.Mappings
import forms.{GB_00_EXCISE_NUMBER_REGEX, XI_00_EXCISE_NUMBER_REGEX, XI_OR_GB_00_EXCISE_NUMBER_REGEX, XSS_REGEX}
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import play.api.data.Form
import play.api.data.validation.Constraint

import javax.inject.Inject

class DestinationWarehouseExciseFormProvider @Inject() extends Mappings {

  private[forms] def inputIsValidForDestinationType(movementScenario: MovementScenario): Constraint[String] =
    Constraint {
      case answer if movementScenario == MovementScenario.UkTaxWarehouse.GB =>
        regexp(GB_00_EXCISE_NUMBER_REGEX, "destinationWarehouseExcise.error.invalidGB00").apply(answer)
      case answer if movementScenario == MovementScenario.UkTaxWarehouse.NI =>
        regexp(XI_00_EXCISE_NUMBER_REGEX, "destinationWarehouseExcise.error.invalidXI00").apply(answer)
      case answer =>
        regexpToNotMatch(XI_OR_GB_00_EXCISE_NUMBER_REGEX, "destinationWarehouseExcise.error.invalidXIOrGB").apply(answer)
    }

  def apply(movementScenario: MovementScenario)(implicit dataRequest: DataRequest[_]): Form[String] =
    Form(
      "value" -> text("destinationWarehouseExcise.error.required")
        .transform[String](_.toUpperCase.replace(" ", ""), identity)
        .verifying(regexpUnlessEmpty(XSS_REGEX, "destinationWarehouseExcise.error.invalidCharacter"))
        .verifying(maxLength(16, "destinationWarehouseExcise.error.length"))
        .verifying(inputIsValidForDestinationType(movementScenario))
    )
}
