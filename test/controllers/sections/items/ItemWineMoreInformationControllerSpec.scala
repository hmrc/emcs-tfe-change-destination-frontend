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
import forms.sections.items.ItemWineMoreInformationFormProvider
import mocks.services.MockUserAnswersService
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemWineMoreInformationChoicePage, ItemWineMoreInformationPage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemWineMoreInformationView

import scala.concurrent.Future

class ItemWineMoreInformationControllerSpec extends SpecBase with MockUserAnswersService {

  def itemWineMoreInformationSubmitAction(idx: Index = testIndex1): Call =
    routes.ItemWineMoreInformationController.onSubmit(testErn, testDraftId, idx, NormalMode)

  lazy val formProvider: ItemWineMoreInformationFormProvider = new ItemWineMoreInformationFormProvider()
  lazy val form: Form[Option[String]] = formProvider()
  lazy val view: ItemWineMoreInformationView = app.injector.instanceOf[ItemWineMoreInformationView]

  lazy val baseUserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200")

  class Test(val userAnswers: Option[UserAnswers] = Some(baseUserAnswers)) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemWineMoreInformationController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemWineMoreInformation Controller" - {

    "must return OK and the correct view for a GET" in new Test() {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, itemWineMoreInformationSubmitAction())(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      baseUserAnswers.set(ItemWineMoreInformationPage(testIndex1), Some("answer"))
    )) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(Some("answer")), itemWineMoreInformationSubmitAction())(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted (with answer, ensuring radio is set to 'true' for the choice page)" in new Test() {

      MockUserAnswersService.set(
        userAnswers.get
          .set(ItemWineMoreInformationChoicePage(testIndex1), true)
          .set(ItemWineMoreInformationPage(testIndex1), Some("answer"))
      ).returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when valid data is submitted (with NO answer, ensuring radio is set to 'false' for the choice page)" in new Test() {

      MockUserAnswersService.set(
        userAnswers.get
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemWineMoreInformationPage(testIndex1), None)
      ).returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must redirect to the next page when valid data is submitted (whitespace-only answer, ensuring radio is set to 'false' for the choice page)" in new Test() {

      MockUserAnswersService.set(
        userAnswers.get
          .set(ItemWineMoreInformationChoicePage(testIndex1), false)
          .set(ItemWineMoreInformationPage(testIndex1), None)
      ).returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "\n\n       \n\n\n\n  ")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test() {

      val boundForm = form.bind(Map("value" -> "<"))
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "<")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, itemWineMoreInformationSubmitAction())(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("value", "answer")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
