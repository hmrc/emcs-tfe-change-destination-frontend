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

import forms.behaviours.IntFieldBehaviours
import play.api.data.FormError

import scala.util.Random

class JourneyTimeDaysFormProviderSpec extends IntFieldBehaviours {

  val fieldName = "value"
  val minimum = 1

  ".value" - {
    val maximum = 45
    val form = new JourneyTimeDaysFormProvider()(maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      Random.between(minimum, maximum).toString
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError  = FormError(fieldName, "journeyTimeDays.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "journeyTimeDays.error.wholeNumber")
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum       = minimum,
      maximum       = maximum,
      expectedError = FormError(fieldName, "journeyTimeDays.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "journeyTimeDays.error.required")
    )
  }

  "uses maxJourneyTimeDays parameter" - {
    val maximum = 20
    val form = new JourneyTimeDaysFormProvider()(maximum)

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum = minimum,
      maximum = maximum,
      expectedError = FormError(fieldName, "journeyTimeDays.error.outOfRange", Seq(minimum, maximum))
    )
  }

}
