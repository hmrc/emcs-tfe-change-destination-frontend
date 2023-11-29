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


object NotOnPrivateBetaMessages {

  sealed trait ViewMessages {
    _: i18n =>
    val heading = "There is a problem"
    val title = titleHelper("There is a problem")
    val p1 = "You do not have permission to view this page."
    val alreadySignedUpH2 = "If you have signed up for the Excise Movement and Control System (EMCS) private beta research"
    val alreadySignedUpP1 = "If you typed the web address, check it is correct. If you pasted the web address, check you copied the entire address."
    val alreadySignedUpP2 = "If you think you have signed in with the wrong details, sign out and check the details you have are correct."
    val notSignedUpH2 = "If you have not signed up for the EMCS private beta research"
    val notSignedUpP1 = "You can choose to take part in the EMCS private beta research if you are not already involved."
    val notSignedUpP2 = "To take part you must currently submit EMCS receipts using the HMRC platform."
  }

  object English extends ViewMessages with BaseEnglish
}
