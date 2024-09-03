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

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object DestinationWarehouseVatMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val title = titleHelper("What is the VAT number for the registered consignee? (optional)")
    val heading: String = "What is the VAT number for the registered consignee? (optional)"
    val cyaLabel: String = "Identification number"
    val cyaChangeHidden: String = "Identification number"
    val errorRequired: String = "Enter a VAT number or use link to skip this question"
    val errorInvalidCharacters: String = "VAT registration number must not contain special characters"
    val errorLength: String = "VAT registration number must be 14 characters or less"
  }

  object English extends ViewMessages with BaseEnglish
}
