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

package forms.sections.transportUnit

import forms.XSS_REGEX
import forms.mappings.Mappings
import models.sections.transportUnit.TransportUnitType
import play.api.data.Form

import javax.inject.Inject

class TransportUnitIdentityFormProvider @Inject() extends Mappings {

  def apply(transportUnitType: TransportUnitType): Form[String] =
    Form(
      "value" -> text(s"transportUnitIdentity.error.required.${transportUnitType.toString}")
        .verifying(maxLength(35, s"transportUnitIdentity.error.length.${transportUnitType.toString}"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, s"transportUnitIdentity.error.invalidCharacters.${transportUnitType.toString}"))
    )

}
