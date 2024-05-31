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

object ConsigneeExportVatMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Enter the VAT registration number of the person representing the consignor at the office of export"
    val title = titleHelper(heading)
    val hint = "This is 9 numbers, sometimes with ‘GB’ at the start, for example 123456789 or GB123456789."
    val errorRequired = "Enter the VAT registration number"

    val cyaLabel: String = "Identification number"
    val cyaChangeHidden: String = "VAT registration number of the person representing the consignor at the office of export"
  }

  object English extends ViewMessages with BaseEnglish
}
