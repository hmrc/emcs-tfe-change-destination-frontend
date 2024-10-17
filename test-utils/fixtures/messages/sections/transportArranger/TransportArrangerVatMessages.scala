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

package fixtures.messages.sections.transportArranger

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}

object TransportArrangerVatMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val goodsOwnerHeading = "Is the goods owner VAT registered in the UK?"
    val goodsOwnerTitle = titleHelper(goodsOwnerHeading, Some(SectionMessages.English.transportArrangerSubHeading))
    val otherHeading = "Is the transport arranger VAT registered in the UK?"
    val otherTitle = titleHelper(otherHeading, Some(SectionMessages.English.transportArrangerSubHeading))
    val hint = "A UK VAT registration number is 9 numbers, sometimes with ‘GB’ at the start, for example 123456789 or GB123456789."
    val vatNumberLabel = "UK VAT registration number"

    val errorRequired = "Enter a VAT registration number"
    val errorAlphanumeric = "VAT registration number must only contain letters and numbers"
    val errorLength = "VAT registration number must be 14 characters or less"

    val cyaChoiceLabel: String = "VAT registered in the UK"
    val cyaChoiceChangeHidden: String = "VAT registered in the UK"

    val cyaInputLabel: String = "VAT registration number"
    val cyaInputChangeHidden: String = "VAT registration number"
  }

  object English extends ViewMessages with BaseEnglish


}
