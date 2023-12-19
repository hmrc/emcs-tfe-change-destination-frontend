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

object DispatchDetailsMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "Dispatch details"
    val title = titleHelper(heading)

    val deferredMovementFalseParagraph = "You can create a draft electronic administrative document (eAD) at any time, but it cannot be submitted more than 7 days before the date of dispatch."
    val deferredMovementTrueParagraph = "This date must match the date you entered on the fallback document."

    val registeredConsignorParagraph = "Enter the date when this movement begins. This will be when the goods get dispatched from where they entered the UK."

    val dateHint = "For example, 30 06 2023."
    val timeHint = "For example, 9am or 14:00. Enter 12pm for midday."

    val cyaDispatchDateLabel: String = "Date of dispatch"
    val cyaChangeDispatchDateHidden: String = "date of dispatch"
    val cyaDispatchTimeLabel: String = "Time of dispatch"
    val cyaChangeDispatchTimeHidden: String = "time of dispatch"
  }

  object English extends ViewMessages with BaseEnglish


}
