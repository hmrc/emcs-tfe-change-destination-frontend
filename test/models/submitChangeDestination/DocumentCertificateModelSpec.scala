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

import base.SpecBase
import models.requests.DataRequest
import models.sections.documents.DocumentType
import pages.sections.documents._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class DocumentCertificateModelSpec extends SpecBase {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "apply" - {
    "should return Some(Seq(_))" - {
      "when Other" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(DocumentsCertificatesPage, true)
            .set(DocumentTypePage(testIndex1), DocumentType("0", "0 type desc"))
            .set(DocumentReferencePage(testIndex1), "0 reference")
            .set(DocumentDescriptionPage(testIndex1), "0 description")
        )

        DocumentCertificateModel.apply mustBe Some(Seq(
          DocumentCertificateModel(
            documentType = Some("0"),
            documentReference = Some("0 reference"),
            documentDescription = Some("0 description"),
            referenceOfDocument = None
          )
        ))
      }
      "when not Other" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(DocumentsCertificatesPage, true)
            .set(DocumentTypePage(testIndex1), DocumentType("1", "1 type desc"))
            .set(DocumentReferencePage(testIndex1), "1 reference")
            .set(DocumentDescriptionPage(testIndex1), "1 description")
            .set(DocumentTypePage(testIndex2), DocumentType("2", "2 type desc"))
            .set(DocumentReferencePage(testIndex2), "2 reference")
            .set(DocumentDescriptionPage(testIndex2), "12 description")
        )

        DocumentCertificateModel.apply mustBe Some(Seq(
          DocumentCertificateModel(
            documentType = Some("1"),
            documentReference = None,
            documentDescription = None,
            referenceOfDocument = None
          ),
          DocumentCertificateModel(
            documentType = Some("2"),
            documentReference = None,
            documentDescription = None,
            referenceOfDocument = None
          )
        ))
      }
      "when a mix of Other and not Other" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(DocumentsCertificatesPage, true)
            .set(DocumentTypePage(testIndex1), DocumentType("1", "1 type desc"))
            .set(DocumentReferencePage(testIndex1), "1 reference")
            .set(DocumentDescriptionPage(testIndex1), "1 description")
            .set(DocumentTypePage(testIndex2), DocumentType("2", "2 type desc"))
            .set(DocumentReferencePage(testIndex2), "2 reference")
            .set(DocumentDescriptionPage(testIndex2), "12 description")
            .set(DocumentTypePage(testIndex3), DocumentType("0", "0 type desc"))
            .set(DocumentReferencePage(testIndex3), "0 reference")
            .set(DocumentDescriptionPage(testIndex3), "0 description")
        )

        DocumentCertificateModel.apply mustBe Some(Seq(
          DocumentCertificateModel(
            documentType = Some("1"),
            documentReference = None,
            documentDescription = None,
            referenceOfDocument = None
          ),
          DocumentCertificateModel(
            documentType = Some("2"),
            documentReference = None,
            documentDescription = None,
            referenceOfDocument = None
          ),
          DocumentCertificateModel(
            documentType = Some("0"),
            documentReference = Some("0 reference"),
            documentDescription = Some("0 description"),
            referenceOfDocument = None
          )
        ))
      }
    }
    "should return None" - {
      "when DocumentsCertificatesPage is false" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(DocumentsCertificatesPage, false)
        )

        DocumentCertificateModel.apply mustBe None
      }
      "when DocumentsCertificatesPage is true but DocumentsCount is < 1" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(DocumentsCertificatesPage, true)
        )

        DocumentCertificateModel.apply mustBe None
      }
    }
  }
}
