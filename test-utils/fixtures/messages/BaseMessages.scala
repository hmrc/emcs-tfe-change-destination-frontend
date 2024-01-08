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


trait BaseMessages { _: i18n =>
  def titleHelper(heading: String) = s"$heading - Excise Movement and Control System - GOV.UK"
  val opensInNewTab: String = "(opens in new tab)"
  val hiddenSectionContent: String = "This section is"
  val movementInformationSection: String = s"$hiddenSectionContent Movement information"
  val consigneeInformationSection = s"$hiddenSectionContent Consignee information"
  val transportArrangerSection: String = s"$hiddenSectionContent Transport arranger"
  val dispatchSection: String = s"$hiddenSectionContent Place of dispatch information"
  val exportInformationSection: String = s"$hiddenSectionContent Export information"
  val importInformationSection: String = s"$hiddenSectionContent Import information"
  val itemInformationSection: String = s"$hiddenSectionContent Item information"
  val destinationSection: String = s"$hiddenSectionContent Place of destination information"
  val sadSection: String = s"$hiddenSectionContent Single Administrative Document"
  val documentsSection: String = s"$hiddenSectionContent Documents"
  val itemSection: String = s"$hiddenSectionContent Item information"
  val draftMovementSection: String = s"$hiddenSectionContent Draft movement"
  val transportUnitsSection: String = s"$hiddenSectionContent Transport units"
  val journeyTypeSection: String = s"$hiddenSectionContent Journey type"
  val guarantorSection: String = s"$hiddenSectionContent Guarantor"

  def lrnSubheading(lrn: String): String = s"Change destination for $lrn"
  val continue = "Continue"
  val confirmAnswers = "Confirm answers"
  val notProvided = "Not provided"
  val saveAndContinue = "Save and continue"
  val returnToDraft = "Return to draft"
  val skipThisQuestion = "Skip this question for now"
  val skipQuestion = "Skip this question"
  val saveAndReturnToMovement = "Save and return to movement"
  val day: String = "Day"
  val month: String = "Month"
  val year: String = "Year"
  val yes: String = "Yes"
  val no: String = "No"
  val none: String = "None"
  val change: String = "Change"
  val remove: String = "Remove"
  val continueEditing: String = "Continue editing"
  val sectionNotComplete: String => String = section => s"$section section not complete"
  val incomplete: String = "Incomplete"
}

trait BaseEnglish extends BaseMessages with EN
object BaseEnglish extends BaseEnglish
