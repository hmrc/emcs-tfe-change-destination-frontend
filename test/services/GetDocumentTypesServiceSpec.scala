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

package services

import base.SpecBase
import fixtures.DocumentTypeFixtures
import mocks.connectors.MockGetDocumentTypesConnector
import models.response.{DocumentTypesException, UnexpectedDownstreamResponseError}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetDocumentTypesServiceSpec extends SpecBase with MockGetDocumentTypesConnector with DocumentTypeFixtures {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetDocumentTypesService(mockGetDocumentTypesConnector)

  ".getDocumentTypes" - {

    "should return Seq[DocumentType] with other at the end" - {

      "when Connector returns success from downstream" in {

        val expectedResult = Seq(
          documentTypeModel,
          documentTypeModel,
          documentTypeOtherModel
        )

        MockGetDocumentTypesConnector.getDocumentTypes().returns(Future(Right(
          Seq(
            documentTypeModel,
            documentTypeOtherModel,
            documentTypeModel
          )
        )))

        val actualResults = testService.getDocumentTypes().futureValue

        actualResults mustBe expectedResult
      }
    }

    "should throw DocumentTypesException" - {

      "when Connector returns failure from downstream" in {

        val expectedResult = "No document types retrieved"

        MockGetDocumentTypesConnector.getDocumentTypes().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[DocumentTypesException](await(testService.getDocumentTypes())).getMessage

        actualResult mustBe expectedResult
      }
    }
  }
}
