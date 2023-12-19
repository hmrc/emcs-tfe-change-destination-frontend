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

object ItemNetGrossMassMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(goodsType: String) = s"What is the net and gross mass of the $goodsType?"
    def title(goodsType: String): String = titleHelper(heading(goodsType))

    def netMassH2 = "Net mass"
    def netMassHint = "The net mass is the weight of the goods without packaging. Enter the net mass in kilograms."

    def grossMassH2 = "Gross mass"
    def grossMassHint = "The gross mass is the weight of the goods including packaging. Enter the gross mass in kilograms."

    val cyaNetMassLabel = "Net mass"
    val cyaNetMassChangeHidden = "net mass"

    val cyaGrossMassLabel = "Gross mass"
    val cyaGrossMassChangeHidden = "gross mass"

    val cyaSuffix = "kg"
  }

  object English extends ViewMessages with BaseEnglish
}
