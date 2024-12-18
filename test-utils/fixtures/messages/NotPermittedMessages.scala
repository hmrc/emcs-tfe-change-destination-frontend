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

package fixtures.messages

object NotPermittedMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: String
    val title: String
    val p1: String
    val p2: String
    val p3: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title: String = titleHelper("There is a problem")
    override val heading = "There is a problem"
    override val p1 = "You do not have permission to view this page."
    override val p2 = "Go to your ‘Account home’ page to access all of your movements and messages."
    override val p3 = "You can also visit the EMCS general enquiries page for more help."
  }

}
