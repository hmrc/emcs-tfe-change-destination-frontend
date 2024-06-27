/*
 * Copyright 2024 HM Revenue & Customs
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

package models.audit

import base.SpecBase
import fixtures.SubmitChangeDestinationFixtures
import models.requests.DataRequest
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

import java.time.LocalDate

class SubmitChangeDestinationAuditSpec extends SpecBase with SubmitChangeDestinationFixtures  {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "Audit details" - {
    "must output as expected" in {

      val date = LocalDate.now()

      implicit val dr: DataRequest[_] = dataRequest(
        request = fakeRequest,
        answers = baseFullUserAnswers,
        ern = "XIRC123"
      )

      val auditJson = SubmitChangeDestinationAudit(
        ern = dr.ern,
        receiptDate = date.toString,
        submissionRequest = maxSubmitChangeDestination,
        submissionResponse = Right(submitChangeDestinationResponseEIS)
      ).detail

      auditJson mustBe
        Json.parse(
          s"""
          |{
          |  "exciseRegistrationNumber": "XIRC123",
          |  "newTransportArrangerTrader": {
          |    "traderName": "arranger name",
          |    "address": {
          |      "streetNumber": "10",
          |      "street": "arranger street",
          |      "postcode": "ZZ1 1ZZ",
          |      "city": "Testown"
          |    },
          |    "vatNumber": "arranger vat"
          |  },
          |  "updateEadEsad": {
          |    "administrativeReferenceCode": "arc",
          |    "journeyTime": "2 hours",
          |    "changedTransportArrangement": {
          |      "code": "3",
          |      "description": "GoodsOwner"
          |    },
          |    "invoiceDate": "2020-02-02",
          |    "invoiceNumber": "somereference",
          |    "transportModeCode": {
          |      "code": "0",
          |      "description": "Other"
          |    },
          |    "complementaryInformation": "info"
          |  },
          |  "destinationChanged": {
          |    "destinationTypeCode": {
          |      "code": "6",
          |      "description": "Export"
          |    },
          |    "newConsigneeTrader": {
          |      "traderExciseNumber": "consignee ern",
          |      "traderName": "consignee name",
          |      "address": {
          |        "streetNumber": "10",
          |        "street": "consignee street",
          |        "postcode": "ZZ1 1ZZ",
          |        "city": "Testown"
          |      },
          |      "eoriNumber": "consignee eori"
          |    },
          |    "deliveryPlaceCustomsOffice": {
          |      "referenceNumber": "exportOffice"
          |    },
          |    "movementGuarantee": {
          |      "guarantorTypeCode": {
          |        "code": "3",
          |        "description": "GoodsOwner"
          |      },
          |      "guarantorTrader": [
          |        {
          |          "traderName": "guarantor name",
          |          "address": {
          |            "streetNumber": "10",
          |            "street": "guarantor street",
          |            "postcode": "ZZ1 1ZZ",
          |            "city": "Testown"
          |          },
          |          "vatNumber": "guarantor vat"
          |        }
          |      ]
          |    }
          |  },
          |  "newTransporterTrader": {
          |    "traderName": "first name",
          |    "address": {
          |      "streetNumber": "10",
          |      "street": "first street",
          |      "postcode": "ZZ1 1ZZ",
          |      "city": "Testown"
          |    },
          |    "vatNumber": "first vat"
          |  },
          |  "transportDetails": [
          |    {
          |      "transportUnitCode": {
          |       "code":"2",
          |       "description":"Vehicle"
          |      },
          |      "identityOfTransportUnits": "identity",
          |      "commercialSealIdentification": "seal type",
          |      "complementaryInformation": "more info",
          |      "sealInformation": "seal info"
          |    }
          |  ],
          |  "status": "success",
          |  "receipt": "UYVQBLMXCYK6HAEBZI7TSWAQ6XDTXFYU",
          |  "receiptDate": "${date.toString}",
          |  "responseCode": 200
          |}
          |""".stripMargin)
    }
  }
}
