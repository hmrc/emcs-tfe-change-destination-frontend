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

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}
import models.sections.transportUnit.TransportUnitType

object TransportUnitTypeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "What will the goods be transported in?"
    val title: String = titleHelper(heading, Some(SectionMessages.English.transportUnitSubHeading))
    val containerRadioOption = "Container"
    val fixedTransportRadioOption = "Fixed transport installations"
    val tractorRadioOption = "Tractor"
    val trailerRadioOption = "Trailer"
    val vehicleRadioOption = "Vehicle"
    val addToListLabel = "Transport type"
    val cyaLabel = "Type of transport"
    val addToListChangeHidden: String = "transport type"
    val addToListValue: TransportUnitType => String = {
      case TransportUnitType.Tractor        => tractorRadioOption
      case TransportUnitType.Trailer        => trailerRadioOption
      case TransportUnitType.Vehicle        => vehicleRadioOption
      case TransportUnitType.FixedTransport => fixedTransportRadioOption
      case TransportUnitType.Container      => containerRadioOption
    }
  }

  object English extends ViewMessages with BaseEnglish


}
