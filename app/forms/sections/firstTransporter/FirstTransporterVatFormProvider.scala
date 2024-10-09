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

package forms.sections.firstTransporter

import forms.ONLY_ALPHANUMERIC_REGEX
import forms.mappings.Mappings
import models.VatNumberModel
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.voa.play.form.ConditionalMappings

import javax.inject.Inject

class FirstTransporterVatFormProvider @Inject() extends Mappings {

  import FirstTransporterVatFormProvider._

  def apply(): Form[VatNumberModel] =
    Form(
      mapping(
        hasVatField -> boolean(radioRequired),
        vatNumberField -> ConditionalMappings.mandatoryIfTrue(hasVatField,
          text(vatRequired)
            .transform[String](_.replace("-", "").replace(" ", ""), identity)
            .verifying(
              firstError(
                maxLength(vatNumberMaxLength, vatNumberLength),
                regexp(ONLY_ALPHANUMERIC_REGEX, vatNumberInvalid)
              )
            )

        )
      )(VatNumberModel.apply)(VatNumberModel.unapply)
    )
}

object FirstTransporterVatFormProvider {
  val hasVatField: String = "hasVatNumber"
  val vatNumberField: String = "vatNumber"

  val radioRequired = "firstTransporterVat.error.radio.required"
  val vatRequired = "firstTransporterVat.error.input.required"
  val vatNumberInvalid = "firstTransporterVat.error.alphanumeric"
  val vatNumberLength = "firstTransporterVat.error.length"

  val vatNumberMaxLength: Int = 14
}
