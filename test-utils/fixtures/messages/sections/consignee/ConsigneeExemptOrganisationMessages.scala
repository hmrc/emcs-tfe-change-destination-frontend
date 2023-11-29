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

package fixtures.messages.sections.consignee

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ConsigneeExemptOrganisationMessages {

  trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "Exempt organisation details"
    val title = titleHelper(heading)
    val subheading = "Consignee information"

    val memberStateLabel = "Select the member state of destination"
    val memberStateErrorRequired = "Select the member state of destination"

    val certificateSerialNumberLabel = "Enter the exemption certificate serial number"
    val certificateSerialNumherErrorRequired = "Enter the exemption certificate serial number"
    val certificateSerialNumberErrorLength = "Serial number must be between 1 and 255 characters"
    val certificateSerialNumberErrorCharacter = "Serial number must contain letters or numbers"
    val certificateSerialNumberErrorXss = "Serial number must not contain < and > and: and; :"
  }

  object English extends ViewMessages with BaseEnglish
}
