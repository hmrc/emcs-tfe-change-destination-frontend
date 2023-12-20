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

package forms.sections.consignee

import forms.mappings.Mappings
import forms.{ALPHANUMERIC_REGEX, BaseTextareaFormProvider, XSS_REGEX}
import models.ExemptOrganisationDetailsModel
import play.api.data.Form
import play.api.data.Forms.mapping

import javax.inject.Inject

class ConsigneeExemptOrganisationFormProvider @Inject() extends BaseTextareaFormProvider[ExemptOrganisationDetailsModel] with Mappings {

  val maxLength = 255

  def apply(): Form[ExemptOrganisationDetailsModel] =
    Form(mapping(
      "memberState" -> text("consigneeExemptOrganisation.memberState.error.required"),
      "certificateSerialNumber" -> text("consigneeExemptOrganisation.certificateSerialNumber.error.required")
        .transform[String](
          _.replace("\n", " ")
            .replace("\r", " ")
            .replaceAll(" +", " ")
            .trim,
          identity
        )
        .verifying(maxLength(maxLength, s"consigneeExemptOrganisation.certificateSerialNumber.error.length"))
        .verifying(regexpUnlessEmpty(ALPHANUMERIC_REGEX, s"consigneeExemptOrganisation.certificateSerialNumber.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, s"consigneeExemptOrganisation.certificateSerialNumber.error.xss"))
    )(ExemptOrganisationDetailsModel.apply)(ExemptOrganisationDetailsModel.unapply))

}
