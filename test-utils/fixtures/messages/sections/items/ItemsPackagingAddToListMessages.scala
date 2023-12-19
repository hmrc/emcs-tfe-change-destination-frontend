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
import models.Index

object ItemsPackagingAddToListMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    def heading(count: Int, itemIdx: Index): String = count match {
      case 1 => s"You have added 1 packaging type for item ${itemIdx.displayIndex}"
      case _ => s"You have added $count packaging types for item ${itemIdx.displayIndex}"
    }

    def title(count: Int, itemIdx: Index): String = titleHelper(heading(count, itemIdx))

    def packageCardTitle(idx: Index) = s"Package type ${idx.displayIndex}"
    def removePackage(idx: Index): String = s"Remove ${packageCardTitle(idx)}"

    def editPackage(idx: Index): String = s"Continue editing ${packageCardTitle(idx)}"

    def h2(itemIdx: Index) = s"Do you need to add another packaging type for item ${itemIdx.displayIndex}?"
    val hint = "You will have the chance to add more items later."
    val no1 = "No, this is the only packaging type for this item"
    val no2 = "No, these are the only packaging types for this item"
    val moreLater = "I will complete the packaging types later"
    val errorRequired = "Select yes if you need to add another packaging type"

    val cyaChangeHidden = "packaging information"
  }

  object English extends ViewMessages with BaseEnglish
}
