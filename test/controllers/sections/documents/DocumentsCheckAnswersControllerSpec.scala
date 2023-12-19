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

package controllers.sections.documents

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockDocumentsCheckAnswersHelper
import models.UserAnswers
import models.requests.DataRequest
import navigation.FakeNavigators.FakeDocumentsNavigator
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.helpers.CheckYourAnswersDocumentsHelper
import views.html.sections.documents.DocumentsCheckAnswersView

class DocumentsCheckAnswersControllerSpec extends SpecBase with MockUserAnswersService with MockDocumentsCheckAnswersHelper {

  implicit val request: DataRequest[AnyContentAsEmpty.type] =
    dataRequest(FakeRequest(GET, routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId).url))
  implicit val msgs: Messages = messages(request)
  lazy val view: DocumentsCheckAnswersView = app.injector.instanceOf[DocumentsCheckAnswersView]
  val summaryList: SummaryList = app.injector.instanceOf[CheckYourAnswersDocumentsHelper].summaryList()(request, msgs)

  class Fixture(optUserAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {
    lazy val testController = new DocumentsCheckAnswersController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeDocumentsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(optUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      messagesControllerComponents,
      view,
      mockDocumentsCheckAnswersHelper
    )
  }

  "DocumentsCheckAnswers Controller" - {
    "must return OK and the correct view for a GET" in new Fixture() {
      MockDocumentsCheckAnswersHelper.summaryList().returns(summaryList)

      val result = testController.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(summaryList).toString
    }

    "must redirect to task list for POST" in new Fixture() {
      val req = dataRequest(FakeRequest(POST, routes.DocumentsCheckAnswersController.onSubmit(testErn, testDraftId).url))

      val result = testController.onSubmit(testErn, testDraftId)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {
      val result = testController.onPageLoad(testErn, testDraftId)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {
      val req = FakeRequest(POST, routes.DocumentsCheckAnswersController.onPageLoad(testErn, testDraftId).url).withFormUrlEncodedBody(("value", "true"))

      val result = testController.onSubmit(testErn, testDraftId)(req)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
