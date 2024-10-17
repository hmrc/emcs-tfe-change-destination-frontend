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

package fixtures.messages.sections.firstTransporter

import fixtures.messages.{BaseEnglish, BaseMessages, SectionMessages, i18n}

object FirstTransporterVatMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Is the first transporter VAT registered in the UK?"
    val title = titleHelper(heading, Some(SectionMessages.English.firstTransporterSubHeading))
    val hint = "A UK VAT registration number is 9 numbers, sometimes with ‘GB’ at the start, for example 123456789 or GB123456789."
    val input = "UK VAT registration number"

    val cyaLabel: String = "VAT registration number"
    val vatChoiceCyaLabel: String = "VAT registered in the UK"
    val cyaChangeChoiceHidden: String = "VAT registered in the UK"
    val cyaChangeHidden: String = "first transporters VAT registration number"
  }

  object English extends ViewMessages with BaseEnglish
}
