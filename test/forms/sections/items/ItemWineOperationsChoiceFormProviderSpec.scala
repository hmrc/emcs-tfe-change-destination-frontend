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

import base.SpecBase
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemBrandNameMessages
import forms.behaviours.CheckboxFieldBehaviours
import models.response.referenceData.WineOperations
import play.api.data.FormError
import play.api.i18n.Messages

class ItemWineOperationsChoiceFormProviderSpec extends SpecBase with CheckboxFieldBehaviours with ItemFixtures {

  val requiredKey = "itemWineOperationsChoice.error.required"
  val invalidKey = "itemWineOperationsChoice.error.invalid"
  val exclusiveKey = "itemWineOperationsChoice.error.exclusive"

  implicit val msgs: Messages = messages(Seq(ItemBrandNameMessages.English.lang))

  val form = new ItemWineOperationsChoiceFormProvider().apply(testWineOperations)

  ".value" - {

    val fieldName = "value"

    behave like checkboxField[WineOperations](
      form,
      fieldName,
      validValues = testWineOperations,
      invalidError = FormError(s"$fieldName[0]", invalidKey)
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )

    "error if both exclusive & non-exclusive options are selected" in {
      form.bind(Map(
        s"$fieldName[0]" -> "0",
        s"$fieldName[1]" -> "1"
      )).errors must contain(FormError(s"$fieldName", exclusiveKey))
    }
  }
}
