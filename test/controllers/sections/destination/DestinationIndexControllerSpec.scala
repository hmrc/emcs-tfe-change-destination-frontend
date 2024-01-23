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

package controllers.sections.destination

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import mocks.services.MockUserAnswersService
import models.sections.info.movementScenario.MovementScenario._
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeDestinationNavigator
import pages.sections.destination.{DestinationDetailsChoicePage, DestinationWarehouseVatPage}
import pages.sections.info.DestinationTypePage
import play.api.http.Status.SEE_OTHER
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._

class DestinationIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.DestinationIndexController.onPageLoad(testErn, testArc).url)

    lazy val testController = new DestinationIndexController(
      mockUserAnswersService,
      new FakeDestinationNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(deliveryPlaceTrader = None)),
      fakeUserAllowListAction,
      messagesControllerComponents
    )

  }

  "DestinationIndexController" - {
    "when DestinationSection.isCompleted" - {
      "must redirect to the CYA controller" in new Fixture(Some(emptyUserAnswers
        .set(DestinationTypePage, TemporaryRegisteredConsignee)
        .set(DestinationWarehouseVatPage, "vat")
        .set(DestinationDetailsChoicePage, false)
      )) {

        val result = testController.onPageLoad(testErn, testArc)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.DestinationCheckAnswersController.onPageLoad(testErn, testArc).url)
      }
    }

    "must redirect to the destination warehouse excise controller" - {
      Seq(GbTaxWarehouse, EuTaxWarehouse).foreach(
        answer =>
          s"when DestinationTypePage answer is $answer" in new Fixture(Some(emptyUserAnswers
            .set(DestinationTypePage, answer)
          )) {
            Seq("GBWK123", "GBRC123", "XIWK123", "XIRC123").foreach {
              ern =>
                val result = testController.onPageLoad(ern, testArc)(request)

                status(result) mustEqual SEE_OTHER
                redirectLocation(result) mustBe
                  Some(routes.DestinationWarehouseExciseController.onPageLoad(ern, testArc, NormalMode).url)
            }
          }
      )
    }

    "must redirect to the destination warehouse vat controller" - {
      Seq(RegisteredConsignee,
        TemporaryRegisteredConsignee,
        ExemptedOrganisation).foreach(
        answer =>
          s"when DestinationTypePage answer is $answer" in new Fixture(Some(emptyUserAnswers
            .set(DestinationTypePage, answer)
          )) {
            Seq("GBWK123", "GBRC123", "XIWK123", "XIRC123").foreach {
              ern =>
                val result = testController.onPageLoad(ern, testArc)(request)

                status(result) mustEqual SEE_OTHER
                redirectLocation(result) mustBe
                  Some(routes.DestinationWarehouseVatController.onPageLoad(ern, testArc, NormalMode).url)
            }
          }
      )
    }

    "must redirect to the destination business name controller" - {
      Seq(DirectDelivery).foreach(
        answer =>
          s"when DestinationTypePage answer is $answer" in new Fixture(Some(emptyUserAnswers
            .set(DestinationTypePage, answer)
          )) {
            Seq("GBWK1234", "GBRC1234", "XIWK1234", "XIRC1234").foreach {
              ern =>
                val result = testController.onPageLoad(ern, testArc)(request)

                status(result) mustEqual SEE_OTHER
                redirectLocation(result) mustBe
                  Some(routes.DestinationBusinessNameController.onPageLoad(ern, testArc, NormalMode).url)
            }
          }
      )
    }

    "must redirect to the tasklist" - {
      Seq(UnknownDestination,
        ExportWithCustomsDeclarationLodgedInTheEu,
        ExportWithCustomsDeclarationLodgedInTheUk).foreach(
        answer =>
          s"when DestinationTypePage answer is $answer" in new Fixture(Some(emptyUserAnswers
            .set(DestinationTypePage, answer)
          )) {
            Seq("GBWK123", "GBRC123", "XIWK123", "XIRC123").foreach {
              ern =>
                val result = testController.onPageLoad(ern, testArc)(request)

                status(result) mustEqual SEE_OTHER
                redirectLocation(result) mustBe
                  Some(controllers.routes.TaskListController.onPageLoad(ern, testArc).url)
            }
          }
      )
    }
  }
}
