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

package forms.sections.exportInformation

import forms.mappings.Mappings
import forms.{CUSTOMS_OFFICE_CODE_REGEX, XSS_REGEX}
import play.api.data.Form

import javax.inject.Inject

class ExportCustomsOfficeFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("exportCustomsOffice.error.required")
        .transform[String](_.toUpperCase.replace(" ", ""), identity)
        .verifying(firstError(
          fixedLength(8, "exportCustomsOffice.error.length"),
          regexp(XSS_REGEX, s"exportCustomsOffice.error.invalidCharacter"),
          regexp(CUSTOMS_OFFICE_CODE_REGEX, s"exportCustomsOffice.error.customOfficeRegex")
        ))
    )
}
