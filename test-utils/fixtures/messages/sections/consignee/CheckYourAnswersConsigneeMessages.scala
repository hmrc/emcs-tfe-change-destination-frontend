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

package fixtures.messages.sections.consignee

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object CheckYourAnswersConsigneeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Check your answers"
    val title: String = titleHelper(heading)
    val caption: String = "Consignee information"
    val ern: String = "Excise registration number (ERN)"
    val details: String = "Consignee's details"
    val eori: String = "EORI number"
    val vat: String = "Identification number"
    val identificationProvided: String = "Identification provided"
    val exempt: String = "Exempted organisation details"
    val identificationForTemporaryRegisteredConsignee = "Identification number for Temporary Registered Consignee"
    val identificationForTemporaryCertifiedConsignee = "Identification number for Temporary Certified Consignee"

  }

  object English extends ViewMessages with BaseEnglish
}
