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

    val heading = "Your movement requires a new guarantor"
    val title = titleHelper(heading)
    val p1 = "This movement is now an export. The original consignee can no longer be the guarantor for this movement. You must give the details of a new guarantor."
    val p2 = "It is the consignor’s responsibility to make sure that a valid movement guarantee is in place. Movement guarantees are valid if lodged with HMRC and one of the businesses named on the guarantee has a suitable connection to that movement. For example, they are the consignor or the owner of the goods."
    val findOutMore = "Read more about financial security for movements of excise goods here (opens in new tab)"
    val enterDetails = "Enter guarantor details"

  }

  object English extends ViewMessages with BaseEnglish
}
