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

package fixtures.messages.sections.destination

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}

object DestinationWarehouseVatMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading: String => String = "Enter an identifier for the " + _
    val title: String => String = destinationType => titleHelper(heading(destinationType), Some(SectionMessages.English.destinationSubHeading))
    val hint: String = "This can be a VAT registration number or any other identifier, such as an excise ID."
    val cyaLabel: String = "Identification number"
    val cyaChangeHidden: String = "identification number"
    val errorRequired: String = "Enter an identifier"
    val errorRequiredSkippable: String = "Enter an identifier or use link to skip this question"
    val errorInvalidCharacters: String = "Identifier must not include < and > and : and ;"
    val errorLength: Int => String = i => s"Identifier must be $i characters or less"
  }

  object English extends ViewMessages with BaseEnglish
}
