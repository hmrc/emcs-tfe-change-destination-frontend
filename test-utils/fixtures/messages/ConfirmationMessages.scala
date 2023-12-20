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

object ConfirmationMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Draft movement submitted"
    val title: String = titleHelper(heading)
    val movementInformationHeader = "Movement information"
    val localReferenceNumber = "Local Reference Number"
    val dateOfSubmission = "Date of submission"
    val printText = "Print this screen to make a record of your submission."
    val whatHappensNextHeader = "What happens next"
    val p1 = "If the submission is successful you’ll receive a 21 digit administrative reference code (ARC). This can take up to 15 minutes."
    val p2 = "If unsuccessful, you’ll get an error which you must correct and resubmit until you get an ARC. Contact the HMRC excise helpline (opens in new tab) if you need more help or information about excise duties."
    val p3 = "Goods must not be dispatched before an ARC has been allocated."
    val p4 = "The ARC must be visible on either a printed eAD or any other commercial document, and must come with the goods."
    val returnToAccountLink = "Return to account"
    val feedbackLink = "What did you think of this service? (opens in new tab) (takes 30 seconds)"

  }

  object English extends ViewMessages with BaseEnglish
}
