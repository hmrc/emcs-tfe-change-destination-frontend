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
import fixtures.messages.sections.items.ItemsPackagingAddToListMessages
import forms.behaviours.OptionFieldBehaviours
import models.sections.items.ItemsPackagingAddToList
import play.api.data.FormError

class ItemsPackagingAddToListFormProviderSpec extends SpecBase with OptionFieldBehaviours {

  val form = new ItemsPackagingAddToListFormProvider()()

  val fieldName = "value"
  val requiredKey = "itemsPackagingAddToList.error.required"

  ".value" - {

    behave like optionsField[ItemsPackagingAddToList](
      form,
      fieldName,
      validValues  = ItemsPackagingAddToList.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error messages" - {

    Seq(ItemsPackagingAddToListMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in the language of '${messagesForLanguage.lang.code}'" - {

        val msgs = messages(Seq(messagesForLanguage.lang))

        "have the correct wording for the required key message" in {

          msgs(requiredKey) mustBe messagesForLanguage.errorRequired
        }
      }
    }
  }
}
