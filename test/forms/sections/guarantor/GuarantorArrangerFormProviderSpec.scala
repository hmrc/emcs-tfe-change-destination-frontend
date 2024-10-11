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

package forms.sections.guarantor

import forms.behaviours.OptionFieldBehaviours
import models.sections.guarantor.GuarantorArranger
import models.sections.info.movementScenario.MovementScenario
import play.api.data.FormError

class GuarantorArrangerFormProviderSpec extends OptionFieldBehaviours {

  val form = new GuarantorArrangerFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "guarantorArranger.error.required"

    behave like optionsField[GuarantorArranger](
      form,
      fieldName,
      validValues = GuarantorArranger.displayValues(MovementScenario.UkTaxWarehouse.GB),
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
