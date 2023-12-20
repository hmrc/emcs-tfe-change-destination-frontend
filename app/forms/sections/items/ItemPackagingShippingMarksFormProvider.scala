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

import forms.{ALPHANUMERIC_REGEX, BaseTextareaFormProvider, XSS_REGEX}

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.Form

class ItemPackagingShippingMarksFormProvider @Inject() extends BaseTextareaFormProvider[String] with Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("itemPackagingShippingMarks.error.required")
        .transform[String](
          _.replace("\n", " ")
            .replace("\r", " ")
            .replaceAll(" +", " ")
            .trim,
          identity
        )
        .verifying(maxLength(999, "itemPackagingShippingMarks.error.length"))
        .verifying(regexpUnlessEmpty(ALPHANUMERIC_REGEX, "itemPackagingShippingMarks.error.character"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, "itemPackagingShippingMarks.error.invalid"))
    )
}
