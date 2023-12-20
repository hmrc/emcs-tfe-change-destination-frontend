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

package controllers.sections.info

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import controllers.actions.predraft.FakePreDraftRetrievalAction
import forms.sections.info.LocalReferenceNumberFormProvider
import mocks.services.{MockPreDraftService, MockUserAnswersService}
import models.sections.info.movementScenario.MovementScenario.{GbTaxWarehouse, UnknownDestination}
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakeInfoNavigator
import pages.sections.info.{DeferredMovementPage, DestinationTypePage, LocalReferenceNumberPage}
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import views.html.sections.info.LocalReferenceNumberView

import scala.concurrent.Future

class LocalReferenceNumberControllerSpec extends SpecBase with MockUserAnswersService with MockPreDraftService {

  lazy val formProvider: LocalReferenceNumberFormProvider = new LocalReferenceNumberFormProvider()
  lazy val form: Form[String] = formProvider(isDeferred = false)
  lazy val view: LocalReferenceNumberView = app.injector.instanceOf[LocalReferenceNumberView]

  lazy val localReferenceNumberPreDraftSubmitRoute: Call =
    controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftSubmit(testErn, NormalMode)
  lazy val localReferenceNumberSubmitRoute: Call =
    controllers.sections.info.routes.LocalReferenceNumberController.onSubmit(testErn, testDraftId)

