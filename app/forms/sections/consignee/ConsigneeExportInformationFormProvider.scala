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

import forms.mappings.Mappings
import models.sections.consignee.ConsigneeExportInformation
import models.sections.consignee.ConsigneeExportInformation.NoInformation
import play.api.data.Form
import play.api.data.Forms.set

import javax.inject.Inject

class ConsigneeExportInformationFormProvider @Inject() extends Mappings {

  def apply(): Form[Set[ConsigneeExportInformation]] =
    Form(
      "value" -> set(
        enumerable[ConsigneeExportInformation](
          requiredKey = "consigneeExportInformation.error.required",
          invalidKey = "consigneeExportInformation.error.invalid"
        )
      )
        .verifying(
          firstError(
            nonEmptySet("consigneeExportInformation.error.required"),
            exclusiveItemInSet("consigneeExportInformation.error.exclusive", NoInformation.toString)
          )
        )
    )
}
