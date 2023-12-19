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

object ItemGeographicalIndicationChoiceMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(goodsType: String) = s"Can you confirm if the $goodsType has a geographical indication (GI)?"
    def title(goodsType: String): String = titleHelper(heading(goodsType))

    val hint = "A geographical indication (GI) relates to where goods are made."

    val pdoRadioOption = "Yes - the product has a Protected Designation of Origin (PDO)"
    val pdoRadioHint = "For example, Champagne has a Protected Designation of Origin (PDO)."

    val pgiRadioOption = "Yes - the product has a Protected Geographical Indication (PGI)"
    val pgiRadioHint = "For example, Mallorca has a protected wine name with Protected Geographical Indication (PGI)."

    val giRadioOption = "Yes - the product has a Geographical Indication (GI)"
    val giRadioHint = "For example Irish Whiskey has a Geographical Indication (GI) protected spirit drink name."

    val divider = "or"

    val noRadioOption = "No - I cannot confirm that the product has a geographical indication (GI)"

    val cyaLabel = "Geographical indication"
    val cyaValuePDO = "Protected Designation of Origin (PDO)"
    val cyaValuePGI = "Protected Geographical Indication (PGI)"
    val cyaValueGI = "Geographical Indication (GI)"
    val cyaChangeHidden = "designation of origin"
  }

  object English extends ViewMessages with BaseEnglish

}
