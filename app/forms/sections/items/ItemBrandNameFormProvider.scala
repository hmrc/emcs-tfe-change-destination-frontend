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

import forms.XSS_REGEX

import javax.inject.Inject
import forms.mappings.Mappings
import models.sections.items.ItemBrandNameModel
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text => playText}

class ItemBrandNameFormProvider @Inject() extends Mappings {

  import ItemBrandNameFormProvider._

  def apply(): Form[ItemBrandNameModel] =
    Form(
      mapping(
        hasBrandNameField -> boolean(radioRequired),
        brandNameField -> optional(
          playText()
            .verifying(maxLength(brandNameMaxLength, brandNameLength))
            .verifying(regexp(XSS_REGEX, brandNameInvalid))
        )
      )(ItemBrandNameModel.apply)(ItemBrandNameModel.unapply)
        .transform[ItemBrandNameModel](
          model => if(!model.hasBrandName) model.copy(brandName = None) else model, identity
        )
    )
}

object ItemBrandNameFormProvider {
  val hasBrandNameField: String = "hasBrandName"
  val brandNameField: String = "brandName"

  val radioRequired = "itemBrandName.error.radio.required"
  val brandNameRequired = "itemBrandName.error.brandName.required"
  val brandNameInvalid = "itemBrandName.error.brandName.invalid"
  val brandNameLength = "itemBrandName.error.brandName.length"

  val brandNameMaxLength: Int = 350
}
