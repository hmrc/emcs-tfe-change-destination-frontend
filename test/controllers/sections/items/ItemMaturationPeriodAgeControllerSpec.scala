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
import controllers.actions.{DataRequiredAction, FakeAuthAction, FakeDataRetrievalAction, FakeUserAllowListAction}
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemMaturationPeriodAgeMessages
import forms.sections.items.ItemMaturationPeriodAgeFormProvider
import forms.sections.items.ItemMaturationPeriodAgeFormProvider.{hasMaturationPeriodAgeField, maturationPeriodAgeField}
import mocks.services.MockUserAnswersService
import models.GoodsType.Wine
import models.sections.items.ItemMaturationPeriodAgeModel
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemMaturationPeriodAgePage}
import play.api.i18n.MessagesApi
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.sections.items.ItemMaturationPeriodAgeView

import scala.concurrent.Future

class ItemMaturationPeriodAgeControllerSpec extends SpecBase with MockUserAnswersService with ItemFixtures {

  //Ensures a dummy item exists in the array for testing
  val defaultUserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcWine)

  val formProvider = new ItemMaturationPeriodAgeFormProvider()
  val form = formProvider(Wine)(messages(Seq(ItemMaturationPeriodAgeMessages.English.lang)))

  def itemMaturationPeriodAgeRoute(idx: Index = testIndex1) = routes.ItemMaturationPeriodAgeController.onPageLoad(testErn, testDraftId, idx, NormalMode).url

  def itemMaturationPeriodAgeSubmitAction(idx: Index = testIndex1) = routes.ItemMaturationPeriodAgeController.onSubmit(testErn, testDraftId, idx, NormalMode)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(defaultUserAnswers)) {

    val view = app.injector.instanceOf[ItemMaturationPeriodAgeView]

    val controller = new ItemMaturationPeriodAgeController(
      messagesApi = app.injector.instanceOf[MessagesApi],
      userAnswersService = mockUserAnswersService,
      userAllowList = app.injector.instanceOf[FakeUserAllowListAction],
      navigator = new FakeItemsNavigator(testOnwardRoute),
      auth = app.injector.instanceOf[FakeAuthAction],
      getData = new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      requireData = app.injector.instanceOf[DataRequiredAction],
      formProvider = formProvider,
      controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
      view = view
    )

  }

  "ItemMaturationPeriodAge Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Fixture() {

      val request = FakeRequest(GET, itemMaturationPeriodAgeRoute())
      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Fixture() {

      val request = FakeRequest(POST, itemMaturationPeriodAgeRoute(testIndex2)).withFormUrlEncodedBody((hasMaturationPeriodAgeField, "false"))
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Fixture() {

      val request = FakeRequest(GET, itemMaturationPeriodAgeRoute())
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, itemMaturationPeriodAgeSubmitAction(), Wine)(dataRequest(request), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
      Some(defaultUserAnswers.set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("brand"))))
    ) {

      val request = FakeRequest(GET, itemMaturationPeriodAgeRoute())
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("brand"))),
        itemMaturationPeriodAgeSubmitAction(),
        Wine
      )(dataRequest(request), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Fixture() {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val request = FakeRequest(POST, itemMaturationPeriodAgeRoute()).withFormUrlEncodedBody((hasMaturationPeriodAgeField, "false"))
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted (no maturation period supplied)" in new Fixture() {

      val request = FakeRequest(POST, itemMaturationPeriodAgeRoute()).withFormUrlEncodedBody((hasMaturationPeriodAgeField, "true"))
      val boundForm =
        form
          .bind(Map(hasMaturationPeriodAgeField -> "true"))
          .withError(maturationPeriodAgeField, ItemMaturationPeriodAgeMessages.English.errorMaturationPeriodAgeRequired)
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, itemMaturationPeriodAgeSubmitAction(), Wine)(dataRequest(request), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Fixture(None) {

      val request = FakeRequest(GET, itemMaturationPeriodAgeRoute())
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Fixture(None) {

      val request = FakeRequest(POST, itemMaturationPeriodAgeRoute()).withFormUrlEncodedBody(("value", "true"))
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
