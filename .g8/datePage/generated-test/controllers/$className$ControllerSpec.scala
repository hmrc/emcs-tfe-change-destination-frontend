package controllers

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import forms.$className$FormProvider
import mocks.services.MockUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeNavigator
import pages.$className$Page
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.$className$View

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with MockUserAnswersService {

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val formProvider = new $className$FormProvider()
  lazy val form = formProvider()
  lazy val view = app.injector.instanceOf[$className$View]

  class Test(val userAnswers: Option[UserAnswers]) {
    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new $className$Controller(
      messagesApi,
      mockUserAnswersService,
      fakeBetaAllowListAction,
      new FakeNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      formProvider,
      Helpers.stubMessagesControllerComponents(),
      view
    )

    def getRequest(): Future[Result] =
      controller.onPageLoad(testErn, testDraftId, NormalMode)(request)

    def postRequest(): Future[Result] =
      controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody(
        "value.day" -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year" -> validAnswer.getYear.toString
      ))
  }

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in new Test(Some(emptyUserAnswers)) {
      val result = getRequest()

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(dataRequest(request, userAnswers.get), messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in new Test(Some(
      emptyUserAnswers.set($className$Page, validAnswer)
    )) {
      val result = getRequest()

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode)(dataRequest(request, userAnswers.get), messages).toString
    }

    "must redirect to the next page when valid data is submitted" in new Test(Some(emptyUserAnswers)) {
      MockUserAnswersService.set().returns(Future.successful(emptyUserAnswers))
      val result = postRequest()

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual testOnwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in new Test(Some(emptyUserAnswers)) {
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testDraftId, NormalMode)(request.withFormUrlEncodedBody("value" -> ""))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(dataRequest(request, userAnswers.get), messages).toString
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in new Test(None) {
      val result = getRequest()

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in new Test(None) {
      val result = postRequest()

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
    }
  }
}
