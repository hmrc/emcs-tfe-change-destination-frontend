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

object DraftMovementMessages {
  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    def headingGbTaxWarehouseTo(input: String): String = s"Great Britain tax warehouse to $input"
    def titleGbTaxWarehouseTo(input: String): String = titleHelper(headingGbTaxWarehouseTo(input))
    def headingDispatchPlaceTo(input1: String, input2: String): String = s"$input1 tax warehouse to $input2"
    def titleDispatchPlaceTo(input1: String, input2: String): String = titleHelper(headingDispatchPlaceTo(input1, input2))
    def headingImportFor(input1: String): String = s"Import for $input1"
    def titleImportFor(input1: String): String = titleHelper(headingImportFor(input1))

    val movementSectionHeading: String = "Movement"
    val movementDetails: String = "Movement details"

    val deliverySectionHeading: String = "Delivery"
    val consignor: String = "Consignor"
    val `import`: String = "Import"
    val dispatch: String = "Place of dispatch"
    val consignee: String = "Consignee"
    val destination: String = "Place of destination"
    val export: String = "Export"

    val guarantorSectionHeading: String = "Guarantor"
    val guarantor: String = "Guarantor"

    val transportSectionHeading: String = "Transport"
    val journeyType: String = "Journey type"
    val transportArranger: String = "Transport arranger"
    val firstTransporter: String = "First transporter"
    val units: String = "Transport units"

    val itemsSectionHeading: String = "Items"
    val items: String = "Items"

    val documentsSectionHeading: String = "Documents"
    val sad: String = "Single Administrative Documents"
    val documents: String = "Documents"

    val submitSectionHeading: String = "Submit"
    val reviewAndSubmit: String = "Review and submit"
  }

  object English extends ViewMessages with BaseEnglish


}
