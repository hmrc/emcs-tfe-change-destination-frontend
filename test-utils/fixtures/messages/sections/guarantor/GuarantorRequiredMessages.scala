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

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}


object GuarantorRequiredMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val headingNotRequired = "Movement guarantee"
    val headingRequiredUkToUk = "Your movement requires a new guarantor"
    val headingRequiredNiToEu = "Your movement requires a guarantee"
    val titleNotRequired = titleHelper(headingNotRequired, Some(SectionMessages.English.guarantorSubHeading))
    val titleRequiredUkToUk = titleHelper(headingRequiredUkToUk, Some(SectionMessages.English.guarantorSubHeading))
    val titleRequiredNiToEu = titleHelper(headingRequiredNiToEu, Some(SectionMessages.English.guarantorSubHeading))
    val p1NotRequired = "Generally, excise goods moving within the UK (between UK warehouses or before export), or from Northern Ireland to the EU, must be covered by financial security in the form of a movement guarantee."
    val p1RequiredUkToUk = "This movement is now an export. The original consignee can no longer be the guarantor for this movement. You must give the details of a new guarantor."
    val p1RequiredNiToEu = "This movement must be covered by financial security in the form of a movement guarantee. The only type of movement that is allowed to take place without a guarantee from Northern Ireland to the EU is the movement of energy products using fixed transport installations."
    val p2Required = "It is the consignor’s responsibility to make sure that a valid movement guarantee is in place. Movement guarantees are valid if lodged with HMRC and one of the businesses named on the guarantee has a suitable connection to that movement. For example, they are the consignor or the owner of the goods."
    val subHeading1 = "Movements that do not require a guarantee"
    val p2NotRequired = "The following movement types do not usually require a guarantee:"
    val bullet1 = "movements of energy products from Northern Ireland to the EU using fixed transport installations"
    val bullet2 = "duty-suspended movements of goods with Excise Product Codes B000, W200 or W300, from UK alcohol production premises to other UK tax or excise warehouses. However, if a change of destination is submitted and the new destination is outside of the UK, a guarantor is required"
    val subHeading2 = "Is a guarantee required for this movement?"
    val inset = "The guarantor is liable for the whole of the movement until the movement has correctly ended. This means the goods reaching their stated destination or, for exports, leaving the UK."
    val findOutMore = "Read more about financial security for movements of excise goods here (opens in new tab)"
    val enterDetails = "Enter guarantor details"
    val cyaLabel = "Guarantor required"
    val cyaChangeHidden = "if a guarantor is required for this movement"
  }

  object English extends ViewMessages with BaseEnglish
}
