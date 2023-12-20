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
import forms.sections.items.ItemMaturationPeriodAgeFormProvider

object ItemMaturationPeriodAgeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(goodsType: String) = s"Do you know the maturation age of the $goodsType?"
    def title(goodsType: String): String = titleHelper(heading(goodsType))

    val hint = "The maturation age refers to a production process that happens before bottling. It can usually be found in the description, presentation and labelling of the goods."
    val maturationPeriodAgeLabel = "Enter the maturation age"

    val cyaLabel = "Maturation age"
    val cyaChangeHidden = "the maturation age"

    def errorRadioRequired(goodsType: String) = s"Select 'yes' if you know the maturation age of the $goodsType"
    val errorMaturationPeriodAgeRequired = s"Field cannot be blank"
    val errorMaturationPeriodAgeInvalid = s"Enter the maturation age in ${ItemMaturationPeriodAgeFormProvider.maturationPeriodAgeMaxLength} characters or less"
    val errorMaturationPeriodAgeLength = "Maturation age must not contain < and > and : and ;"
  }

  object English extends ViewMessages with BaseEnglish

}
