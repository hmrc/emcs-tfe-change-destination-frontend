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

package controllers.sections.importInformation

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.routes
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockCheckYourAnswersImportHelper
import models.requests.DataRequest
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeImportInformationNavigator
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.sections.importInformation.ImportCustomsOfficeCodeSummary
import viewmodels.govuk.SummaryListFluency
import views.html.sections.importInformation.CheckYourAnswersImportView

class CheckYourAnswersImportControllerSpec extends SpecBase with SummaryListFluency
  with MockCheckYourAnswersImportHelper with MockUserAnswersService {

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  implicit val testDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(request)
  implicit val msgs: Messages = messages(request)

  lazy val view: CheckYourAnswersImportView = app.injector.instanceOf[CheckYourAnswersImportView]

  val summaryList: SummaryList = SummaryListViewModel(
    rows = Seq(ImportCustomsOfficeCodeSummary.row(showActionLinks = true)).flatten
  ).withCssClass("govuk-!-margin-bottom-9")

  class Fixture(userAnswers: Option[UserAnswers]) {
    lazy val controller = new CheckYourAnswersImportController(
      messagesApi,
      fakeAuthAction,
      fakeUserAllowListAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      Helpers.stubMessagesControllerComponents(),
      new FakeImportInformationNavigator(testOnwardRoute),
      MockCheckYourAnswersImportHelper,
      view
    )
  }

  "CheckYourAnswersImportController" - {
    ".onPageLoad" - {

      "must return OK and the correct view" in new Fixture(Some(emptyUserAnswers.set(ImportCustomsOfficeCodePage, "AB123456"))) {
        MockCheckAnswersImportHelper.summaryList().returns(summaryList)
        val result = controller.onPageLoad(testErn, testDraftId)(request)

        lazy val viewAsString = view(
          testErn,
          testDraftId,
          summaryList
        )(dataRequest(request), messages(request)).toString

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString
      }

      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {
        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.JourneyRecoveryController.onPageLoad().url
      }

      "must redirect to /customs-office if user answers doesn't contain the correct page" in new Fixture(Some(emptyUserAnswers)) {
        val result = controller.onPageLoad(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe
          controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onPageLoad(testErn, testDraftId, NormalMode).url
      }
    }

    ".onSubmit" - {
      "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers)) {
        val result = controller.onSubmit(testErn, testDraftId)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe testOnwardRoute.url
      }
    }
  }
}

