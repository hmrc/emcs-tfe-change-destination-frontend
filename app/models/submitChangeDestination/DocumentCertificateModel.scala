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

package models.submitChangeDestination

import models.Index
import models.requests.DataRequest
import models.sections.documents.DocumentType
import pages.sections.documents._
import play.api.libs.json.{Json, OFormat}
import queries.DocumentsCount
import utils.ModelConstructorHelpers

case class DocumentCertificateModel(
                                     documentType: Option[String],
                                     documentReference: Option[String],
                                     documentDescription: Option[String],
                                     referenceOfDocument: Option[String]
                                   )

object DocumentCertificateModel extends ModelConstructorHelpers {

  def apply(implicit request: DataRequest[_]): Option[Seq[DocumentCertificateModel]] = {
    val thereAnyDocumentCertificates = mandatoryPage(DocumentsCertificatesPage)

    if (!thereAnyDocumentCertificates) None else {
      request.userAnswers.get(DocumentsCount) match {
        case Some(0) | None => None
        case Some(value) =>
          Some((0 until value)
            .map(Index(_))
            .map {
              idx =>
                val documentType = mandatoryPage(DocumentTypePage(idx)).code

                if (documentType == DocumentType.OtherCode) {
                  DocumentCertificateModel(
                    documentType = Some(mandatoryPage(DocumentTypePage(idx)).code),
                    documentReference = request.userAnswers.get(DocumentReferencePage(idx)),
                    documentDescription = request.userAnswers.get(DocumentDescriptionPage(idx)),
                    referenceOfDocument = None
                  )
                } else {
                  DocumentCertificateModel(
                    documentType = Some(documentType),
                    documentReference = None,
                    documentDescription = None,
                    referenceOfDocument = None
                  )
                }
            })
      }
    }
  }

  implicit val fmt: OFormat[DocumentCertificateModel] = Json.format
}
