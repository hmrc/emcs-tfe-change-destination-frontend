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

package fixtures.messages.sections.transportUnit

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}
import models.sections.transportUnit.TransportUnitType

object TransportUnitIdentityMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    val title: TransportUnitType => String = transportUnitType => titleHelper(heading(transportUnitType))
    val heading: TransportUnitType => String = {
      case TransportUnitType.Tractor =>  "What is the vehicle registration number or unique identifier for this tractor?"
      case TransportUnitType.Trailer => "What is the trailer number or unique identifier for this trailer?"
      case TransportUnitType.Vehicle => "What is the vehicle registration number or unique identifier for this vehicle?"
      case TransportUnitType.FixedTransport => "What is the unique identifier for this fixed transport installation?"
      case TransportUnitType.Container => "What is the container number or unique identifier for this container?"
    }
    val errorEmpty: TransportUnitType =>  String = {
      case TransportUnitType.Tractor | TransportUnitType.Vehicle => "Enter the vehicle registration number or unique identifier"
      case TransportUnitType.Trailer => "Enter the trailer number or unique identifier"
      case TransportUnitType.FixedTransport => "Enter the unique identifier"
      case TransportUnitType.Container => "Enter the container number or unique identifier"
    }
    val errorInputTooLong: TransportUnitType => String = {
      case TransportUnitType.Tractor | TransportUnitType.Vehicle => "Vehicle registration number or unique identifier must be 35 characters or less"
      case TransportUnitType.Trailer => "Trailer number or unique identifier must be 35 characters or less"
      case TransportUnitType.FixedTransport => "Unique identifier must be 35 characters or less"
      case TransportUnitType.Container => "Container number or unique identifier must be 35 characters or less"
    }

    val errorInputDisallowedCharacters: TransportUnitType => String = {
      case TransportUnitType.Tractor | TransportUnitType.Vehicle => "Vehicle registration number or unique identifier must only contain letters and numbers"
      case TransportUnitType.Trailer => "Trailer number or unique identifier must only contain letters and numbers"
      case TransportUnitType.FixedTransport => "Unique identifier must only contain letters and numbers"
      case TransportUnitType.Container => "Container number or unique identifier must only contain letters and numbers"
    }

    val cyaLabel: String = "Transport identifier"

    val cyaChangeHidden: String = "transport identifier"
  }

  object English extends ViewMessages with BaseEnglish

}
