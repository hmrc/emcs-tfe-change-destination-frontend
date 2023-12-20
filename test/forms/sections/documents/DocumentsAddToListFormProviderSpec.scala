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

package forms.sections.documents

import forms.behaviours.OptionFieldBehaviours
import models.sections.documents.DocumentsAddToList
import play.api.data.FormError

class DocumentsAddToListFormProviderSpec extends OptionFieldBehaviours {

  val form = new DocumentsAddToListFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "documentsAddToList.error.required"

    behave like optionsField[DocumentsAddToList](
      form,
      fieldName,
      validValues  = DocumentsAddToList.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
