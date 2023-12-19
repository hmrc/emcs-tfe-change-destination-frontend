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

object ItemGeographicalIndicationMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(goodsType: String) = s"What is the geographical indication for the $goodsType?"
    def title(goodsType: String) = titleHelper(heading(goodsType))

    val hintPDO = "Enter the name of the Protected Designation of Origin (PDO) for the product. Include a register number if you have one."
    val hintPGI = "Enter the name of the Protected Geographical Indication (PGI) for the product. Include a register number if you have one."
    val hintGI = "Enter the name of the Geographical Indication (GI) for the product. Include a register number if you have one."

    val cyaLabel = "Geographical indication information"
    val cyaChangeHidden = "geographical indication information"

    val errorRequired = "Enter details of the geographical indication"
    val errorLength = "Geographical indication must be 350 characters or less"
    val errorXss = "Geographical indication must not contain < and > and : and ;"
    val errorAlphanumeric = "Geographical indication must include letters and numbers"
  }

  object English extends ViewMessages with BaseEnglish
}
