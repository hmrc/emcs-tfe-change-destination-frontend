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

package fixtures.messages.sections.documents

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ReferenceAvailableMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "Does the document have a reference?"
    val title = titleHelper(heading)
    val errorRequired = "Select yes if the document has a reference"

    val cyaLabel: String = "Reference Available"

    val cyaChangeHidden: String = "Reference Available"
  }

  object English extends ViewMessages with BaseEnglish
}
