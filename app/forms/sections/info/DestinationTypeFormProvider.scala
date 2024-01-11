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

package forms.sections.info

import forms.mappings.Mappings
import models.UserType._
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import models.sections.info.movementScenario.MovementScenario
import play.api.data.Form
import utils.Logging

import javax.inject.Inject

class DestinationTypeFormProvider @Inject() extends Mappings with Logging {

  def apply()(implicit request: DataRequest[_]): Form[MovementScenario] =
    Form(
      "value" -> enumerable[MovementScenario](requiredKey = {
        request.userTypeFromErn match {
          case GreatBritainWarehouseKeeper | NorthernIrelandWarehouseKeeper => "destinationType.error.required.movement"
          case GreatBritainRegisteredConsignor | NorthernIrelandRegisteredConsignor => "destinationType.error.required.import"
          case userType =>
            logger.error(s"[title] invalid UserType for COD journey: $userType")
            throw InvalidUserTypeException(s"[DestinationTypeFormProvider][apply] invalid UserType for COD journey: $userType")
        }
      })
    )
}
