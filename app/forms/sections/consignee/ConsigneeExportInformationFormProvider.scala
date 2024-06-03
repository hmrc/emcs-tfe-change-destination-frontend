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
import models.sections.consignee.{ConsigneeExportInformation, ConsigneeExportInformationType}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual

import javax.inject.Inject

class ConsigneeExportInformationFormProvider @Inject() extends Mappings {

  private val VAT_NUMBER_MAX_LENGTH = 14
  private val EORI_NUMBER_MAX_LENGTH = 17

  def apply(): Form[ConsigneeExportInformation] = {
    Form(
      mapping(
        "exportType" -> enumerable[ConsigneeExportInformationType]("consigneeExportInformation.consigneeExportType.error.required"),
        "vatNumber" ->
          mandatoryIfEqual(
            fieldName = "exportType",
            value = "yesVatNumber",
            mapping = text("consigneeExportInformation.vatNumber.error.required")
              .verifying(firstError(maxLength(VAT_NUMBER_MAX_LENGTH, s"consigneeExportInformation.vatNumber.error.length")))
          ),
        "eoriNumber" ->
          mandatoryIfEqual(
            fieldName = "exportType",
            value = "yesEoriNumber",
            mapping = text("consigneeExportInformation.eoriNumber.error.required")
            .verifying(firstError(maxLength(EORI_NUMBER_MAX_LENGTH, s"consigneeExportInformation.eoriNumber.error.length")))
          )
      )(ConsigneeExportInformation.apply)(ConsigneeExportInformation.unapply)
    )
  }
}
