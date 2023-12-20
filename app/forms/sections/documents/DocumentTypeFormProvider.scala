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

import forms.mappings.Mappings
import models.sections.documents.DocumentType
import play.api.data.Form
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject

class DocumentTypeFormProvider @Inject() extends Mappings {

  def apply(documentTypes: Seq[DocumentType]): Form[DocumentType] =
    Form(
      "document-type" -> text("documentType.error.required")
        .transform[Option[DocumentType]](codeToDocumentType(documentTypes), documentToCode())
        .verifying(isValidCode())
        .transform[DocumentType](_.getOrElse(throw new IllegalArgumentException("Invalid document type")), Some(_))
    )

  private def codeToDocumentType(documentTypes: Seq[DocumentType]): String => Option[DocumentType] =
    code => documentTypes.find(_.code == code)

  private def documentToCode(): Option[DocumentType] => String =
    document => document.fold("")(_.code)

  private def isValidCode(): Constraint[Option[DocumentType]] =
    Constraint {
      case Some(_) => Valid
      case _ => Invalid("documentType.error.required")
    }

}
