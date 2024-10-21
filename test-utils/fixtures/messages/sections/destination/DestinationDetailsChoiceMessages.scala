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

package fixtures.messages.sections.destination

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}

object DestinationDetailsChoiceMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "Do you want to give the address and business name of the registered consignee?"
    val title = titleHelper(heading, Some(SectionMessages.English.destinationSubHeading))
    val hint = "This information is optional."
    val cyaLabel: String => String = destinationType => s"Give details of $destinationType"
    val cyaChangeHidden: String => String = destinationType => s"Give details of $destinationType"
  }

  object English extends ViewMessages with BaseEnglish
}
