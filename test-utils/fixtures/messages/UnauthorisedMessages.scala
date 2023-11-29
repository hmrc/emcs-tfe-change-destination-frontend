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

import fixtures.messages.BaseEnglish.titleHelper


object UnauthorisedMessages {

  sealed trait ViewMessages {
    _: i18n =>
    val heading = "There is a problem"
    val title = titleHelper("There is a problem")
    val p1 = "You do not have permission to view this page."
    val p2 = "You should:"
    val bullet1 = "check you have signed in with the right details"
    val bullet2 = "check you are using the right excise reference number (ERN)"
    val p3 = "If you are using the right sign in details and ERN, check the web address is correct."
  }

  object English extends ViewMessages with BaseEnglish
}
