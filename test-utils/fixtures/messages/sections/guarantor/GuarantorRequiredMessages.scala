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

package fixtures.messages.sections.guarantor

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object GuarantorRequiredMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Is a guarantor required for this movement?"
    val title = titleHelper(heading)
    val caption = "Guarantor"
    val cyaLabel: String = "Guarantor required"
    val cyaChangeHidden: String = "if a guarantor is required for this movement"
    val errorRequired = "Select yes if a guarantor is required for this movement"
  }

  object English extends ViewMessages with BaseEnglish
}
