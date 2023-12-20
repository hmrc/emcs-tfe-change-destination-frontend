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

object ItemBulkPackagingChoiceMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    val heading: String => String = goodsType => s"Is the $goodsType being moved in bulk?"
    val title: String => String = goodsType => titleHelper(heading(goodsType))
    val hint = "Moving goods in bulk means that the goods are loaded directly into a vessel in large quantity, " +
      "and not packaged. For example, transporting liquids in a tanker."
    val cyaLabel: String = "Bulk package"
    def cyaChangeHidden(goodsType: String): String = s"if the $goodsType is being moved in bulk"
  }

  object English extends ViewMessages with BaseEnglish
}
