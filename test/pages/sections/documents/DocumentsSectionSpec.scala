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

package pages.sections.documents

import base.SpecBase
import fixtures.DocumentTypeFixtures
import models.requests.DataRequest
import models.sections.documents.DocumentsAddToList
import play.api.test.FakeRequest
import viewmodels.taskList.{Completed, InProgress, NotStarted}

class DocumentsSectionSpec extends SpecBase with DocumentTypeFixtures {

  "isCompleted" - {

    "must return true" - {

      "when finished" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DocumentsCertificatesPage, false)
        )

        DocumentsSection.isCompleted mustBe true
      }
    }

    "must return false" - {

      "when not finished" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DocumentsCertificatesPage, true)
        )

        DocumentsSection.isCompleted mustBe false
      }
    }
  }

  "status" - {

    "mustBe Completed" - {

      "when DocumentCertificatesPage is set to false" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
          .set(DocumentsCertificatesPage, false)
        )

        DocumentsSection.status mustBe Completed
      }

      "when DocumentCertificatesPage is set to true" - {

        "when every document has been Completed and the DocumentsAddToList page has been answered No" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(DocumentsCertificatesPage, true)
            .set(DocumentTypePage(0), documentTypeOtherModel)
            .set(ReferenceAvailablePage(0), true)
            .set(DocumentReferencePage(0), "reference")
            .set(DocumentsAddToListPage, DocumentsAddToList.No)
          )

          DocumentsSection.status mustBe Completed
        }

        "when every document has been Completed and the MAX amount of documents has been reached" in {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(DocumentsCertificatesPage, true)
            .set(DocumentTypePage(0), documentTypeModel).set(DocumentReferencePage(0), "reference")
            .set(DocumentTypePage(1), documentTypeModel).set(DocumentReferencePage(1), "reference")
            .set(DocumentTypePage(2), documentTypeModel).set(DocumentReferencePage(2), "reference")
            .set(DocumentTypePage(3), documentTypeModel).set(DocumentReferencePage(3), "reference")
            .set(DocumentTypePage(4), documentTypeModel).set(DocumentReferencePage(4), "reference")
            .set(DocumentTypePage(5), documentTypeModel).set(DocumentReferencePage(5), "reference")
            .set(DocumentTypePage(6), documentTypeModel).set(DocumentReferencePage(6), "reference")
            .set(DocumentTypePage(7), documentTypeModel).set(DocumentReferencePage(7), "reference")
            .set(DocumentTypePage(8), documentTypeModel).set(DocumentReferencePage(8), "reference")
          )

          DocumentsSection.status mustBe Completed
        }
      }
    }

    "mustBe InProgress" - {

      "when DocumentCertificatesPage is set to true" - {

        "when every document has been Completed" - {

          "the DocumentsAddToList page has been answered MoreLater" in {

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(DocumentsCertificatesPage, true)
              .set(DocumentTypePage(0), documentTypeOtherModel)
              .set(ReferenceAvailablePage(0), true)
              .set(DocumentReferencePage(0), "reference")
              .set(DocumentsAddToListPage, DocumentsAddToList.MoreLater)
            )

            DocumentsSection.status mustBe InProgress
          }

          "the DocumentsAddToList page has been answered Yes" in {

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(DocumentsCertificatesPage, true)
              .set(DocumentTypePage(0), documentTypeOtherModel)
              .set(ReferenceAvailablePage(0), true)
              .set(DocumentReferencePage(0), "reference")
              .set(DocumentsAddToListPage, DocumentsAddToList.Yes)
            )

            DocumentsSection.status mustBe InProgress
          }

          "the DocumentsAddToList page has NOT been answered" in {

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(DocumentsCertificatesPage, true)
              .set(DocumentTypePage(0), documentTypeOtherModel)
              .set(ReferenceAvailablePage(0), true)
              .set(DocumentReferencePage(0), "reference")
            )

            DocumentsSection.status mustBe InProgress
          }
        }

        "when every document at least one document is InProgress" - {

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers
            .set(DocumentsCertificatesPage, true)
            .set(DocumentTypePage(0), documentTypeOtherModel)
            .set(ReferenceAvailablePage(0), true)
            .set(DocumentReferencePage(0), "reference")
            .set(ReferenceAvailablePage(1), false)
            .set(DocumentsAddToListPage, DocumentsAddToList.No)
          )

          DocumentsSection.status mustBe InProgress
        }
      }
    }

    "mustBe NotStated" - {

      "when DocumentCertificatesPage is NOT answered" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        DocumentsSection.status mustBe NotStarted
      }
    }
  }
}
