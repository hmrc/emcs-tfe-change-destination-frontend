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
import models.{Checkboxable, Enumerable}
import play.api.data.Form
import play.api.data.Forms.set

import javax.inject.Inject

class ItemWineOperationsChoiceFormProvider @Inject() extends Mappings {

  def apply[T <: Checkboxable[T]]()(implicit et: Enumerable[T]): Form[Set[T]] = {
    Form(
      "value" -> set(
        enumerable[T](
          requiredKey = "itemWineOperationsChoice.error.required",
          invalidKey = "itemWineOperationsChoice.error.invalid"
        )
      )
        .verifying(nonEmptySet("itemWineOperationsChoice.error.required"))
        .verifying(exclusiveItemInSet("itemWineOperationsChoice.error.exclusive", "0"))
    )
  }

  def apply[T <: Checkboxable[T]](values: Seq[T])(implicit et: Seq[T] => Enumerable[T]): Form[Set[T]] =
    apply()(et(values))
}
