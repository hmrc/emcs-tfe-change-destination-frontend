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

object ItemFiscalMarksMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = s"Give information about the fiscal marks on the tobacco"
    val title: String = titleHelper(heading)

    val cyaLabel = "Fiscal marks information"
    val cyaChangeHidden = "fiscal marks information"
  }

  object English extends ViewMessages with BaseEnglish

}
