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

object DispatchPlaceMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val title = titleHelper("Where is the place of dispatch for this movement?")
    val heading = "Where is the place of dispatch for this movement?"
    val greatBritainRadioOption = "Great Britain"
    val northernIrelandRadioOption = "Northern Ireland"
  }

  object English extends ViewMessages with BaseEnglish


}
