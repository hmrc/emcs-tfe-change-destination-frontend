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

import forms.EORI_NUMBER_REGEX
import forms.mappings.Mappings
import play.api.data.Form

import javax.inject.Inject

class ConsigneeExportEoriFormProvider @Inject() extends Mappings {

  private val EORI_NUMBER_MAX_LENGTH = 17

  def apply(): Form[String] =
    Form(
      "value" -> text("consigneeExportEori.error.required")
        .verifying(
          firstError(
            maxLength(EORI_NUMBER_MAX_LENGTH, "consigneeExportEori.error.length"),
            regexp(EORI_NUMBER_REGEX, "consigneeExportEori.error.invalidFormat")
          )
        )
    )
}
