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

object ImportNumberMessages {
  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Enter the number of the Single Administrative Document (SAD) used for releasing the goods for free circulation"
    val title: String = titleHelper(heading)
    val hint = "For example, 555 A12345B 14092016. You will have the chance to add more SAD numbers later."
    val subHeading = "Single Administrative Document"
    val errorLength = "The number must be 21 characters or less"
    val errorXss = "Document reference must not include < and > and : and ;"
    val alphanumeric = "Document reference must only contain letters and numbers"
    val errorRequired = "Enter the number of the Single Administrative Document"
    val checkYourAnswersLabel = "Number"
    val changeHidden = "import number"
  }

  object English extends ViewMessages with BaseEnglish


}