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

package controllers.sections.transportUnit

import base.SpecBase
import controllers.actions.{FakeDataRetrievalAction, FakeMovementAction}
import mocks.services.MockUserAnswersService
import models.response.emcsTfe.{TransportDetailsModel, TransportModeModel}
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.journeyType.HowMovementTransported.RoadTransport
import models.sections.transportUnit.TransportUnitType.{Container, FixedTransport}
import models.sections.transportUnit.{TransportSealTypeModel, TransportUnitType}
import models.{NormalMode, UserAnswers}
import navigation.TransportUnitNavigator
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import queries.TransportUnitsCount

import scala.concurrent.Future

class TransportUnitIndexControllerSpec extends SpecBase with MockUserAnswersService {

  class Test(userAnswers: Option[UserAnswers],
             transportUnits: Seq[TransportDetailsModel] = Seq.empty,
             transportMode: TransportModeModel = maxGetMovementResponse.transportMode) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new TransportUnitIndexController(
      messagesApi,
      mockUserAnswersService,
      app.injector.instanceOf[TransportUnitNavigator],
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(transportDetails = transportUnits, transportMode = transportMode)),
      Helpers.stubMessagesControllerComponents()
    )
  }

  "TransportUnitIndex Controller" - {

    "must redirect to the transport unit review answers page (COD-08) when the section needs reviewing" in new Test(
      Some(emptyUserAnswers), transportUnits = Seq(maxGetMovementResponse.transportDetails.head.copy(transportUnitCode = "1"))
    ) {
      val expectedUserAnswers = emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportSealChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("TransportDetailsComplementaryInformation1"))
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("TransportDetailsCommercialSealIdentification1", Some("TransportDetailsSealInformation1")))
        .set(TransportUnitIdentityPage(testIndex1), "TransportDetailsIdentityOfTransportUnits1")

      MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe
        routes.TransportUnitsReviewController.onPageLoad(testErn, testArc).url
    }

    "must redirect to the transport unit check answers page (CAM-TU09) when the journey type is " +
      "Fixed Transport Installations" in new Test(
      Some(emptyUserAnswers),
      transportUnits = Seq(maxGetMovementResponse.transportDetails.head.copy(transportUnitCode = "5")),
      transportMode = maxGetMovementResponse.transportMode.copy(transportModeCode = "7")
    ) {

      val expectedUserAnswers = emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), FixedTransport)
        .set(TransportSealChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("TransportDetailsComplementaryInformation1"))
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("TransportDetailsCommercialSealIdentification1", Some("TransportDetailsSealInformation1")))
        .set(TransportUnitIdentityPage(testIndex1), "TransportDetailsIdentityOfTransportUnits1")
        .set(TransportUnitsReviewPage, KeepAnswers)

      MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe
        routes.TransportUnitCheckAnswersController.onPageLoad(testErn, testArc).url
    }

    "must redirect to the transport unit type page (CAM-TU01) when no transport units answered" in new Test(Some(
      emptyUserAnswers.set(TransportUnitsReviewPage, ChangeAnswers)
    )) {

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.TransportUnitTypeController.onPageLoad(testErn, testArc, testIndex1, NormalMode).url
    }

    "must redirect to the transport unit type page (CAM-TU01) when there is an empty transport unit list" in new Test(Some(
      emptyUserAnswers
        .set(HowMovementTransportedPage, RoadTransport)
        .set(TransportUnitsCount, Seq.empty)
        .set(TransportUnitsReviewPage, ChangeAnswers)
    )) {

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(testErn, testArc, testIndex1, NormalMode).url
    }

    "must redirect to the add to list page (CAM-TU07) when any answer is present" in new Test(Some(
      emptyUserAnswers
        .set(HowMovementTransportedPage, RoadTransport)
        .set(TransportUnitTypePage(testIndex1), TransportUnitType.Vehicle)
        .set(TransportUnitsReviewPage, ChangeAnswers)
    )) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.TransportUnitsAddToListController.onPageLoad(testErn, testArc).url
    }

    "must redirect to the add to list page (CAM-TU07) when any answer is present " +
      "adding any 801 TU's when none are present in the user answers (prevents index out of bounds when adding new TU's)" in new Test(
      Some(emptyUserAnswers
        .set(HowMovementTransportedPage, RoadTransport)
        .set(TransportUnitsReviewPage, ChangeAnswers)
      ), transportUnits = Seq(maxGetMovementResponse.transportDetails.head.copy(transportUnitCode = "1"))
    ) {

      val expectedUserAnswers = emptyUserAnswers
        .set(HowMovementTransportedPage, RoadTransport)
        .set(TransportUnitsReviewPage, ChangeAnswers)
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportSealChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("TransportDetailsComplementaryInformation1"))
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("TransportDetailsCommercialSealIdentification1", Some("TransportDetailsSealInformation1")))
        .set(TransportUnitIdentityPage(testIndex1), "TransportDetailsIdentityOfTransportUnits1")

      MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.TransportUnitsAddToListController.onPageLoad(testErn, testArc).url
    }

    "must redirect to the add to list page (CAM-TU07) when any answer is present " +
      "adding any 801 TU's when none are present in the user answers (prevents index out of bounds when adding new TU's) " +
      "setting only TransportSealTypePage when TransportUnitIdentity is not provided" in new Test(
      Some(emptyUserAnswers
        .set(HowMovementTransportedPage, RoadTransport)
        .set(TransportUnitsReviewPage, ChangeAnswers)
      ), transportUnits = Seq(maxGetMovementResponse.transportDetails.head.copy(transportUnitCode = "1", identityOfTransportUnits = None))
    ) {

      val expectedUserAnswers = emptyUserAnswers
        .set(HowMovementTransportedPage, RoadTransport)
        .set(TransportUnitsReviewPage, ChangeAnswers)
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportSealChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("TransportDetailsComplementaryInformation1"))
        .set(TransportSealTypePage(testIndex1), TransportSealTypeModel("TransportDetailsCommercialSealIdentification1", Some("TransportDetailsSealInformation1")))

      MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.TransportUnitsAddToListController.onPageLoad(testErn, testArc).url
    }

    "must redirect to the add to list page (CAM-TU07) when any answer is present " +
      "adding any 801 TU's when none are present in the user answers (prevents index out of bounds when adding new TU's) " +
      "setting only TransportUnitIdentityPage when Transport Seal Info is not provided" in new Test(
      Some(emptyUserAnswers
        .set(HowMovementTransportedPage, RoadTransport)
        .set(TransportUnitsReviewPage, ChangeAnswers)
      ), transportUnits = Seq(maxGetMovementResponse.transportDetails.head.copy(transportUnitCode = "1", commercialSealIdentification = None))
    ) {

      val expectedUserAnswers = emptyUserAnswers
        .set(HowMovementTransportedPage, RoadTransport)
        .set(TransportUnitsReviewPage, ChangeAnswers)
        .set(TransportUnitTypePage(testIndex1), Container)
        .set(TransportSealChoicePage(testIndex1), false)
        .set(TransportUnitGiveMoreInformationChoicePage(testIndex1), true)
        .set(TransportUnitGiveMoreInformationPage(testIndex1), Some("TransportDetailsComplementaryInformation1"))
        .set(TransportUnitIdentityPage(testIndex1), "TransportDetailsIdentityOfTransportUnits1")

      MockUserAnswersService.set(expectedUserAnswers).returns(Future.successful(expectedUserAnswers))

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.TransportUnitsAddToListController.onPageLoad(testErn, testArc).url
    }
  }
}
