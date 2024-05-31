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

object ConsigneeExportInformationMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val title = titleHelper("Do you know the VAT or EORI number of the person representing the consignor at the office of export?")
    val heading = "Do you know the VAT or EORI number of the person representing the consignor at the office of export?"
    val yesVatNumberRadioOption = "Yes - VAT number"
    val yesEoriNumberRadioOption = "Yes - EORI number"
    val noRadioOption = "No"
    val cyaEoriLabel: String = "EORI Number"
    val cyaVatLabel: String = "VAT Number"
    val cyaChangeHidden: String = "VAT or EORI number of the person representing the consignor"
  }

  object English extends ViewMessages with BaseEnglish
}
