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

object DocumentsCertificatesMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "Are there any document certificates associated with this movement?"
    val title = titleHelper(heading)
    val errorRequired = "Select yes if there are documents associated with this movement"
    val hint = "For example, a delivery note, invoice or electronic administrative document (eAD). You will have the chance to add more later."
    val cyaLabel: String = "Documents Certificates"

    val cyaChangeHidden: String = "Documents Certificates"
  }

  object English extends ViewMessages with BaseEnglish
}
