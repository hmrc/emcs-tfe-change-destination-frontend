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
import models.sections.transportUnit.TransportUnitType._

object TransportSealChoiceMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val title: TransportUnitType => String =
      transportUnitType => titleHelper(heading(transportUnitType))

    val heading: TransportUnitType => String = {
      case Container => "Is there a commercial seal on this container?"
      case Vehicle => "Is there a commercial seal on this vehicle?"
      case Trailer => "Is there a commercial seal on this trailer?"
      case Tractor => "Is there a commercial seal on this tractor?"
      case FixedTransport => "Is there a commercial seal on this fixed transport installation?"
    }

    val error: TransportUnitType => String = {
      case Container => "Select yes if there is a commercial seal on this container"
      case Vehicle => "Select yes if there is a commercial seal on this vehicle"
      case Trailer => "Select yes if there is a commercial seal on this trailer"
      case Tractor => "Select yes if there is a commercial seal on this tractor"
      case FixedTransport => "Select yes if there is a commercial seal on this fixed transport installation"
    }

    val hint = "This is a seal that prevents items being removed or added."

    val cyaLabel = "Commercial seal"

    val moreInfoCyaChangeHidden = "commercial seal"
  }

  object English extends ViewMessages with BaseEnglish


}
