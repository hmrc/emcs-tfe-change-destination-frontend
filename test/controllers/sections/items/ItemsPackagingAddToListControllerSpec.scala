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
import forms.sections.items.ItemsPackagingAddToListFormProvider
import mocks.services.MockUserAnswersService
import mocks.viewmodels.MockItemsPackagingAddToListHelper
import models.UserAnswers
import models.sections.items.ItemsPackagingAddToList
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items._
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.items.ItemsPackagingAddToListView

import scala.concurrent.Future

class ItemsPackagingAddToListControllerSpec extends SpecBase with MockUserAnswersService with MockItemsPackagingAddToListHelper {

  lazy val formProvider: ItemsPackagingAddToListFormProvider = new ItemsPackagingAddToListFormProvider()
  lazy val form: Form[ItemsPackagingAddToList] = formProvider()
  lazy val view: ItemsPackagingAddToListView = app.injector.instanceOf[ItemsPackagingAddToListView]

  lazy val controllerRoute: String = routes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1).url
  lazy val onSubmitCall: Call = routes.ItemsPackagingAddToListController.onSubmit(testErn, testDraftId, testIndex1)

  val singlePackageUserAnswers = emptyUserAnswers
    .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
    .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
    .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
    .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

  val userAnswersWithMaxPackages: UserAnswers =
    (0 until ItemsPackagingSection(testIndex1).MAX).foldLeft(emptyUserAnswers) {
      case (userAnswers, packageIdx) =>
        userAnswers
          .set(ItemSelectPackagingPage(testIndex1, packageIdx), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, packageIdx), "5")
          .set(ItemPackagingProductTypePage(testIndex1, packageIdx), true)
          .set(ItemPackagingSealChoicePage(testIndex1, packageIdx), false)
    }

  class Setup(val startingUserAnswers: Option[UserAnswers] = Some(singlePackageUserAnswers)) {

    val request = FakeRequest(GET, controllerRoute)

    lazy val testController = new ItemsPackagingAddToListController(
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
      mockItemsPackagingAddToListHelper
    )

  }

  "ItemsPackagingAddToList Controller" - {

    "GET onPageLoad" - {

      "must return OK and the correct view when there are NO InProgress items" in new Setup() {

        MockItemsPackagingAddToListHelper.allPackagesSummary(testIndex1)()

        val result = testController.onPageLoad(testErn, testDraftId, testIndex1)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = Some(form),
          onSubmitCall = onSubmitCall,
          packages = Seq.empty,
          showNoOption = true,
          itemIdx = testIndex1
        )(dataRequest(request), messages(request)).toString
      }

      "must return OK and the correct view when there ARE InProgress items" in new Setup(
        Some(singlePackageUserAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), testPackageAerosol))
      ) {

        MockItemsPackagingAddToListHelper.allPackagesSummary(testIndex1)()

        val result = testController.onPageLoad(testErn, testDraftId, testIndex1)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = Some(form),
          onSubmitCall = onSubmitCall,
          packages = Seq.empty,
          showNoOption = false,
          itemIdx = testIndex1
        )(dataRequest(request), messages(request)).toString
      }

      "must return OK and the correct view when there MAX packages already added" in new Setup(Some(userAnswersWithMaxPackages)) {

        MockItemsPackagingAddToListHelper.allPackagesSummary(testIndex1)()

        val result = testController.onPageLoad(testErn, testDraftId, testIndex1)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = None,
          onSubmitCall = onSubmitCall,
          packages = Seq.empty,
          showNoOption = false,
          itemIdx = testIndex1
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {
        val result = testController.onPageLoad(testErn, testDraftId, testIndex1)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "POST onSubmit" - {

      "must redirect to the next page when Yes is submitted" in new Setup() {

        MockUserAnswersService.set(singlePackageUserAnswers).returns(Future.successful(singlePackageUserAnswers))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", ItemsPackagingAddToList.Yes.toString))

        val result = testController.onSubmit(testErn, testDraftId, testIndex1)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page and wipe data when Yes is submitted with this page already answered" in new Setup(Some(
        singlePackageUserAnswers
          .set(ItemsPackagingAddToListPage(testIndex1), ItemsPackagingAddToList.No)
      )) {

        MockUserAnswersService.set(singlePackageUserAnswers).returns(Future.successful(singlePackageUserAnswers))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", ItemsPackagingAddToList.Yes.toString))

        val result = testController.onSubmit(testErn, testDraftId, testIndex1)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page when No is submitted" in new Setup() {

        val updatedAnswers = singlePackageUserAnswers.set(ItemsPackagingAddToListPage(testIndex1), ItemsPackagingAddToList.No)

        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", ItemsPackagingAddToList.No.toString))

        val result = testController.onSubmit(testErn, testDraftId, testIndex1)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page when MoreLater is submitted" in new Setup() {

        val updatedAnswers = singlePackageUserAnswers.set(ItemsPackagingAddToListPage(testIndex1), ItemsPackagingAddToList.MoreLater)

        MockUserAnswersService.set(updatedAnswers).returns(Future.successful(updatedAnswers))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", ItemsPackagingAddToList.MoreLater.toString))

        val result = testController.onSubmit(testErn, testDraftId, testIndex1)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must redirect to the next page when submitted with MAX packages already added" in new Setup(Some(userAnswersWithMaxPackages)) {
        val req = FakeRequest(POST, controllerRoute)

        val result = testController.onSubmit(testErn, testDraftId, testIndex1)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Setup() {
        MockItemsPackagingAddToListHelper.allPackagesSummary(testIndex1)()

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val result = testController.onSubmit(testErn, testDraftId, testIndex1)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          formOpt = Some(boundForm),
          onSubmitCall = onSubmitCall,
          packages = Seq.empty,
          showNoOption = true,
          itemIdx = testIndex1
        )(dataRequest(req), messages(req)).toString
      }

      "redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {
        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", ItemsPackagingAddToList.values.head.toString))

        val result = testController.onSubmit(testErn, testDraftId, testIndex1)(req)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
