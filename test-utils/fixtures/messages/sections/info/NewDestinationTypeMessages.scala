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

object NewDestinationTypeMessages {
  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    val h1 = "Destination Type"
    val headingMovement = "What is the new destination type for this movement?"
    val titleMovement = titleHelper(h1)
    val headingImport = "What is the new destination type for this import?"
    val titleImport = titleHelper(h1)
    val h2 = "If you’re moving vaping products"
    val p1 = "You must submit a separate movement for vaping products when the movement is going from:"
    val bullet1 = "Northern Ireland to the EU"
    val bullet2 = "the EU to Northern Ireland"
    val bullet3 = "Northern Ireland to a destination outside the EU but the export declaration is lodged in an EU member state"
    val p2 = "This applies even if you transport them with other excise goods."
    val hint = "Not applicable to movements with vaping products"
    val exportWithCustomsDeclarationLodgedInTheUk = "Export with customs declaration lodged in the United Kingdom"
    val gbTaxWarehouse = "Tax warehouse in Great Britain"
    val niTaxWarehouse = "Tax warehouse in Northern Ireland"
    val directDelivery = "Direct delivery"
    val euTaxWarehouse = "Tax warehouse in the European Union"
    val exemptedOrganisation = "Exempted organisation"
    val exportWithCustomsDeclarationLodgedInTheEu = "Export with customs declaration lodged in the European Union"
    val registeredConsignee = "Registered consignee"
    val temporaryRegisteredConsignee = "Temporary registered consignee"
    val unknownDestination = "Unknown destination"

    val cyaLabel: String = "Destination type"
    val cyaChangeHidden: String = "destination type"
  }

  object English extends ViewMessages with BaseEnglish


}
