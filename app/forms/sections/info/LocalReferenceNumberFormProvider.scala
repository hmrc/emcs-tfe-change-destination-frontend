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

package forms.sections.info

import forms.mappings.Mappings
import play.api.data.Form

import javax.inject.Inject

class LocalReferenceNumberFormProvider @Inject() extends Mappings {

  def apply(isDeferred: Boolean): Form[String] =
    Form(
      "value" -> text(errMsgForKey("required")(isDeferred))
        .verifying(maxLength(22, errMsgForKey("length")(isDeferred)))
    )

  private def errMsgForKey(key: String)(isDeferred: Boolean) = {
    val infix = if (isDeferred) "deferred" else "new"
    s"localReferenceNumber.$infix.error.$key"
  }
}
