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
import forms.sections.items.ItemProducerSizeFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.GoodsType.Wine
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemProducerSizePage}
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import utils.TimeMachine
import views.html.sections.items.ItemProducerSizeView

import java.time.LocalDateTime
import scala.concurrent.Future

class ItemProducerSizeControllerSpec extends SpecBase with MockPreDraftService with MockUserAnswersService with ItemFixtures {

  class Setup(userAnswers: Option[UserAnswers] = Some(emptyUserAnswers), monthValue: Int = 2) {

    object FakeTimeMachine extends TimeMachine {
      override def now(): LocalDateTime =
        LocalDateTime.of(2023, monthValue, 12, 1, 1, 1)
    }

    val formProvider = new ItemProducerSizeFormProvider()
    val form = formProvider()

    val view = app.injector.instanceOf[ItemProducerSizeView]

    object TestController extends ItemProducerSizeController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      navigator = new FakeItemsNavigator(testOnwardRoute),
      auth = fakeAuthAction,
      getData = new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requireData = dataRequiredAction,
      userAllowList = fakeUserAllowListAction,
      formProvider = formProvider,
      controllerComponents = Helpers.stubMessagesControllerComponents(),
      view = view,
      timeMachine = FakeTimeMachine
    )

    def itemProducerSizeRoute(idx: Index): String = routes.ItemProducerSizeController.onPageLoad(testErn, testDraftId, idx, NormalMode).url

    def onSubmitAction(idx: Index): Call = routes.ItemProducerSizeController.onSubmit(testErn, testDraftId, idx, NormalMode)
  }


  "ItemProducerSize Controller" - {

    "for a GET onPageLoad" - {

      "must return OK and the correct view for a GET with a goodstype set" in new Setup(Some(
        emptyUserAnswers.set(ItemExciseProductCodePage(0), testEpcWine)
      )) {

        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, itemProducerSizeRoute(0))

        val result: Future[Result] = TestController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          onSubmitAction = onSubmitAction(0),
          goodsType = Wine,
          startYear = "2022",
          endYear = "2023"
        )(dataRequest(request), messages(request)).toString
      }

      "must return OK and the correct view for a GET when the current date is January" in new Setup(
        userAnswers = Some(emptyUserAnswers.set(ItemExciseProductCodePage(0), testEpcWine)),
        monthValue = 1
      ) {

        val request = FakeRequest(GET, itemProducerSizeRoute(0))

        val result: Future[Result] = TestController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form,
          onSubmitAction = onSubmitAction(0),
          goodsType = Wine,
          startYear = "2021",
          endYear = "2022"
        )(dataRequest(request), messages(request)).toString
      }

      "must populate the view correctly on a GET when the question has previously been answered" in new Setup(Some(
        emptyUserAnswers
          .set(ItemExciseProductCodePage(0), testEpcWine)
          .set(ItemProducerSizePage(0), BigInt(1))
      )) {

        val request = FakeRequest(GET, itemProducerSizeRoute(0))

        val result: Future[Result] = TestController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = form.fill(1),
          onSubmitAction = onSubmitAction(0),
          goodsType = Wine,
          startYear = "2022",
          endYear = "2023"
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to Index of section when the idx is outside of bounds for a GET" in new Setup(Some(
        emptyUserAnswers.set(ItemExciseProductCodePage(0), testEpcWine)
      )) {
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, itemProducerSizeRoute(1))

        val result = TestController.onPageLoad(testErn, testDraftId, 1, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to ItemsIndexController for a GET there is no GoodsType in UserAnswers" in new Setup() {

        val request = FakeRequest(GET, itemProducerSizeRoute(0))

        val result: Future[Result] = TestController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.items.routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to Journey Recovery for a GET if no existing data is found" in new Setup(None) {

        val request = FakeRequest(GET, itemProducerSizeRoute(0))

        val result: Future[Result] = TestController.onPageLoad(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }

    }

    "for a POST onSubmit" - {

      "must redirect to the next page when valid data is submitted" in new Setup() {

        MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

        val request = FakeRequest(POST, itemProducerSizeRoute(0))
          .withFormUrlEncodedBody(("value", "1"))

        val result: Future[Result] = TestController.onSubmit(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "must return a Bad Request and errors when invalid data is submitted and there is a GoodsType set" in new Setup(Some(
        emptyUserAnswers.set(ItemExciseProductCodePage(0), testEpcWine)
      )) {

        val request = FakeRequest(POST, itemProducerSizeRoute(0))
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val result: Future[Result] = TestController.onSubmit(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          form = boundForm,
          onSubmitAction = onSubmitAction(0),
          goodsType = Wine,
          startYear = "2022",
          endYear = "2023"
        )(dataRequest(request), messages(request)).toString
      }

      "must redirect to Index of section when the idx is outside of bounds for a GET" in new Setup(Some(
        emptyUserAnswers.set(ItemExciseProductCodePage(0), testEpcWine)
      )) {

        val request = FakeRequest(POST, itemProducerSizeRoute(1))
          .withFormUrlEncodedBody(("value", ""))
        val result = TestController.onSubmit(testErn, testDraftId, 1, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to ItemsIndexController when invalid data is submitted and there is a No GoodsType set" in new Setup() {

        val request = FakeRequest(POST, itemProducerSizeRoute(0))
          .withFormUrlEncodedBody(("value", ""))

        val result: Future[Result] = TestController.onSubmit(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.sections.items.routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
      }

      "must redirect to Journey Recovery for a POST if no existing data is found" in new Setup(None) {

        val request = FakeRequest(POST, itemProducerSizeRoute(0))
          .withFormUrlEncodedBody(("value", "1"))

        val result: Future[Result] = TestController.onSubmit(testErn, testDraftId, 0, NormalMode)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
