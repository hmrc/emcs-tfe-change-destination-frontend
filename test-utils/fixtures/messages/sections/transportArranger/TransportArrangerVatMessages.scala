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

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object TransportArrangerVatMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val goodsOwnerHeading = "What is the goods owner’s UK VAT registration number?"
    val goodsOwnerTitle = titleHelper(goodsOwnerHeading)
    val otherHeading = "What is the transport arranger’s UK VAT registration number?"
    val otherTitle = titleHelper(otherHeading)
    val hint = "This is 9 or 12 numbers, sometimes with ‘GB’ at the start, like 123456789 or GB123456789."
    val goodsOwnerNonGbVatLink = "The goods owner is not UK VAT registered"
    val otherNonGbVatLink = "The transport arranger is not UK VAT registered"

    val errorRequired = "Enter a VAT registration number"
    val errorAlphanumeric = "VAT registration number must only contain letters and numbers"
    val errorLength = "VAT registration number must be 14 characters or less"

    val cyaLabel: String = "VAT registration number"
    val cyaChangeHidden: String = "VAT registration number"
  }

  object English extends ViewMessages with BaseEnglish


}
