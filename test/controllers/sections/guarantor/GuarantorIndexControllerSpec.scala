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

package controllers.sections.guarantor

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import mocks.services.MockUserAnswersService
import models.response.emcsTfe.GuarantorType.{Consignee, NoGuarantor}
import models.response.emcsTfe.{GetMovementResponse, MovementGuaranteeModel}
import models.sections.ReviewAnswer.ChangeAnswers
import models.sections.guarantor.GuarantorArranger.Consignor
import models.sections.info.movementScenario.MovementScenario.{ExportWithCustomsDeclarationLodgedInTheUk, UkTaxWarehouse}
import models.{NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage, GuarantorReviewPage}
import pages.sections.info.DestinationTypePage
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class GuarantorIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers), movementResponse: GetMovementResponse = maxGetMovementResponse) {

    val request = FakeRequest(GET, routes.GuarantorIndexController.onPageLoad(testErn, testArc).url)

    lazy val testController = new GuarantorIndexController(
      mockUserAnswersService,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(movementResponse),
      messagesControllerComponents
    )

  }


  "GuarantorIndexController" - {
    "when GuarantorSection.isCompleted" - {
      "must redirect to the CYA controller" in new Fixture(
        Some(emptyUserAnswers
          .set(GuarantorReviewPage, ChangeAnswers)
          .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
          .set(GuarantorArrangerPage, Consignor)
          .set(ConsignorAddressPage, UserAddress(None, None, "", "", ""))
        )) {

        val result = testController.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc).url)
      }
    }

    "when there the section is not completed or needs review" - {

      "when it needs review" - {

        "must redirect to the GuarantorReview page" in new Fixture(
          Some(emptyUserAnswers
            .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
            .set(GuarantorArrangerPage, Consignor)
            .set(ConsignorAddressPage, UserAddress(None, None, "", "", ""))
          )) {

          val result = testController.onPageLoad(testErn, testArc)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.GuarantorReviewController.onPageLoad(testErn, testArc).url)
        }
      }

      "when the destination type has changed to Export and the Guarantor was previously a Consignee" - {

        "must redirect to the GuarantorRequired page" in new Fixture(
          Some(emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)),
          maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(Consignee, None))
        ) {
          val result = testController.onPageLoad(testGreatBritainWarehouseErn, testArc)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.GuarantorRequiredController.onPageLoad(testGreatBritainWarehouseErn, testArc, NormalMode).url)
        }
      }

      "when in progress must redirect to the guarantor arranger controller" in new Fixture(
        Some(emptyUserAnswers
          .set(GuarantorReviewPage, ChangeAnswers)
          .set(GuarantorRequiredPage, true)
          .set(DestinationTypePage, UkTaxWarehouse.GB)),
        movementResponse = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
      ) {

        val result = testController.onPageLoad(testGreatBritainWarehouseErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.GuarantorArrangerController.onPageLoad(testGreatBritainWarehouseErn, testArc, NormalMode).url)
      }
    }
  }

}
