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
import forms.sections.items.ItemPackagingProductTypeFormProvider
import mocks.services.MockUserAnswersService
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemPackagingProductTypePage, ItemPackagingShippingMarksPage, ItemSelectPackagingPage}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemPackagingProductTypeView

import scala.concurrent.Future

class ItemPackagingProductTypeControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  lazy val formProvider = new ItemPackagingProductTypeFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[ItemPackagingProductTypeView]

  def onSubmitAction(itemIdx: Index, packagingIdx: Index): Call =
    routes.ItemPackagingProductTypeController.onSubmit(testErn, testDraftId, itemIdx, packagingIdx, NormalMode)

  private val packageType = testItemPackagingTypes.head
  private val packageTypeDescription = packageType.description

  class Test(val userAnswers: Option[UserAnswers]) {

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemPackagingProductTypeController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      userAllowList = fakeUserAllowListAction,
      navigator = new FakeItemsNavigator(testOnwardRoute),
      auth = fakeAuthAction,
      getData = new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requireData = dataRequiredAction,
      formProvider = formProvider,
      controllerComponents = Helpers.stubMessagesControllerComponents(),
      view = view
    )
  }

  "ItemPackagingProductType Controller" - {

    "for GET onPageLoad" - {

      "must return OK and the correct view for a GET" in new Test(Some(
        emptyUserAnswers
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), packageType)
      )) {

        val result = controller.onPageLoad(testErn, testDraftId, 0, 0, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          description = packageTypeDescription,
          onSubmitAction = onSubmitAction(testIndex1, testPackagingIndex1)
        )(dataRequest(request, userAnswers.get), messages(request)).toString
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
        emptyUserAnswers
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testItemPackagingTypes.head)
      )) {

        val result = controller.onPageLoad(testErn, testDraftId, 0, 0, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(true),
          description = packageTypeDescription,
          onSubmitAction = onSubmitAction(testIndex1, testPackagingIndex1)
        )(dataRequest(request, userAnswers.get), messages(request)).toString
      }

      "must redirect to the ItemsPackagingIndexController when no packageType has been given" in new Test(Some(emptyUserAnswers)) {

        val result = controller.onPageLoad(testErn, testDraftId, 0, 0, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, 0).url
      }

      "must redirect to the ItemsPackagingIndexController when the packagingIndex is invalid" in new Test(Some(
        emptyUserAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testItemPackagingTypes.head)
      )) {

        val result = controller.onPageLoad(testErn, testDraftId, 0, 1, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, 0).url
      }

      "must redirect to the ItemsPackagingIndexController when the itemsIndex is invalid" in new Test(Some(
        emptyUserAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testItemPackagingTypes.head)
      )) {

        val result = controller.onPageLoad(testErn, testDraftId, 1, 0, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, 1).url
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
        val result = controller.onPageLoad(testErn, testDraftId, 0, 0, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "for POST onSubmit" - {

      s"must redirect to the next page when Yes is submitted (removing any shipping marks)" in new Test(Some(
        emptyUserAnswers
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), packageType)
          .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "SHIPPING")
      )) {

        MockUserAnswersService.set(
          emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), packageType)
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
        ).returns(Future.successful(
          emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), packageType)
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
        ))

        val result = controller.onSubmit(testErn, testDraftId, 0, 0, NormalMode)(
          request.withFormUrlEncodedBody(("value", "true"))
        )

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      s"must redirect to the next page when No is submitted (without removing the shipping marks answer)" in new Test(Some(
        emptyUserAnswers
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), packageType)
          .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "SHIPPING")
      )) {

        MockUserAnswersService.set(
          emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), packageType)
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), false)
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "SHIPPING")
        ).returns(Future.successful(
          emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), packageType)
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), false)
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "SHIPPING")
        ))

        val result = controller.onSubmit(testErn, testDraftId, 0, 0, NormalMode)(
          request.withFormUrlEncodedBody(("value", "false"))
        )

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(
        emptyUserAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), packageType)
      )) {
        val boundForm = form.bind(Map("value" -> ""))

        val result = controller.onSubmit(testErn, testDraftId, 0, 0, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          description = packageTypeDescription,
          onSubmitAction = onSubmitAction(testIndex1, testPackagingIndex1)
        )(dataRequest(request, userAnswers.get), messages(request)).toString
      }

      "must redirect to the ItemsPackagingIndexController when no packageType has been given and invalid data is submitted" in new Test(Some(
        emptyUserAnswers
      )) {

        val result = controller.onSubmit(testErn, testDraftId, 0, 0, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, 0).url
      }

      "must redirect to the ItemsPackagingIndexController when the packagingIndex is invalid" in new Test(Some(
        emptyUserAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), packageType)
      )) {

        val result = controller.onSubmit(testErn, testDraftId, 0, 1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, 0).url
      }

      "must redirect to the ItemsPackagingIndexController when the itemIndex is invalid" in new Test(Some(
        emptyUserAnswers.set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), packageType)
      )) {

        val result = controller.onSubmit(testErn, testDraftId, 1, 0, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, 1).url
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
        val result = controller.onSubmit(testErn, testDraftId, 0, 0, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
