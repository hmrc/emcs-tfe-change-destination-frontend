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

import forms._
import forms.mappings.Mappings
import models.sections.transportUnit.TransportSealTypeModel
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

import javax.inject.Inject

class TransportSealTypeFormProvider @Inject() extends BaseTextareaFormProvider[TransportSealTypeModel] with Mappings {

  def apply(): Form[TransportSealTypeModel] = Form(
    mapping(
      "value" -> text("transportSealType.sealType.error.required")
        .verifying(
          firstError(
            maxLength(35, "transportSealType.sealType.error.length"),
            regexpUnlessEmpty(XSS_REGEX, s"transportSealType.sealType.error.invalid")
          )
        ),
      "moreInfo" -> optional(text()
        .verifying(
          firstError(
            maxLength(TEXTAREA_MAX_LENGTH, s"transportSealType.moreInfo.error.length"),
            regexp(s"$ALPHANUMERIC_REGEX", s"transportSealType.moreInfo.error.invalid"),
            regexp(XSS_REGEX, s"transportSealType.moreInfo.error.invalidCharacter")
          )
        ))
    )(TransportSealTypeModel.apply)(TransportSealTypeModel.unapply)
  )
}
