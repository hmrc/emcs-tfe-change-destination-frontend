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

object DocumentReferenceMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Enter a reference for this document"
    val title = titleHelper(heading)
    val errorRequired = "Enter a reference for this document"
    val errorLength = "Document reference must be 35 characters or less"
    val errorXss = "Document reference must not contain < and > and : and ;"
    val cyaLabel = "Document reference"
    val cyaChangeHidden = "Document reference"
  }

  object English extends ViewMessages with BaseEnglish
}
