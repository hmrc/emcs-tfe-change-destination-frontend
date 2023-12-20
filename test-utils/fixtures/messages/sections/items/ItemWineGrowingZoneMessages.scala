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

object ItemWineGrowingZoneMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "Which wine-growing zone has the wine come from?"
    val title = titleHelper(heading)

    val findOutMoreLink = "find more information about each wine-growing zone (opens in new tab)"
    val p1 = s"Select a wine-growing zone code. You can $findOutMoreLink in Annex IX to Regulation (EC) No 479/2008."

    val a = "A"
    val b = "B"
    val ci = "CI"
    val cii = "CII"
    val ciii_a = "CIII(a)"
    val ciii_b = "CIII(b)"

    val errorRequired = "Select a wine-growing zone"

    val cyaLabel = "Wine growing zone"
    val cyaChangeHidden = "wine growing zone"
  }

  object English extends ViewMessages with BaseEnglish

}
