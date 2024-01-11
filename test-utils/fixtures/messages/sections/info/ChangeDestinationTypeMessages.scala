/*
 * Copyright 2024 HM Revenue & Customs
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

package fixtures.messages.sections.info

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ChangeDestinationTypeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Current destination type"
    val title: String = titleHelper(heading)

    val summaryListKey: String = "Destination type"
    val legend: String = "Do you want to change the destination type for this movement?"

    val noOption: String = "No, I want to keep the destination type the same"
  }

  object English extends ViewMessages with BaseEnglish
}
