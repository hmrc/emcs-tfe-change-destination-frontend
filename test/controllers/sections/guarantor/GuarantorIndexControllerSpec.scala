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
import models.response.emcsTfe.GuarantorType.NoGuarantor
import models.response.emcsTfe.MovementGuaranteeModel
import models.sections.guarantor.GuarantorArranger.Consignor
import models.sections.info.movementScenario.MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk
import models.{NormalMode, UserAddress, UserAnswers}
import navigation.FakeNavigators.FakeGuarantorNavigator
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage}
import pages.sections.info.DestinationTypePage
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers._

class GuarantorIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    val request = FakeRequest(GET, controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testArc).url)

    lazy val testController = new GuarantorIndexController(
      mockUserAnswersService,
      new FakeGuarantorNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))),
      fakeUserAllowListAction,
      messagesControllerComponents
    )

  }


  "GuarantorIndexController" - {
    "when GuarantorSection.isCompleted" - {
      "must redirect to the CYA controller" in new Fixture(
        Some(emptyUserAnswers
          .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
          .set(GuarantorRequiredPage, true)
          .set(GuarantorArrangerPage, Consignor)
          .set(ConsignorAddressPage, UserAddress(None, "", "", ""))
        )) {

        val result = testController.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(testErn, testArc).url)
      }
    }

    "when there the section is not completed or needs review" - {

      "must redirect to the guarantor required controller" in new Fixture(
        Some(emptyUserAnswers
          .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
        )) {
        val result = testController.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.sections.guarantor.routes.GuarantorRequiredController.onPageLoad(testErn, testArc, NormalMode).url)
      }
    }
  }

}
