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
import forms.sections.items.ItemsAddToListFormProvider
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockItemsAddToListHelper
import models.UserAnswers
import models.sections.items.ItemGeographicalIndicationType.NoGeographicalIndication
import models.sections.items.{ItemBrandNameModel, ItemNetGrossMassModel, ItemsAddToList}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items._
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.items.ItemsAddToListView

import scala.concurrent.Future

class ItemsAddToListControllerSpec extends SpecBase with MockUserAnswersService with MockItemsAddToListHelper with ItemFixtures {

  lazy val formProvider: ItemsAddToListFormProvider = new ItemsAddToListFormProvider()
  lazy val form: Form[ItemsAddToList] = formProvider()
  lazy val view: ItemsAddToListView = app.injector.instanceOf[ItemsAddToListView]

  lazy val controllerRoute: String = routes.ItemsAddToListController.onPageLoad(testErn, testDraftId).url
  lazy val onSubmitCall: Call = routes.ItemsAddToListController.onSubmit(testErn, testDraftId)

  val userAnswersWithMaxItems: UserAnswers =
    (0 until ItemsSectionItems.MAX).foldLeft(emptyUserAnswers) {
      case (userAnswers, idx) =>
        userAnswers
          .set(ItemExciseProductCodePage(idx), testEpcWine)
          .set(ItemCommodityCodePage(idx), testCnCodeWine)
          .set(ItemBrandNamePage(idx), ItemBrandNameModel(hasBrandName = true, Some("brand")))
          .set(ItemCommercialDescriptionPage(idx), "Wine from grapes")
          .set(ItemAlcoholStrengthPage(idx), BigDecimal(12.5))
          .set(ItemGeographicalIndicationChoicePage(idx), NoGeographicalIndication)
          .set(ItemQuantityPage(idx), BigDecimal("1000"))
          .set(ItemNetGrossMassPage(idx), ItemNetGrossMassModel(BigDecimal("2000"), BigDecimal("2105")))
          .set(ItemBulkPackagingChoicePage(idx), false)
          .set(ItemImportedWineFromEuChoicePage(idx), true)
          .set(ItemWineMoreInformationChoicePage(idx), false)
          .set(ItemSelectPackagingPage(idx, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(idx, testPackagingIndex1), "400")
          .set(ItemPackagingProductTypePage(idx, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(idx, testPackagingIndex1), false)
    }

  class Setup(val startingUserAnswers: Option[UserAnswers] = Some(singleCompletedWineItem)) {

    val request = FakeRequest(GET, controllerRoute)

    lazy val testController = new ItemsAddToListController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(startingUserAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      messagesControllerComponents,
      view,
      mockItemsAddToListHelper
    )

  }

  "ItemsAddToList Controller" - {

    "GET onPageLoad" - {

      "must return SEE_OTHER and Redirect to ItemIndex when there are no items added yet" in new Setup(Some(emptyUserAnswers)) {

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must return OK and the correct view when there are NO InProgress items" in new Setup() {

        MockItemsAddToListHelper.allItemsSummary().returns(Future.successful(Seq()))

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = Some(form),
          onSubmitCall = onSubmitCall,
          items = Seq.empty,
          showNoOption = true
        )(dataRequest(request), messages(request)).toString
      }

      "must return OK and the correct view when there ARE InProgress items" in new Setup(Some(singleCompletedWineItem
        .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
        .set(ItemCommodityCodePage(testIndex2), testCnCodeTobacco)
      )) {

        MockItemsAddToListHelper.allItemsSummary().returns(Future.successful(Seq()))

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = Some(form),
          onSubmitCall = onSubmitCall,
          items = Seq.empty,
          showNoOption = false
        )(dataRequest(request), messages(request)).toString
      }

      "must clear down any items which don't have a EPC and CnCode before rendering" in new Setup(Some(singleCompletedWineItem
        .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
      )) {

        MockItemsAddToListHelper.allItemsSummary().returns(Future.successful(Seq()))
        MockUserAnswersService.set(singleCompletedWineItem).returns(Future.successful(singleCompletedWineItem))

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = Some(form),
          onSubmitCall = onSubmitCall,
          items = Seq.empty,
          showNoOption = true
        )(dataRequest(request), messages(request)).toString
      }

      "must return OK and the correct view when there are MAX items already added" in new Setup(Some(userAnswersWithMaxItems)) {

        MockItemsAddToListHelper.allItemsSummary().returns(Future.successful(Seq()))

        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = None,
          onSubmitCall = onSubmitCall,
          items = Seq.empty,
          showNoOption = false
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {
        val result = testController.onPageLoad(testErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "POST onSubmit" - {

      "must redirect to the next page when Yes is submitted" in new Setup() {

        MockUserAnswersService.set(singleCompletedWineItem).returns(Future.successful(singleCompletedWineItem))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", ItemsAddToList.Yes.toString))

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page and wipe data when Yes is submitted with this page already answered" in new Setup(Some(
        singleCompletedWineItem
          .set(ItemsAddToListPage, ItemsAddToList.No)
      )) {

        MockUserAnswersService.set(singleCompletedWineItem).returns(Future.successful(singleCompletedWineItem))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", ItemsAddToList.Yes.toString))

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      Seq(
        ItemsAddToList.No,
        ItemsAddToList.MoreLater
      ).foreach { answer =>
        s"must redirect to the next page when $answer is submitted" in new Setup() {

          val updatedAnswers = singleCompletedWineItem.set(ItemsAddToListPage, answer)

          MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

          val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", answer.toString))

          val result = testController.onSubmit(testErn, testDraftId)(req)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual testOnwardRoute.url
        }
      }

      "must redirect to the next page when submitted with MAX packages already added" in new Setup(Some(userAnswersWithMaxItems)) {
        val req = FakeRequest(POST, controllerRoute)

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Setup() {

        MockItemsAddToListHelper.allItemsSummary().returns(Future.successful(Seq()))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          formOpt = Some(boundForm),
          onSubmitCall = onSubmitCall,
          items = Seq.empty,
          showNoOption = true
        )(dataRequest(req), messages(req)).toString
      }

      "redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {
        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", ItemsAddToList.values.head.toString))

        val result = testController.onSubmit(testErn, testDraftId)(req)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
