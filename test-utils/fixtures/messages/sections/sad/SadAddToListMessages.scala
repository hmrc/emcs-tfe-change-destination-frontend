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

package fixtures.messages.sections.sad

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object SadAddToListMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "You have given information for 1 Single Administrative Document (SAD) number"
    val title = titleHelper(heading)

    val headingMultiple = "You have given information for 2 Single Administrative Document (SAD) numbers"
    val titleMultiple = titleHelper(headingMultiple)

    val question = "Do you need to add another Single Administrative Document (SAD) number for this movement?"
    val yesOption = "Yes"
    val noOption = "No, there are no other Single Administrative Document (SAD) numbers for this movement"
    val errorMessage = "Enter Select yes if you need to add another SAD import number"
    val errorMessageHelper: String => String = s"Error: " + _

    val sad1 = "SAD document number 1"
    val sad2 = "SAD document number 2"
    val removeLink1WithHiddenText    = "Remove SAD document number 1"
    val removeLink2WithHiddenText    = "Remove SAD document number 2"

    val importNumberMessages: BaseMessages
  }

  object English extends ViewMessages with BaseEnglish {
    override val importNumberMessages: BaseMessages = ImportNumberMessages.English
  }
}
