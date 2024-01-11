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

package fixtures.messages.sections.info

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ChangeTypeMessages {
  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "What do you want to change about this movement?"
    val title = titleHelper(heading)
    val consigneeRadio = "I want to change the consignee"
    val destinationRadio = "I want to keep the consignee the same and change the place of destination"
    val exportOfficeRadio = "I want to keep the consignee the same and change the office of export"
    val returnToSenderRadio = "I want to return the goods to the consignor’s place of dispatch"
    val errorRequired: String = "Select what you want to change about the movement"
  }

  object English extends ViewMessages with BaseEnglish


}
