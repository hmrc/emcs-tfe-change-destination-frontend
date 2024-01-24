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

package fixtures.messages.sections.journeyType

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object HowMovementTransportedMessages {
  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "How will the goods be transported?"
    val title: String = titleHelper(heading)
    val headingFixed: String = "How the goods will be transported"
    val titleFixed: String = titleHelper(headingFixed)
    val radioOption1: String = "Air transport"
    val radioOption2: String = "Fixed transport installations"
    val radioOption3: String = "Inland waterway transport"
    val radioOption4: String = "Postal consignment"
    val radioOption5: String = "Rail transport"
    val radioOption6: String = "Road transport"
    val radioOption7: String = "Sea transport"
    val radioOption8: String = "Other"
    val info: String = "All movements of energy products between Northern Ireland and the European Union that do not require a guarantee must travel by fixed transport installations."
    val cyaLabel: String = "Type of transport"
    val cyaChangeHidden: String = "how will the goods be transported"
  }

  object English extends ViewMessages with BaseEnglish


}
