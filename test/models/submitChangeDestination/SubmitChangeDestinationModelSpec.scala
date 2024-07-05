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
import models.response.emcsTfe.GuarantorType
import models.sections.ReviewAnswer.KeepAnswers
import models.sections.consignee.ConsigneeExportInformation.EoriNumber
import models.sections.info.ChangeType.Destination
import pages.sections.consignee.{ConsigneeAddressPage, ConsigneeBusinessNamePage, ConsigneeExcisePage, ConsigneeExportInformationPage}
import pages.sections.destination.{DestinationAddressPage, DestinationBusinessNamePage, DestinationConsigneeDetailsPage, DestinationWarehouseExcisePage}
import pages.sections.firstTransporter.FirstTransporterReviewPage
import pages.sections.guarantor.GuarantorReviewPage
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

      "when all sections have changed and now requires a new guarantor (max scenario)" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers.remove(GuarantorReviewPage),
          ern = testGreatBritainWarehouseErn,
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = models.response.emcsTfe.MovementGuaranteeModel(GuarantorType.Consignee, None))
        )

        SubmitChangeDestinationModel.apply mustBe maxSubmitChangeDestination
      }

      "when all sections have changed and no new guarantor required (keeping existing Guarantor)" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers.set(GuarantorReviewPage, KeepAnswers),
          ern = testGreatBritainWarehouseErn,
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = models.response.emcsTfe.MovementGuaranteeModel(GuarantorType.NoGuarantor, None))
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
          answers = baseFullUserAnswers
            .set(GuarantorReviewPage, KeepAnswers)
            .set(TransportUnitsReviewPage, KeepAnswers),
          ern = testGreatBritainWarehouseErn
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
          answers = baseFullUserAnswers
            .set(GuarantorReviewPage, KeepAnswers)
            .set(TransportArrangerReviewPage, KeepAnswers),
          ern = testGreatBritainWarehouseErn
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
          answers = baseFullUserAnswers
            .set(GuarantorReviewPage, KeepAnswers)
            .set(FirstTransporterReviewPage, KeepAnswers),
          ern = testGreatBritainWarehouseErn
        )

        SubmitChangeDestinationModel.apply mustBe maxSubmitChangeDestination.copy(
          destinationChanged = maxSubmitChangeDestination.destinationChanged.copy(
            movementGuarantee = None
          )
        )
      }

      "when all sections have changed apart from Journey Type (no new guarantor required)" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = baseFullUserAnswers
            .set(GuarantorReviewPage, KeepAnswers)
            .set(JourneyTypeReviewPage, KeepAnswers),
          ern = testGreatBritainWarehouseErn
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
          answers = baseFullUserAnswers
            .set(GuarantorReviewPage, KeepAnswers)
            .set(MovementReviewAnswersPage, KeepAnswers),
          ern = testGreatBritainWarehouseErn
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
          .set(GuarantorReviewPage, KeepAnswers)
          // consignee
          .set(ConsigneeBusinessNamePage, "consignee name")
          .set(ConsigneeExcisePage, "consignee ern")
          .set(ConsigneeExportInformationPage, Set(EoriNumber))
          .set(ConsigneeAddressPage, testUserAddress.copy(street = "consignee street"))
          // deliveryPlaceTrader
          .set(DestinationWarehouseExcisePage, testGreatBritainErn)
          .set(DestinationConsigneeDetailsPage, false)
          .set(DestinationBusinessNamePage, "destination name")
          .set(DestinationAddressPage, testUserAddress.copy(street = "destination street"))

        implicit val dr: DataRequest[_] = dataRequest(
          request = fakeRequest,
          answers = userAnswers,
          ern = testGreatBritainWarehouseErn
        )

        SubmitChangeDestinationModel.apply mustBe minimumSubmitChangeDestinationModel
      }
    }
  }
}
