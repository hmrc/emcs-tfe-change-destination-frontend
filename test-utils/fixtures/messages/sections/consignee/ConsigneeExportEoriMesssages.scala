/*
 * Copyright 2024 HM Revenue & Customs
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

object ConsigneeExportEoriMesssages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Enter the EORI number of the person representing the consignor at the office of export"
    val title = titleHelper(heading, Some(SectionMessages.English.consigneeSubHeading))
    val hint = "The first 2 letters are the country code, like GB or XI. This is usually followed by 12 or 15 digits, like GB123456123456."

    val errorRequired = "Enter the EORI number"
    val errorLength = "EORI number must be 17 characters or less"
    val errorInvalid = "EORI number starts with a 2 letter country code, followed by up to 15 numbers or mixed numbers and letters"

    val cyaLabel: String = "EORI number"
    val cyaChangeHidden: String = "EORI number"
  }

  object English extends ViewMessages with BaseEnglish
}
