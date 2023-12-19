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
import forms.sections.items.ItemWineGrowingZoneFormProvider
import mocks.services.{MockGetCnCodeInformationService, MockUserAnswersService}
import models.sections.items.ItemWineGrowingZone
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemWineGrowingZonePage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemWineGrowingZoneView

import scala.concurrent.Future

class ItemWineGrowingZoneControllerSpec extends SpecBase with MockUserAnswersService with MockGetCnCodeInformationService {

  lazy val formProvider = new ItemWineGrowingZoneFormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[ItemWineGrowingZoneView]

  lazy val submitUrl = routes.ItemWineGrowingZoneController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  lazy val defaultUserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200")

  class Test(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemWineGrowingZoneController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      navigator = new FakeItemsNavigator(testOnwardRoute),
      auth = fakeAuthAction,
      getData = new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requireData = dataRequiredAction,
      userAllowList = fakeUserAllowListAction,
      formProvider = formProvider,
      controllerComponents = Helpers.stubMessagesControllerComponents(),
      view = view
    )
  }

  "ItemWineGrowingZone Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Test() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Test() {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Test() {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, submitUrl)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      defaultUserAnswers.set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.values.head)
    )) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(ItemWineGrowingZone.values.head), submitUrl)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test() {

      MockUserAnswersService.set(defaultUserAnswers.set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.values.head))
        .returns(Future.successful(defaultUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", ItemWineGrowingZone.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test() {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, submitUrl)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", ItemWineGrowingZone.values.head.toString)))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
