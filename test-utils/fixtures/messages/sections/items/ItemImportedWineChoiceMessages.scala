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

object ItemImportedWineChoiceMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading: String = "Was the wine imported from an EU country?"
    val title: String = titleHelper(heading)

    val cyaLabel = "Wine imported from EU"
    val cyaChangeHidden = "if wine is imported from EU"

    val errorRequired = "Select yes if the wine was imported from an EU country"
  }

  object English extends ViewMessages with BaseEnglish

}
