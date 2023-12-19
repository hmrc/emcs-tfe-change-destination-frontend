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

package controllers.sections.items

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import fixtures.ItemFixtures
import mocks.services.{MockGetCnCodeInformationService, MockUserAnswersService}
import mocks.viewmodels.MockItemConfirmCommodityCodeHelper
import models.{ReviewMode, UserAnswers}
import models.requests.{CnCodeInformationItem, DataRequest}
import models.response.referenceData.CnCodeInformation
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Result, Results}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.checkAnswers.sections.items.{ItemCommodityCodeSummary, ItemExciseProductCodeSummary}
import viewmodels.govuk.SummaryListFluency
import views.html.sections.items.ItemConfirmCommodityCodeView

import scala.concurrent.Future

class ItemConfirmCommodityCodeControllerSpec extends SpecBase
  with SummaryListFluency
  with MockItemConfirmCommodityCodeHelper
  with MockUserAnswersService
  with MockGetCnCodeInformationService
  with ItemFixtures {
  def request: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, controllers.sections.items.routes.ItemConfirmCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1).url)

  implicit val testDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(request)

  lazy val itemExciseProductCodeSummary: ItemExciseProductCodeSummary = app.injector.instanceOf[ItemExciseProductCodeSummary]
  lazy val itemCommodityCodeSummary: ItemCommodityCodeSummary = app.injector.instanceOf[ItemCommodityCodeSummary]

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    lazy val controller = new ItemConfirmCommodityCodeController(
      messagesApi,
      fakeAuthAction,
      fakeUserAllowListAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      mockUserAnswersService,
      dataRequiredAction,
      mockGetCnCodeInformationService,
      Helpers.stubMessagesControllerComponents(),
      new FakeItemsNavigator(testOnwardRoute),
      mockItemConfirmCommodityCodeHelper,
      view
    )

    implicit lazy val msgs: Messages = messages(request)

    lazy val confirmCodesList: Seq[SummaryListRow] = Seq(
      itemExciseProductCodeSummary.row(idx = testIndex1, cnCodeInformation = testCommodityCodeTobacco, mode = ReviewMode),
      itemCommodityCodeSummary.row(idx = testIndex1, cnCodeInformation = testCommodityCodeTobacco, mode = ReviewMode).get
    )

    lazy val confirmCodesSummaryList: SummaryList = SummaryListViewModel(
      rows = confirmCodesList
    ).withCssClass("govuk-!-margin-bottom-9")

    lazy val view: ItemConfirmCommodityCodeView = app.injector.instanceOf[ItemConfirmCommodityCodeView]
  }

  "ItemConfirmCommodityCode Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture(Some(emptyUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture(Some(emptyUserAnswers)) {
      def request: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(POST, controllers.sections.items.routes.ItemConfirmCommodityCodeController.onSubmit(testErn, testDraftId, testIndex1).url)
      val result = controller.onSubmit(testErn, testDraftId, testIndex2)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
    }

    ".onPageLoad" - {

      "must return OK and the correct view when supplied EPC and Commodity Code" in new Fixture(
        Some(emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
          .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco))) {

        MockGetCnCodeInformationService
          .getCnCodeInformationWithMovementItems(Seq(CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)))
          .returns(Future.successful(Seq(CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco) -> testCommodityCodeTobacco)))

        MockItemConfirmCommodityCodeHelper.summaryList(testIndex1, testCommodityCodeTobacco).returns(confirmCodesSummaryList)

        val result = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

        val viewAsString = view(controllers.sections.items.routes.ItemConfirmCommodityCodeController.onSubmit(testErn, testDraftId, testIndex1),
          confirmCodesSummaryList
        )(dataRequest(request), messages(request)).toString

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString
      }
      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {

        val result = controller.onPageLoad(testErn, testDraftId, testIndex1)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe controllers.routes.JourneyRecoveryController.onPageLoad().url

      }
    }

    ".onSubmit" - {
      "must redirect to the onward route" in new Fixture(Some(emptyUserAnswers
        .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
        .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco))) {

        def request: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest(POST, controllers.sections.items.routes.ItemConfirmCommodityCodeController.onSubmit(testErn, testDraftId, testIndex1).url)


        val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe testOnwardRoute.url

      }
      "must redirect to Journey Recovery if no existing data is found" in new Fixture(None) {

        def request: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest(POST, controllers.sections.items.routes.ItemConfirmCommodityCodeController.onSubmit(testErn, testDraftId, testIndex1).url)


        val result = controller.onSubmit(testErn, testDraftId, testIndex1)(request)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe controllers.routes.JourneyRecoveryController.onPageLoad().url

      }
    }


    "withCnCodeInformation" - {
      val cnCodeSuccessFunction: CnCodeInformation => Result = _ => Results.Ok

      "must redirect to the index controller" - {
        "when EPC is missing" in new Fixture(Some(emptyUserAnswers.set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco))) {
          val result: Future[Result] = controller.withCnCodeInformation(testIndex1)(cnCodeSuccessFunction)(dataRequest(request, userAnswers.get))

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
        }
        "when CN Code is missing" in new Fixture(Some(emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco))) {
          val result: Future[Result] = controller.withCnCodeInformation(testIndex1)(cnCodeSuccessFunction)(dataRequest(request, userAnswers.get))

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
        }
      }
      "must redirect to JourneyRecovery" - {
        "when both EPC and CN Code are in userAnswers and service returns an empty list" in new Fixture(Some(
          emptyUserAnswers
            .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
        )) {
          MockGetCnCodeInformationService
            .getCnCodeInformationWithMovementItems(Seq(CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)))
            .returns(Future.successful(Nil))

          val result: Future[Result] = controller.withCnCodeInformation(testIndex1)(cnCodeSuccessFunction)(dataRequest(request, userAnswers.get))

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe controllers.routes.JourneyRecoveryController.onPageLoad().url
        }
      }
      "must load the success function" - {
        "when both EPC and CN Code are in userAnswers and service returns a list with one item" in new Fixture(Some(
          emptyUserAnswers
            .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
        )) {
          val item: CnCodeInformationItem = CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)

          MockGetCnCodeInformationService
            .getCnCodeInformationWithMovementItems(Seq(CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)))
            .returns(Future.successful(Seq(item -> testCommodityCodeTobacco)))

          val result: Future[Result] = controller.withCnCodeInformation(testIndex1)(cnCodeSuccessFunction)(dataRequest(request, userAnswers.get))

          status(result) mustBe OK
        }
        "when both EPC and CN Code are in userAnswers and service returns a list with multiple items" in new Fixture(Some(
          emptyUserAnswers
            .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
        )) {
          val item: CnCodeInformationItem = CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)

          MockGetCnCodeInformationService
            .getCnCodeInformationWithMovementItems(Seq(CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)))
            .returns(Future.successful(Seq(
              item -> testCommodityCodeTobacco,
              item -> testCommodityCodeWine
            )))

          val result: Future[Result] = controller.withCnCodeInformation(testIndex1)(cnCodeSuccessFunction)(dataRequest(request, userAnswers.get))

          status(result) mustBe OK
        }
      }
    }
  }
}

