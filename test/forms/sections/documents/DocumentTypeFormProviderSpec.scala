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

import base.SpecBase
import fixtures.DocumentTypeFixtures
import fixtures.messages.sections.documents.DocumentTypeMessages.English
import forms.behaviours.OptionFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class DocumentTypeFormProviderSpec extends SpecBase with OptionFieldBehaviours with DocumentTypeFixtures {

  val documentTypes = Seq(documentTypeModel, documentTypeOtherModel)
  val form = new DocumentTypeFormProvider()(documentTypes)

  ".document-type" - {

    val fieldName = "document-type"
    val requiredKey = "documentType.error.required"

    "succeed when value is one of the DocumentType" in {

      val data = Map(fieldName -> documentTypeModel.code)
      val result = form.bind(data)

      result.errors mustBe Seq.empty
      result.value.value mustBe documentTypeModel
    }

    "error when value is not given" in {

      val data = Map(fieldName -> "")
      val result = form.bind(data)

      result.errors must contain only FormError(fieldName, requiredKey)
    }

    "error when submitted value is not one of the DocumentType" in {

      val data = Map(fieldName -> "invalidCode")
      val result = form.bind(data)

      result.errors must contain only FormError(fieldName, requiredKey)
    }

    "Error Messages" - {

      implicit val msgs: Messages = messages(Seq(English.lang))

      "have the correct error message for required" in {

        msgs(requiredKey) mustBe
          English.errorRequired
      }
    }
  }
}
