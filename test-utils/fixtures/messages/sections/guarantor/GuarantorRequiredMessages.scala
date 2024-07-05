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


object GuarantorRequiredMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val headingIsUkToUk = "Your movement requires a new guarantor"
    val headingIsNiToEu = "Your movement requires a guarantee"
    val titleIsUkToUk = titleHelper(headingIsUkToUk)
    val titleIsNiToEu = titleHelper(headingIsNiToEu)
    val p1IsUkToUk = "This movement is now an export. The original consignee can no longer be the guarantor for this movement. You must give the details of a new guarantor."
    val p1IsNiToEu = "This movement must be covered by financial security in the form of a movement guarantee. The only type of movement that is allowed to take place without a guarantee from Northern Ireland to the EU is the movement of energy products using fixed transport installations."
    val p2 = "It is the consignor’s responsibility to make sure that a valid movement guarantee is in place. Movement guarantees are valid if lodged with HMRC and one of the businesses named on the guarantee has a suitable connection to that movement. For example, they are the consignor or the owner of the goods."
    val inset = "The guarantor is liable for the whole of the movement until the movement has correctly ended. This means the goods reaching their stated destination or, for exports, leaving the UK."
    val findOutMore = "Read more about financial security for movements of excise goods here (opens in new tab)"
    val enterDetails = "Enter guarantor details"

  }

  object English extends ViewMessages with BaseEnglish
}
