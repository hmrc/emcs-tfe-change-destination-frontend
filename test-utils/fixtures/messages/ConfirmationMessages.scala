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
    val heading = "Change of destination submitted"
    val title: String = titleHelper(heading)

    val movementInformationHeader = "Movement information"
    val arc = "Administrative Reference Code (ARC)"
    val dateOfSubmission = "Date of submission"
    val printText = "Print this screen to make a record of your submission."
    val whatHappensNextHeader = "What happens next"
    val p1ConsigneeSame = "The movement will be updated to show you have successfully submitted a change of destination. Messages will be sent to your inbox showing details of the change of destination and the updated movement. This may not be happen straight away."
    val p1ConsigneeChanged = "If the submission is successful you’ll get a new 21 digit administrative reference code (ARC) sent to your messages inbox, along with details of the updated movement. This can take up to 15 minutes."
    val insetP = "You should advise the transporter to make a note on any accompanying or commercial documentation of the:"
    val insetBullet1 = "date and time you advised them of the change of destination"
    val insetBullet2 = "new place of destination and new consignee details"
    val p2 = "Once your goods have been delivered, you will receive a Report of Receipt from the consignee to let you know if the movement was satisfactory, or if there were any problems."
    val p3 = "Links to cancel, change the movement or explain a delay can be found in the movement overview."
    val explainDelayHeader = "If you need to explain a delay"
    val explainDelayP = "You may submit an explanation of a delay while the goods are in transit. A link to do this can be found in the movement overview."
    val submissionUnsuccessfulHeader = "If your submission is unsuccessful"
    val submissionUnsuccessfulP1 = "If there is a problem with the consignee or delivery details you have added, you’ll get an error message sent to your messages inbox."
    val submissionUnsuccessfulP2 = "The message will tell you what needs to be corrected. You must correct and resubmit the movement until you get a new ARC."
    val submissionUnsuccessfulP3 = "If the error cannot be corrected, you must submit a new change of destination in order to send the goods back to the place of dispatch."
    val p4 = "Contact the HMRC excise helpline if you need more help or information about excise duties."
    val returnToAccountLink = "Return to account"
    val feedbackLink = "What did you think of this service? (opens in new tab) (takes 30 seconds)"
  }

  object English extends ViewMessages with BaseEnglish
}
