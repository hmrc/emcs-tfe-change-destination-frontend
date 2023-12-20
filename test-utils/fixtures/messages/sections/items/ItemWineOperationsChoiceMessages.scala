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

package fixtures.messages.sections.items

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ItemWineOperationsChoiceMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    val heading = s"Has the wine undergone any operations?"
    val title: String = titleHelper(heading)
    val hint: String = "Select all that apply."

    val checkBoxItem0 = "The product has been enriched"
    val checkBoxItem1 = "The product has been acidified"
    val checkBoxItem2 = "The product has been de-acidified"
    val checkBoxItem3 = "The product has been sweetened"
    val checkBoxItem4 = "The product has been fortified for distillation"
    val checkBoxItem5 = "A product originating in a geographical unit other than that indicated in the description has been added to the product"
    val checkBoxItem6 = "A product obtained from a vine variety other than that indicated in the description has been added to the product"
    val checkBoxItem7= "A product harvested during a year other than that indicated in the description has been added to the product"
    val checkBoxItem8 = "The product has been made using oak chips"
    val checkBoxItem9 = "The product has been made on the basis of experimental use of a new oenological practice"
    val checkBoxItem10 = "The product has been partially dealcoholised"
    val checkBoxItem11 = "Other operations"
    val checkBoxItem12 = "or"
    val checkBoxItem13 = "No, I am not aware that the wine has undergone any operations"

    val cyaLabel = "Wine operations"
    val cyaChangeHidden = "wine operations"

  }

  object English extends ViewMessages with BaseEnglish

}
