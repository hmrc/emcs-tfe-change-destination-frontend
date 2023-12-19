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

package forms.sections.sad

import forms.ONLY_ALPHANUMERIC_REGEX

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.Form

class ImportNumberFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("importNumber.error.required")
        .verifying(regexp(ONLY_ALPHANUMERIC_REGEX, "importNumber.error.alphanumeric"))
        .verifying(maxLength(21, "importNumber.error.length"))
    )
}
