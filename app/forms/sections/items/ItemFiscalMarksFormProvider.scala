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

package forms.sections.items

import forms.mappings.Mappings
import forms.{ALPHANUMERIC_REGEX, BaseTextareaFormProvider, TEXTAREA_MAX_LENGTH, XSS_REGEX}
import play.api.data.Form

import javax.inject.Inject

class ItemFiscalMarksFormProvider @Inject() extends BaseTextareaFormProvider[String] with Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("itemFiscalMarks.error.required")
        .transform[String](
          _.replace("\n", " ")
            .replace("\r", " ")
            .replaceAll(" +", " ")
            .trim,
          identity
        )
        .verifying(maxLength(TEXTAREA_MAX_LENGTH, "itemFiscalMarks.error.length"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "itemFiscalMarks.error.xss"))
        .verifying(regexpUnlessEmpty(ALPHANUMERIC_REGEX, "itemFiscalMarks.error.character"))
    )
}
