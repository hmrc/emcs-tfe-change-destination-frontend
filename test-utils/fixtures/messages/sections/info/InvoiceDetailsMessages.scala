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

object InvoiceDetailsMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading = "Invoice details"
    val title = titleHelper(heading)
    val text = "If you do not have an invoice, you can use a delivery note or transport document, or skip this question and return to it later. The reference should relate to these specific goods in your records."
    val referenceLabel = "Invoice reference"
    val referenceErrorRequired = "Enter a reference, or skip this question for now"
    val referenceErrorLength = "Reference must be 35 characters or less"
    val dateLabel = "Invoice date of issue"
    val dateHint = (date: String) => s"For example, $date."
    val dateErrorRequiredAll = "Enter the date of issue, or skip this question for now"
    val dateErrorInvalid = "Date of issue must be a real date"
    val dateErrorRequiredTwo = (fieldOne: String, fieldTwo: String) => s"Date of issue must include a $fieldOne and $fieldTwo"
    val dateErrorRequired = (field: String) => s"Date of issue must include a $field"
    val yearIsNotFourDigitsLong = "Enter a valid year using four digits (for example, 2023)"
    val cyaInvoiceReferenceLabel: String = "Invoice reference"
    val cyaInvoiceDateLabel: String = "Invoice date of issue"
    val cyaChangeInvoiceReferenceHidden: String = "Invoice reference"
    val cyaChangeInvoiceDateHidden: String = "Invoice date of issue"
  }

  object English extends ViewMessages with BaseEnglish


}
