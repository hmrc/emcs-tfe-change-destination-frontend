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

object ItemPackagingProductTypeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "Is this the only product type in the package?"
    val title = titleHelper(heading)

    def p(packageType: String) = s"The packaging type is: $packageType."

    val noMoreThanOne = "No - the packaging contains more than one product type, required to be entered as separate items in this movement"

    val errorRequired = "Select yes if there is only one product type in the package"
    val cyaLabel = "Contains one product type"
    val cyaChangeHidden = "if the packaging contains one or more product types"
  }

  object English extends ViewMessages with BaseEnglish

}
