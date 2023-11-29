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

package fixtures.messages.sections.guarantor

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object GuarantorArrangerMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val title = titleHelper("Who is providing the guarantee for this movement?")
    val heading = "Who is providing the guarantee for this movement?"
    val consignorRadioOption = "Consignor"
    val consigneeRadioOption = "Consignee"
    val goodsOwnerRadioOption = "Goods owner"
    val transporterRadioOption = "Transporter"

    val cyaLabel: String = "Guarantor arranger"
    val cyaChangeHidden: String = "who is providing the guarantee for this movement?"
  }

  object English extends ViewMessages with BaseEnglish
}
