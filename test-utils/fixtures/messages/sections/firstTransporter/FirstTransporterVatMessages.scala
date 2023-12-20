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

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object FirstTransporterVatMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "What is the first transporter's VAT registration number?"
    val title = titleHelper(heading)
    val hint = "This is 9 or 12 numbers, sometimes with ‘GB’ at the start, like 123456789 or GB123456789."
    val nonGbVatLink = "The first transporter is not VAT registered"

    val errorRequired = "Enter a VAT registration number"
    val errorLength = "VAT registration number must be 12 characters or less"
    val errorAlphanumeric = "VAT registration number number must only contain letters and numbers"

    val cyaLabel: String = "VAT registration number"
    val cyaChangeHidden: String = "first transporters VAT registration number"
  }

  object English extends ViewMessages with BaseEnglish
}
