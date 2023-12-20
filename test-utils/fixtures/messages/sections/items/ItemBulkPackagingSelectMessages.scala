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

object ItemBulkPackagingSelectMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    def heading(goodsType: String) = s"Which bulk packaging type are you using for the $goodsType?"

    def title(goodsType: String): String = titleHelper(heading(goodsType))

    val radioOption1 = "Bulk, gas (at 1031 mbar and 15°C) (VG)"
    val radioOption2 = "Bulk, liquefied gas (abn.temp/press) (VQ)"
    val radioOption3 = "Bulk, liquid (VL)"
    val radioOption4 = "Bulk, solid, fine (powders) (VY)"
    val radioOption5 = "Bulk, solid, granular (grains) (VR)"
    val radioOption6 = "Bulk, solid, large (nodules) (VO)"
    val radioOption7 = "Unpacked or unpackaged (NE)"

    val cyaLabel = "Bulk package type"
    val cyaChangeHidden = "bulk package type"

  }

  object English extends ViewMessages with BaseEnglish

}
