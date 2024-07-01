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
import models.response.MissingMandatoryPage
import models.sections.transportUnit.{TransportSealTypeModel, TransportUnitType}
import pages.sections.transportUnit._
import play.api.test.FakeRequest

class TransportDetailsModelSpec extends SpecBase {
  "apply" - {
    "must throw an error" - {
      "when no transport units" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers,
          movementDetails = maxGetMovementResponse.copy(transportDetails = Seq.empty)
        )

        val result = intercept[MissingMandatoryPage](TransportDetailsModel.apply)

        result.message mustBe "TransportUnitSection should contain at least one item"
      }
    }

    "must return a Seq(TransportDetailsModel)" - {
      "when there is one transport unit" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
            .set(TransportUnitIdentityPage(testIndex1), "identity")
            .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal type", Some("seal info")))
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more info"))
        )

        TransportDetailsModel.apply mustBe Seq(
          TransportDetailsModel(
            transportUnitCode = TransportUnitType.FixedTransport,
            identityOfTransportUnits = Some("identity"),
            commercialSealIdentification = Some("seal type"),
            complementaryInformation = Some("more info"),
            sealInformation = Some("seal info")
          )
        )
      }
      "when there is more than one transport unit" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(TransportUnitTypePage(testIndex1), TransportUnitType.FixedTransport)
            .set(TransportUnitIdentityPage(testIndex1), "identity")
            .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("seal type", Some("seal info")))
            .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("more info"))
            .set(TransportUnitTypePage(testIndex2), TransportUnitType.Trailer)
            .set(TransportUnitIdentityPage(testIndex2), "identity 2")
            .set(TransportSealTypePage(testIndex2), TransportSealTypeModel("seal type 2", Some("seal info 2")))
            .set(TransportUnitGiveMoreInformationPage(testIndex2), Some("more info 2"))
        )

        TransportDetailsModel.apply mustBe Seq(
          TransportDetailsModel(
            transportUnitCode = TransportUnitType.FixedTransport,
            identityOfTransportUnits = Some("identity"),
            commercialSealIdentification = Some("seal type"),
            complementaryInformation = Some("more info"),
            sealInformation = Some("seal info")
          ),
          TransportDetailsModel(
            transportUnitCode = TransportUnitType.Trailer,
            identityOfTransportUnits = Some("identity 2"),
            commercialSealIdentification = Some("seal type 2"),
            complementaryInformation = Some("more info 2"),
            sealInformation = Some("seal info 2")
          )
        )
      }
    }
  }
}
