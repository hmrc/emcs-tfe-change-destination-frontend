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

object DocumentTypeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "What type of document do you want to add?"
    val title = titleHelper(heading)
    val errorRequired = "Select the document you want to add"
    val cyaLabel = "Document type"
    val cyaChangeHidden = "document type"

    val defaultSelectOption = "Choose document type"
    val firstSelectionOption = "First selection (code)"
  }

  object English extends ViewMessages with BaseEnglish
}
