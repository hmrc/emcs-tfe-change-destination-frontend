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
import controllers.actions.{DataRequiredAction, FakeDataRetrievalAction}
import fixtures.ItemFixtures
import forms.sections.items.ItemWineOperationsChoiceFormProvider
import mocks.services.{MockGetWineOperationsService, MockUserAnswersService}
import models.response.referenceData.WineOperations
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemWineOperationsChoicePage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.items.ItemWineOperationsChoiceView

import scala.concurrent.Future

class ItemWineOperationsChoiceControllerSpec extends SpecBase
  with MockUserAnswersService
  with MockGetWineOperationsService
  with ItemFixtures {

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val formProvider = new ItemWineOperationsChoiceFormProvider()
  lazy val form: Form[Set[WineOperations]] = formProvider.apply(testWineOperations)
  lazy val view: ItemWineOperationsChoiceView = app.injector.instanceOf[ItemWineOperationsChoiceView]

  val baseUserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), "W200")

  def submitRoute: Call = routes.ItemWineOperationsChoiceController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  class Test(userAnswers: Option[UserAnswers] = Some(baseUserAnswers), callsService: Boolean = true) {

    if (callsService) {
      MockGetWineOperationsService.getWineOperations().returns(Future.successful(testWineOperations))
    }

    lazy val controller = new ItemWineOperationsChoiceController(
      messagesApi,
      mockUserAnswersService,
      fakeUserAllowListAction,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      app.injector.instanceOf[DataRequiredAction],
      formProvider,
      mockGetWineOperationsService,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "ItemWineOperationsChoiceController" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Test(callsService = false) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Test(callsService = false) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("value[]", "1")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url)
    }

    "must redirect to Index of section when Excise Product Code is missing" in new Test(Some(emptyUserAnswers), callsService = false) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Test() {

      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest())

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, submitRoute, testWineOperations)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(
      Some(baseUserAnswers.set(
        ItemWineOperationsChoicePage(testIndex1),
        Set(WineOperations("1", "The product has been enriched"))
      ))
    ) {

      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest())

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(Set(WineOperations("1", "The product has been enriched"))),
        submitRoute,
        testWineOperations
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test() {

      MockUserAnswersService.set(
        baseUserAnswers.set(
          ItemWineOperationsChoicePage(testIndex1),
          Set(WineOperations("1", "The product has been enriched")))
      ).returns(
        Future.successful(baseUserAnswers.set(
          ItemWineOperationsChoicePage(testIndex1),
          Set(WineOperations("1", "The product has been enriched"))))
      )

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(
        FakeRequest().withFormUrlEncodedBody(("value[]", "1"))
      )

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test() {

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest().withFormUrlEncodedBody(("value", "invalid value")))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, submitRoute, testWineOperations)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None, callsService = false) {

      val result: Future[Result] = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest())

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in new Test(None, callsService = false) {

      val result: Future[Result] = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(FakeRequest().withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
