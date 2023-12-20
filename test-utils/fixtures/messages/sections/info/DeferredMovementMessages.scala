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

object DeferredMovementMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val title = titleHelper("Is this a deferred movement?")
    val heading = "Is this a deferred movement?"
    val caption = "Movement information"
    val hint = "A deferred movement is one that was originally made using fallback procedures due to EMCS being unavailable."
    val summary = "Help with fallback procedures"
    val paragraph1 = "Fallback is used when goods need to be dispatched and EMCS is unavailable. When this happens the Fallback Accompanying Document (FAD) is completed and printed to travel with the goods."
    val paragraph2 = "Any information entered on EMCS for a deferred movement must match the information on the FAD."
    val errorRequired = "Select yes if this is a deferred movement"
    val cyaLabel: String = "Deferred movement"
    val cyaChangeHidden: String = "deferred movement"
  }

  object English extends ViewMessages with BaseEnglish
}