  class Fixture(val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)) {

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new LocalReferenceNumberController(
      messagesApi,
      mockPreDraftService,
      new FakeInfoNavigator(testOnwardRoute),
      fakeAuthAction,
      new FakePreDraftRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      preDraftDataRequiredAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeUserAllowListAction,
      formProvider,
      mockUserAnswersService,
      Helpers.stubMessagesControllerComponents(),
      view
    )
  }

  "LocalReferenceNumberController" - {

    "pre-draft" - {

      ".onPageLoad()" - {

        "when the Destination Type Page answer exists in session" - {

          "when the Deferred Movement answer exists in session" - {

            val answersSoFar = emptyUserAnswers
              .set(DestinationTypePage, GbTaxWarehouse)
              .set(DeferredMovementPage(), false)

            "must return OK and the correct view for a GET" in new Fixture(userAnswers = Some(answersSoFar)) {
              val result = controller.onPreDraftPageLoad(testErn, NormalMode)(request)

              status(result) mustEqual OK
              contentAsString(result) mustEqual
                view(isDeferred = false, form, localReferenceNumberPreDraftSubmitRoute)(dataRequest(request, userAnswers.get), messages(request)).toString
            }
          }

          "when the Deferred Movement answer DOES NOT exist in session" - {

            val answersSoFar = emptyUserAnswers
              .set(DestinationTypePage, UnknownDestination)

            "must return SEE_OTHER and redirect to the Deferred Movement page" in new Fixture(userAnswers = Some(answersSoFar)) {
              val result = controller.onPreDraftPageLoad(testErn, NormalMode)(request)

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(testErn, NormalMode).url)
            }
          }
        }

        "when the Destination Type Page answer does not exist in session" - {

          val answersSoFar = emptyUserAnswers

          "must return SEE_OTHER and redirect to the Destination Type page" in new Fixture(userAnswers = Some(answersSoFar)) {
            val result = controller.onPreDraftPageLoad(testErn, NormalMode)(request)

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(testErn, NormalMode).url)
          }
        }
      }

      ".onSubmit()" - {

        "when the Destination Type Page answer exists" - {

          "when the Deferred Movement answer exists" - {

            val answersSoFar =
              emptyUserAnswers
                .set(DestinationTypePage, GbTaxWarehouse)
                .set(DeferredMovementPage(), false)

            "must redirect to the next page when valid data is submitted" in new Fixture(Some(answersSoFar)) {
              val expectedSavedAnswers = answersSoFar.set(LocalReferenceNumberPage(), testLrn)

              MockPreDraftService.set(expectedSavedAnswers).returns(Future.successful(true))

              val result = controller.onPreDraftSubmit(testErn, NormalMode)(request.withFormUrlEncodedBody(("value", testLrn)))

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual testOnwardRoute.url
            }

            "must return a Bad Request and errors when invalid data is submitted" in
              new Fixture(
                userAnswers = Some(
                  emptyUserAnswers
                    .set(DestinationTypePage, UnknownDestination)
                    .set(DeferredMovementPage(), false)
                )) {

                val boundForm = form.bind(Map("value" -> ""))
                val result = controller.onPreDraftSubmit(testErn, NormalMode)(request.withFormUrlEncodedBody(("value", "")))

                status(result) mustEqual BAD_REQUEST
                contentAsString(result) mustEqual
                  view(isDeferred = false, boundForm, localReferenceNumberPreDraftSubmitRoute)(dataRequest(request, userAnswers.get), messages(request)).toString
              }
          }


          "when the Deferred Movement answer DOES NOT exist in session" - {

            "must return SEE_OTHER and redirect to the Deferred Movement page" in
              new Fixture(
                userAnswers = Some(
                  emptyUserAnswers
                    .set(DestinationTypePage, UnknownDestination)
                )) {
                val result = controller.onPreDraftSubmit(testErn, NormalMode)(request.withFormUrlEncodedBody(("value", testLrn)))

                status(result) mustEqual SEE_OTHER
                redirectLocation(result) mustBe Some(controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(testErn, NormalMode).url)
              }
          }

        }

        "when the Destination Type Page answer does not exist in session" - {

          "must return SEE_OTHER and redirect to the Destination Type page" in new Fixture() {
            val result = controller.onPreDraftSubmit(testErn, NormalMode)(request.withFormUrlEncodedBody(("value", testLrn)))

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(testErn, NormalMode).url)
          }
        }

        "when the Destination Type Page answer exists in session but is an invalid value" - {

          "must return SEE_OTHER and redirect to the Destination Type page" in new Fixture() {
            val result = controller.onPreDraftSubmit(testErn, NormalMode)(request.withFormUrlEncodedBody(("value", testLrn)))

            status(result) mustEqual SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(testErn, NormalMode).url)
          }

        }
      }
    }

    "post-draft" - {

      ".onPageLoad()" - {

        "when the Destination Type Page answer exists in session" - {

          "when the Deferred Movement answer exists in session" - {

            val answersSoFar = emptyUserAnswers
              .set(DestinationTypePage, GbTaxWarehouse)
              .set(DeferredMovementPage(), false)

            "must return OK and the correct view for a GET" in new Fixture(userAnswers = Some(answersSoFar)) {
              val result = controller.onPageLoad(testErn, testDraftId)(request)

              status(result) mustEqual OK
              contentAsString(result) mustEqual
                view(isDeferred = false, form, localReferenceNumberSubmitRoute)(dataRequest(request, userAnswers.get), messages(request)).toString
            }
          }

          "when the Deferred Movement answer DOES NOT exist in session" - {

            val answersSoFar = emptyUserAnswers
              .set(DestinationTypePage, UnknownDestination)

            "must return SEE_OTHER and redirect to the Deferred Movement page" in new Fixture(userAnswers = Some(answersSoFar)) {
              val result = controller.onPageLoad(testErn, testDraftId)(request)

              status(result) mustEqual SEE_OTHER
              redirectLocation(result) mustBe Some(controllers.sections.info.routes.DeferredMovementController.onPageLoad(testErn, testDraftId).url)
            }
          }
        }
      }

      ".onSubmit()" - {

        "when the Destination Type Page answer exists" - {

          "when the Deferred Movement answer exists" - {

            val answersSoFar =
              emptyUserAnswers
                .set(DestinationTypePage, GbTaxWarehouse)
                .set(DeferredMovementPage(), false)

            "must redirect to the next page when valid data is submitted" in new Fixture(Some(answersSoFar)) {
              val expectedSavedAnswers = answersSoFar.set(LocalReferenceNumberPage(), testLrn)

              MockUserAnswersService.set(expectedSavedAnswers).returns(Future.successful(emptyUserAnswers))

              val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", testLrn)))

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual testOnwardRoute.url
            }

            "must return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(
              emptyUserAnswers
                .set(DestinationTypePage, UnknownDestination)
                .set(DeferredMovementPage(), false)
            )) {
              val boundForm = form.bind(Map("value" -> ""))
              val result = controller.onSubmit(testErn, testDraftId)(request.withFormUrlEncodedBody(("value", "")))

              status(result) mustEqual BAD_REQUEST
              contentAsString(result) mustEqual
                view(isDeferred = false, boundForm, localReferenceNumberSubmitRoute)(dataRequest(request), messages(request)).toString
            }
          }
        }
      }
    }
  }
}
