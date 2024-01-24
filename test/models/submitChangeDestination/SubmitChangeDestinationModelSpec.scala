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
import config.AppConfig
import fixtures.SubmitChangeDestinationFixtures
import models.requests.DataRequest
import models.response.emcsTfe.GuarantorType.NoGuarantor
import models.sections.ReviewAnswer.KeepAnswers
import models.sections.consignee.{ConsigneeExportVat, ConsigneeExportVatType}
import models.sections.info.ChangeType.Destination
import pages.sections.consignee.{ConsigneeAddressPage, ConsigneeBusinessNamePage, ConsigneeExcisePage, ConsigneeExportVatPage}
import pages.sections.destination.{DestinationAddressPage, DestinationBusinessNamePage, DestinationConsigneeDetailsPage, DestinationWarehouseExcisePage}
import pages.sections.firstTransporter.FirstTransporterReviewPage
import pages.sections.info.ChangeTypePage
import pages.sections.journeyType.JourneyTypeReviewPage
import pages.sections.movement.MovementReviewAnswersPage
import pages.sections.transportArranger.TransportArrangerReviewPage
import pages.sections.transportUnit.TransportUnitsReviewPage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class SubmitChangeDestinationModelSpec extends SpecBase with SubmitChangeDestinationFixtures {
  implicit val ac: AppConfig = appConfig

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "apply" - {

    "must return a model" - {

      "when all sections have changed and now requires a guarantor (max scenario)" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers,
          ern = testGreatBritainErn,
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = models.response.emcsTfe.MovementGuaranteeModel(NoGuarantor, None))
        )

        SubmitChangeDestinationModel.apply mustBe maxSubmitChangeDestination
      }

      "when all sections have changed and no new guarantor required" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers,
          ern = testGreatBritainErn
        )

        SubmitChangeDestinationModel.apply mustBe maxSubmitChangeDestination.copy(
          destinationChanged = maxSubmitChangeDestination.destinationChanged.copy(
            movementGuarantee = None
          )
        )
      }

      "when all sections have changed apart from Transport Units (no new guarantor required)" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers.set(TransportUnitsReviewPage, KeepAnswers),
          ern = testGreatBritainErn
        )

        SubmitChangeDestinationModel.apply mustBe maxSubmitChangeDestination.copy(
          destinationChanged = maxSubmitChangeDestination.destinationChanged.copy(
            movementGuarantee = None
          ),
          transportDetails = None
        )
      }

      "when all sections have changed apart from Transport Arranger (no new guarantor required)" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers.set(TransportArrangerReviewPage, KeepAnswers),
          ern = testGreatBritainErn
        )

        SubmitChangeDestinationModel.apply mustBe maxSubmitChangeDestination.copy(
          destinationChanged = maxSubmitChangeDestination.destinationChanged.copy(
            movementGuarantee = None
          ),
          updateEadEsad = maxSubmitChangeDestination.updateEadEsad.copy(
            changedTransportArrangement = None
          ),
          newTransportArrangerTrader = None
        )
      }

      "when all sections have changed apart from First Transporter (no new guarantor required)" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers.set(FirstTransporterReviewPage, KeepAnswers),
          ern = testGreatBritainErn
        )

        SubmitChangeDestinationModel.apply mustBe maxSubmitChangeDestination.copy(
          destinationChanged = maxSubmitChangeDestination.destinationChanged.copy(
            movementGuarantee = None
          ),
          newTransporterTrader = None
        )
      }

      "when all sections have changed apart from Journey Type (no new guarantor required)" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers.set(JourneyTypeReviewPage, KeepAnswers),
          ern = testGreatBritainErn
        )

        SubmitChangeDestinationModel.apply mustBe maxSubmitChangeDestination.copy(
          destinationChanged = maxSubmitChangeDestination.destinationChanged.copy(
            movementGuarantee = None
          ),
          updateEadEsad = maxSubmitChangeDestination.updateEadEsad.copy(
            journeyTime = None,
            transportModeCode = None,
            complementaryInformation = None
          )
        )
      }

      "when all sections have changed apart from Movement (no new guarantor required)" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers.set(MovementReviewAnswersPage, KeepAnswers),
          ern = testGreatBritainErn
        )

        SubmitChangeDestinationModel.apply mustBe maxSubmitChangeDestination.copy(
          destinationChanged = maxSubmitChangeDestination.destinationChanged.copy(
            movementGuarantee = None
          ),
          updateEadEsad = maxSubmitChangeDestination.updateEadEsad.copy(
            invoiceDate = None,
            invoiceNumber = None
          )
        )
      }

      "when only Destination Place has changed (minimum scenario)" in {

        val userAnswers = emptyUserAnswers
          // Info Section
          .set(ChangeTypePage, Destination)
          // Sections Reviews
          .set(MovementReviewAnswersPage, KeepAnswers)
          .set(JourneyTypeReviewPage, KeepAnswers)
          .set(FirstTransporterReviewPage, KeepAnswers)
          .set(TransportUnitsReviewPage, KeepAnswers)
          .set(TransportArrangerReviewPage, KeepAnswers)
          // consignee
          .set(ConsigneeBusinessNamePage, "consignee name")
          .set(ConsigneeExcisePage, "consignee ern")
          .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesEoriNumber, Some("vat no"), Some("consignee eori")))
          .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
          // deliveryPlaceTrader
          .set(DestinationWarehouseExcisePage, testGreatBritainErn)
          .set(DestinationConsigneeDetailsPage, false)
          .set(DestinationBusinessNamePage, "destination name")
          .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = userAnswers,
          ern = testGreatBritainErn
        )

        SubmitChangeDestinationModel.apply mustBe minimumSubmitChangeDestinationModel
      }
    }
  }
}
