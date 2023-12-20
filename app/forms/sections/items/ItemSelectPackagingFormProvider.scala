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
import models.GoodsType
import models.response.referenceData.ItemPackaging
import play.api.data.Form
import play.api.i18n.Messages

import javax.inject.Inject

class ItemSelectPackagingFormProvider @Inject() extends Mappings {

  def apply(goodsType: GoodsType, acceptablePackagingTypes: Seq[ItemPackaging])(implicit messages: Messages): Form[ItemPackaging] =
    Form(
      "packaging" -> text("itemSelectPackaging.error.required", args = Seq(goodsType.toSingularOutput()))
        .verifying(valueInList(acceptablePackagingTypes.map(_.packagingType), "itemSelectPackaging.error.required", goodsType.toSingularOutput()))
        .transform[ItemPackaging](findPackagingTypeFromCode(_)(acceptablePackagingTypes).get, _.packagingType)

    )

  private def findPackagingTypeFromCode(code: String)(packagingTypes: Seq[ItemPackaging]): Option[ItemPackaging] = {
    packagingTypes.find(_.code == code)
  }
}
