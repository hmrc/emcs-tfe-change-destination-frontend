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
import forms.sections.transportUnit.TransportUnitsAddToListFormProvider
import mocks.services.MockUserAnswersService
import models.response.emcsTfe.TransportDetailsModel
import models.sections.transportUnit.TransportUnitType.Tractor
import models.sections.transportUnit.TransportUnitsAddToListModel
import models.sections.transportUnit.TransportUnitsAddToListModel.{MoreToCome, NoMoreToCome}
import models.{Index, NormalMode, UserAnswers}
import navigation.TransportUnitNavigator
import pages.sections.transportUnit.{TransportUnitTypePage, TransportUnitsAddToListPage}
import play.api.Play.materializer
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.helpers.TransportUnitsAddToListHelper
import views.html.sections.transportUnit.TransportUnitsAddToListView

import scala.concurrent.Future

class TransportUnitsAddToListControllerSpec extends SpecBase with MockUserAnswersService {

  def submitRoute: Call = routes.TransportUnitsAddToListController.onSubmit(testErn, testArc)

  lazy val formProvider: TransportUnitsAddToListFormProvider = new TransportUnitsAddToListFormProvider()

  lazy val form: Form[TransportUnitsAddToListModel] = formProvider()

  lazy val view: TransportUnitsAddToListView = app.injector.instanceOf[TransportUnitsAddToListView]

  lazy val helper: TransportUnitsAddToListHelper = app.injector.instanceOf[TransportUnitsAddToListHelper]

  class Test(userAnswers: Option[UserAnswers], is99TransportUnits: Boolean = false) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    val transportUnitsIn801: Seq[TransportDetailsModel] = if (is99TransportUnits) {
      (0 until 99).map { index =>
        TransportDetailsModel(
          transportUnitCode = s"TransportDetailsTransportUnitCode${index + 1}",
          identityOfTransportUnits = Some(s"TransportDetailsIdentityOfTransportUnits${index + 1}"),
          commercialSealIdentification = Some(s"TransportDetailsCommercialSealIdentification${index + 1}"),
          complementaryInformation = Some(s"TransportDetailsComplementaryInformation${index + 1}"),
          sealInformation = Some(s"TransportDetailsSealInformation${index + 1}")
        )
      }
    } else {
      maxGetMovementResponse.transportDetails
    }

    lazy val fullCheckAnswers: Seq[SummaryList] = helper.allTransportUnitsSummary()(dataRequest(request, userAnswers.getOrElse(emptyUserAnswers), movementDetails = maxGetMovementResponse.copy(transportDetails = transportUnitsIn801)), messages(request))

    lazy val controller = new TransportUnitsAddToListController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      app.injector.instanceOf[TransportUnitNavigator],
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      new FakeMovementAction(maxGetMovementResponse.copy(transportDetails = transportUnitsIn801)),
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      helper
    )
  }

  "TransportUnitsAddToList Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(Some(form), Nil, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.values.head)
    )) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(Some(form.fill(TransportUnitsAddToListModel.values.head)), Nil, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must have no form populated if Transport units is 99 for GET" in new Test(Some(
      (0 until 99)
        .foldLeft(emptyUserAnswers)((answers, int) => answers.set(TransportUnitTypePage(Index(int)), Tractor))
        .set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.values.head)
    ), is99TransportUnits = true) {
      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(None, fullCheckAnswers, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to task list page CAM-02 if Transport units is 99 for POST" in new Test(Some(
      (0 until 99)
        .foldLeft(emptyUserAnswers)((answers, int) => answers.set(TransportUnitTypePage(Index(int)), Tractor))
        .set(TransportUnitsAddToListPage, TransportUnitsAddToListModel.values.head)
    )) {

      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody("value" -> TransportUnitsAddToListModel.Yes.toString))

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.DraftMovementController.onPageLoad(testErn, testArc).url
    }

    "must redirect to transport unit type page with next index if yes selected and clear down the answer for the page" in new Test(Some(
      emptyUserAnswers
        .set(TransportUnitTypePage(testIndex1), Tractor)
        .set(TransportUnitsAddToListPage, MoreToCome)
    )) {

      MockUserAnswersService
        .set(emptyUserAnswers
          .set(TransportUnitTypePage(testIndex1), Tractor))
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitTypePage(testIndex1), Tractor)
          ))

      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.Yes.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(testErn, testArc, testIndex2, NormalMode).url
    }

    "must redirect to task list page if NoMoreToCome is selected" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      MockUserAnswersService
        .set()
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitsAddToListPage, NoMoreToCome)
          ))

      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.NoMoreToCome.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.routes.DraftMovementController.onPageLoad(testErn, testArc).url
    }

    "must redirect to task list page if MoreToCome is selected" in new Test(Some(
      emptyUserAnswers.set(TransportUnitTypePage(testIndex1), Tractor)
    )) {
      MockUserAnswersService
        .set()
        .returns(
          Future.successful(
            emptyUserAnswers
              .set(TransportUnitsAddToListPage, MoreToCome)
          ))

      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.MoreToCome.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.routes.DraftMovementController.onPageLoad(testErn, testArc).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", "invalid value")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(Some(boundForm), Nil, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      val result = controller.onPageLoad(testErn, testArc)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      val result = controller.onSubmit(testErn, testArc)(request.withFormUrlEncodedBody(("value", TransportUnitsAddToListModel.values.head.toString)))

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
