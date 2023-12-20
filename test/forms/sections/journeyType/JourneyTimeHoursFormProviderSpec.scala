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

package forms.sections.journeyType

import forms.behaviours.BigIntFieldBehaviours
import play.api.data.FormError

import scala.util.Random

class JourneyTimeHoursFormProviderSpec extends BigIntFieldBehaviours {

  val form = new JourneyTimeHoursFormProvider()()

  ".value" - {

    val fieldName = "value"

    val minimum = 1
    val maximum = 23

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      Random.between(minimum, maximum).toString
    )

    behave like bigIntField(
      form,
      fieldName,
      nonNumericError  = FormError(fieldName, "journeyTimeHours.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "journeyTimeHours.error.wholeNumber")
    )

    behave like bigIntFieldWithRange(
      form,
      fieldName,
      minimum       = minimum,
      maximum       = maximum,
      expectedError = FormError(fieldName, "journeyTimeHours.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "journeyTimeHours.error.required")
    )
  }
}
