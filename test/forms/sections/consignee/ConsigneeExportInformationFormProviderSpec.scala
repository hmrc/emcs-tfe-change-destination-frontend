/*
 * Copyright 2024 HM Revenue & Customs
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

package forms.sections.consignee

import base.SpecBase
import forms.behaviours.CheckboxFieldBehaviours
import models.sections.consignee.ConsigneeExportInformation
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation}
import play.api.data.FormError

class ConsigneeExportInformationFormProviderSpec extends SpecBase with CheckboxFieldBehaviours {

  "ConsigneeExportInformationFormProvider" - {

    val requiredKey = "consigneeExportInformation.error.required"
    val invalidKey = "consigneeExportInformation.error.invalid"
    val exclusiveKey = "consigneeExportInformation.error.exclusive"

    val form = new ConsigneeExportInformationFormProvider().apply()

    ".value" - {

      val fieldName = "value"

      behave like checkboxField[ConsigneeExportInformation](
        form,
        fieldName,
        validValues = ConsigneeExportInformation.values,
        invalidError = FormError(s"$fieldName[0]", invalidKey)
      )

      behave like mandatoryCheckboxField(
        form,
        fieldName,
        requiredKey
      )

      "error if both exclusive & non-exclusive options are selected" in {
        form.bind(Map(
          s"$fieldName[0]" -> EoriNumber.toString,    // non-exclusive option
          s"$fieldName[1]" -> NoInformation.toString  // exclusive option
        )).errors must contain(FormError(s"$fieldName", exclusiveKey))
      }
    }
  }
}
