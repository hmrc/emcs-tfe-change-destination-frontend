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

package controllers.sections.info

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.actions.predraft.FakePreDraftRetrievalAction
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import mocks.viewmodels.MockInformationCheckAnswersHelper
import models.UserAnswers
import models.sections.info.movementScenario.MovementScenario.GbTaxWarehouse
import navigation.FakeNavigators.FakeInfoNavigator
import pages.sections.info.{DeferredMovementPage, DestinationTypePage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.SummaryListFluency
import views.html.sections.info.InformationCheckAnswersView

import scala.concurrent.Future

class InformationCheckAnswersControllerSpec extends SpecBase
  with SummaryListFluency
  with MockInformationCheckAnswersHelper
  with MockUserAnswersService
  with MockPreDraftService {

  val list: SummaryList = SummaryListViewModel(Seq.empty).withCssClass("govuk-!-margin-bottom-9")

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val view: InformationCheckAnswersView = app.injector.instanceOf[InformationCheckAnswersView]

  class Fixtures(userAnswers: Option[UserAnswers]) {
    lazy val controller = new InformationCheckAnswersController(
      messagesApi,
      mockPreDraftService,
      mockUserAnswersService,
      new FakeInfoNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakePreDraftRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      preDraftDataRequiredAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      MockInformationCheckAnswersHelper,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "InformationCheckAnswers Controller" - {

    val userAnswers = emptyUserAnswers.set(DestinationTypePage, GbTaxWarehouse).set(DeferredMovementPage(), true)

    "pre-draft" - {
      "must return OK and the correct view for a GET" in new Fixtures(Some(userAnswers)) {
        MockCheckAnswersJourneyTypeHelper.summaryList(deferredMovement = true).returns(list)

        val result = controller.onPreDraftPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          list = list,
          submitAction = controllers.sections.info.routes.InformationCheckAnswersController.onPreDraftSubmit(testErn)
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to the next page when submitting the page" in new Fixtures(Some(userAnswers)) {
        MockUserAnswersService.set().returns(Future.successful(userAnswers))
        MockPreDraftService.clear(testErn, testSessionId).returns(Future.successful(true))

        val result = controller.onPreDraftSubmit(testErn)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }

    "post-draft" - {
      "must return OK and the correct view for a GET" in new Fixtures(Some(userAnswers)) {
        MockCheckAnswersJourneyTypeHelper.summaryList(deferredMovement = true).returns(list)

        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          list = list,
          submitAction = controllers.sections.info.routes.InformationCheckAnswersController.onSubmit(testErn, testDraftId)
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to the next page when submitting the page" in new Fixtures(Some(userAnswers)) {
        val result = controller.onSubmit(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }
    }
  }
}
