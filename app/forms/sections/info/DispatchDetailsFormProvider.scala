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

package forms.sections.info

import forms.mappings.Mappings
import models.sections.info.DispatchDetailsModel
import play.api.data.Form
import play.api.data.Forms.mapping

import java.time.LocalDate
import javax.inject.Inject

class DispatchDetailsFormProvider @Inject() extends Mappings {

  // scalastyle:off magic.number
  private val earliestDispatchDate = LocalDate.of(2000,1,1)
  // scalastyle:on magic.number

  def apply(): Form[DispatchDetailsModel] =
    Form(
      mapping(
        "value" -> localDate(
          invalidKey = "dispatchDetails.value.error.invalid",
          allRequiredKey = "dispatchDetails.value.error.required.all",
          twoRequiredKey = "dispatchDetails.value.error.required.two",
          requiredKey = "dispatchDetails.value.error.required"
        )
          .verifying(
            firstError(
              fourDigitYear("dispatchDetails.value.error.yearNotFourDigits"),
              minDate(earliestDispatchDate, "dispatchDetails.value.error.earliestDate")
            )
          ),
        "time" -> localTime(
          invalidKey = "dispatchDetails.time.error.invalid",
          requiredKey = "dispatchDetails.time.error.required"
        )
      )(DispatchDetailsModel.apply)(DispatchDetailsModel.unapply))

}