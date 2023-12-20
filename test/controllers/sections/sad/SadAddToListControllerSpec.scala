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

package controllers.sections.sad

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.sections.sad.SadAddToListFormProvider
import mocks.services.MockUserAnswersService
import models.requests.DataRequest
import models.sections.sad.SadAddToListModel
import models.{Index, NormalMode, UserAnswers}
import navigation.SadNavigator
import pages.sections.sad.{ImportNumberPage, SadAddToListPage}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.helpers.SadAddToListHelper
import views.html.sections.sad.SadAddToListView

import scala.concurrent.Future

class SadAddToListControllerSpec extends SpecBase with MockUserAnswersService {

  object FakeHelper extends SadAddToListHelper {
    override def allSadSummary()(implicit request: DataRequest[_], messages: Messages): Seq[SummaryList] = Nil
  }

  lazy val formProvider: SadAddToListFormProvider = new SadAddToListFormProvider()
  lazy val form: Form[SadAddToListModel] = formProvider()
  lazy val view: SadAddToListView = app.injector.instanceOf[SadAddToListView]

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new SadAddToListController(
      messagesApi,
      mockUserAnswersService,
      app.injector.instanceOf[SadNavigator],
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view,
      FakeHelper
    )
  }

  "SadAddToList Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(Some(form), Nil, NormalMode)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set(SadAddToListPage, SadAddToListModel.values.head)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(Some(form.fill(SadAddToListModel.values.head)), Nil, NormalMode)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(emptyUserAnswers)) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", SadAddToListModel.NoMoreToCome.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to task list page CAM-02 if Transport units is 99 for POST" in new Test(Some(
      (0 until 99).foldLeft(emptyUserAnswers)((answers, int) => answers.set(ImportNumberPage(Index(int)), ""))
        .set(SadAddToListPage, SadAddToListModel.Yes)
    )) {
      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", SadAddToListModel.Yes.toString)))

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId).url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(Some(boundForm), Nil, NormalMode)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
