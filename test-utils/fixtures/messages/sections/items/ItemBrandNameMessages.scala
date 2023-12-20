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

package fixtures.messages.sections.items

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}
import forms.sections.items.ItemBrandNameFormProvider

object ItemBrandNameMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(goodsType: String) = s"Do you know the brand name of the $goodsType?"
    def title(goodsType: String): String = titleHelper(heading(goodsType))

    val brandNameLabel = "Brand name"
    val cyaLabel = "Brand name"
    val cyaChangeHidden = "if goods are branded"

    val errorRadioRequired = "Select yes if you know the brand name"
    val errorBrandNameRequired = s"Enter a brand name between 1 and ${ItemBrandNameFormProvider.brandNameMaxLength} characters"
    val errorBrandNameInvalid = s"Brand name must be ${ItemBrandNameFormProvider.brandNameMaxLength} characters or less"
    val errorBrandNameLength = "Brand name must not include < and > and : and ;"
  }

  object English extends ViewMessages with BaseEnglish

}
