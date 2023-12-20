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
import models.Index

object DocumentsAddToListMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading: Int => String = {
      case 1 => "You have given information for 1 document"
      case idx => s"You have given information for $idx documents"
    }

    val title: Int => String = idx => titleHelper(heading(idx))

    def documentCardTitle(idx: Index) = s"Document ${idx.displayIndex}"
    def removeDocument(idx: Index): String = s"Remove ${documentCardTitle(idx)}"

    def editDocument(idx: Index): String = s"Continue editing ${documentCardTitle(idx)}"

    val h2 = "Do you need to add another document?"
    val no1 = "No, this is the only document"
    val no2 = "No, these are the only documents in this movement"
    val moreLater = "I will add more documents later"
    val errorRequired = "Select yes if you need to add another document"
  }

  object English extends ViewMessages with BaseEnglish
}
