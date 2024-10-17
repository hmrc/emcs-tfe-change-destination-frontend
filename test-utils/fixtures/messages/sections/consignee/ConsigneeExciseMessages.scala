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

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}

object ConsigneeExciseMessages {
  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    val heading = "What is the consignee’s excise registration number (ERN)?"
    val title = titleHelper(heading, Some(SectionMessages.English.consigneeSubHeading))
    val hint = "An ERN contains 13 characters, starting with 2 letters that represent the member state of the consignee, such as GBWK123456789. It can be found on your approval letter."

    val temporaryRegisteredConsigneeHeading = "What is the Temporary Registered Consignee’s authorisation reference?"
    val temporaryRegisteredConsigneeTitle: String = titleHelper(temporaryRegisteredConsigneeHeading, Some(SectionMessages.English.consigneeSubHeading))
    val temporaryRegisteredConsigneeHint = "This contains 13 characters, starting with 2 letters that represent the member state of the Temporary Registered Consignee. For example, XI12345678900. This is also known as a Temporary Consignment Authorisation (TCA) number."

    val temporaryCertifiedConsigneeHeading = "What is the Temporary Certified Consignee’s authorisation reference?"
    val temporaryCertifiedConsigneeTitle: String = titleHelper(temporaryCertifiedConsigneeHeading, Some(SectionMessages.English.consigneeSubHeading))
    val temporaryCertifiedConsigneeHint = "This contains 13 characters, starting with 2 letters that represent the member state of the Temporary Certified Consignee. For example, XI12345678900. This is also known as a Temporary Consignment Authorisation (TCA) number."
    val errorSameAsExisting = "Consignee ERN must not be the same as the previous submission of this movement"
  }

  object English extends ViewMessages with BaseEnglish
}
