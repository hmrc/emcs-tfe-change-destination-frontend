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
import forms.sections.items.ItemWineOriginFormProvider
import mocks.services.{MockGetCountriesAndMemberStatesService, MockUserAnswersService}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeItemsNavigator
import pages.sections.items.{ItemExciseProductCodePage, ItemWineOriginPage}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.Aliases.SelectItem
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemWineOriginView

import scala.concurrent.Future

class ItemWineOriginControllerSpec
  extends SpecBase
  with MockUserAnswersService
  with MockGetCountriesAndMemberStatesService
  with ItemFixtures {

  private val countries = Seq(countryModelAT, countryModelBE, countryModelGB, countryModelAU, countryModelBR)
  private val countriesWithoutEUMemberStates = Seq(countryModelAU, countryModelBR, countryModelGB)
  lazy val formProvider = new ItemWineOriginFormProvider()
  lazy val form = formProvider(countries)
  lazy val view = app.injector.instanceOf[ItemWineOriginView]
  val baseUserAnswers: UserAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
  val action: Call = controllers.sections.items.routes.ItemWineOriginController.onSubmit(testErn, testDraftId, testIndex1, NormalMode)

  class Test(val userAnswers: Option[UserAnswers], callsService: Boolean = true) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new ItemWineOriginController(
      messagesApi,
      mockUserAnswersService,
      new FakeItemsNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      mockGetCountriesAndMemberStatesService,
      Helpers.stubMessagesControllerComponents(),
      view
    )

    if(callsService) {
      MockGetCountriesAndMemberStatesService.getCountryCodesAndMemberStates().returns(Future.successful(countries))

      MockGetCountriesAndMemberStatesService.removeEUMemberStates(countries).returns(Future.successful(countriesWithoutEUMemberStates))
    }

    val selectItems: Seq[SelectItem] = SelectItemHelper.constructSelectItems(countriesWithoutEUMemberStates, "itemWineOrigin.select.defaultValue")(messages(request))
  }

  "ItemWineOrigin Controller" - {

    "must redirect to Index of section when the idx is outside of bounds for a GET" in new Test(Some(baseUserAnswers), callsService = false) {

      val result = controller.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must redirect to Index of section when the idx is outside of bounds for a POST" in new Test(Some(baseUserAnswers), callsService = false) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("country", "AU")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.ItemsIndexController.onPageLoad(testErn, testDraftId).url
    }

    "must return OK and the correct view for a GET" in new Test(Some(baseUserAnswers)) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, action, selectItems)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      baseUserAnswers.set(ItemWineOriginPage(testIndex1), countryModelAU)
    )) {

      val sampleEPCsSelectOptionsWithAUSelected = SelectItemHelper.constructSelectItems(
        selectOptions = countriesWithoutEUMemberStates,
        defaultTextMessageKey = "itemWineOrigin.select.defaultValue",
        existingAnswer = Some("AU"))(messages(request))

      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(countryModelAT), action, sampleEPCsSelectOptionsWithAUSelected)(dataRequest(request,
        userAnswers.get), messages(request)).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(baseUserAnswers)) {

      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("country", "AU")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(baseUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("country", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, action, selectItems)(dataRequest(request, userAnswers.get), messages(request)).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None, callsService = false) {
      val result = controller.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None, callsService = false) {
      val result = controller.onSubmit(testErn, testDraftId, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("country", "AU")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
