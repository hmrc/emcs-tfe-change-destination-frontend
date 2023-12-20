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

package forms.sections.items

import base.SpecBase
import fixtures.messages.sections.items.ItemProducerSizeMessages.English
import forms.behaviours.{BigIntFieldBehaviours, StringFieldBehaviours}
import play.api.data.FormError
import play.api.i18n.Messages

class ItemProducerSizeFormProviderSpec extends SpecBase with BigIntFieldBehaviours with StringFieldBehaviours {

  val requiredKey = "itemProducerSize.error.required"
  val outOfRangeKey = "itemProducerSize.error.outOfRange"
  val wholeNumberKey = "itemProducerSize.error.wholeNumber"
  val nonNumericKey = "itemProducerSize.error.nonNumeric"

  val min: BigInt = BigInt(1)
  val max: BigInt = BigInt("999999999999999")

  val form = new ItemProducerSizeFormProvider()()

  ".value" - {

    val fieldName = "value"

    "for the minimum valid value" - {

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        min.toString
      )
    }

    "for the maximum valid value" - {

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        max.toString
      )
    }

    behave like bigIntField(
      form,
      fieldName,
      nonNumericError = FormError(fieldName, nonNumericKey),
      wholeNumberError = FormError(fieldName, wholeNumberKey)
    )

    behave like bigIntFieldWithRange(
      form,
      fieldName,
      minimum = min,
      maximum = max,
      expectedError = FormError(fieldName, outOfRangeKey, Seq(min, max))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error Messages" - {

    implicit val msgs: Messages = messages(Seq(English.lang))

    s"when output for language code '${English.lang.code}'" - {

      "have the correct error message for required key" in {

        msgs(requiredKey) mustBe
          English.errorRequired
      }

      "have the correct error message for out of range key" in {

        msgs(outOfRangeKey) mustBe
          English.errorOutOfRange
      }

      "have the correct error message for whole number key" in {

        msgs(wholeNumberKey) mustBe
          English.errorWholeNumber
      }

      "have the correct error message for non numeric key" in {

        msgs(wholeNumberKey) mustBe
          English.errorNonNumeric
      }
    }
  }
}
