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
import models.Index
import models.sections.transportUnit.TransportUnitType
import models.sections.transportUnit.TransportUnitType._

object TransportSealTypeMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    val title = (transportUnitType: TransportUnitType) => titleHelper(heading(transportUnitType), Some(SectionMessages.English.transportUnitSubHeading))

    val heading = (transportUnitType: TransportUnitType) => transportUnitType match {
      case Container => "What type of commercial seal is on this container?"
      case Vehicle => "What type of commercial seal is on this vehicle?"
      case Trailer => "What type of commercial seal is on this trailer?"
      case Tractor => "What type of commercial seal is on this tractor?"
      case FixedTransport => "What type of commercial seal is on this fixed transport installation?"
    }

    val sealType = "Type of commercial seal"
    val sealTypeErrorRequired = "Enter the type of commercial seal"
    val sealTypeErrorLength = "Commercial seal type must be 35 characters or less"
    val sealTypeErrorInvalid = "Commercial seal type must only contain letters and numbers"
    val sealTypeCYA = "Commercial seal type"
    def sealTypeCyaChangeHidden(idx: Index) = s"commercial seal type for transport unit ${idx.displayIndex}"
    val moreInfo = "Give more information (optional)"
    val moreInfoHint = "Describe the seal so that it can be identified if the packaging has been tampered with. Include a reference number if there is one."
    val moreInfoErrorCharacters = "Information must only contain letters and numbers"
    val moreInfoErrorLength = "Information must be 350 characters or less"
    val moreInfoCYA = "Commercial seal information"
    def moreInfoCyaChangeHidden(idx: Index) = s"commercial seal information for transport unit ${idx.displayIndex}"
    val moreInfoCYAAddInfo = "Enter more information about the commercial seal (optional)"
  }

  object English extends ViewMessages with BaseEnglish



}
