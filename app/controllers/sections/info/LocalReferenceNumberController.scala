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

import controllers.BasePreDraftNavigationController
import controllers.actions._
import controllers.actions.predraft.{PreDraftAuthActionHelper, PreDraftDataRequiredAction, PreDraftDataRetrievalAction}
import forms.sections.info.LocalReferenceNumberFormProvider
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import models.{CheckMode, Mode}
import navigation.InformationNavigator
import pages.sections.info.LocalReferenceNumberPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.{PreDraftService, UserAnswersService}
import views.html.sections.info.LocalReferenceNumberView

import javax.inject.Inject
import scala.concurrent.Future

class LocalReferenceNumberController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                val preDraftService: PreDraftService,
                                                val navigator: InformationNavigator,
                                                val auth: AuthAction,
                                                val getPreDraftData: PreDraftDataRetrievalAction,
                                                val requirePreDraftData: PreDraftDataRequiredAction,
                                                val getData: DataRetrievalAction,
                                                val requireData: DataRequiredAction,
                                                val userAllowList: UserAllowListAction,
                                                formProvider: LocalReferenceNumberFormProvider,
                                                val userAnswersService: UserAnswersService,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: LocalReferenceNumberView
                                              ) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {


  def onPreDraftPageLoad(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      withGuard(isOnPreDraftFlow = true) {
        case (_, isDeferred) =>
          renderView(
            Ok,
            fillForm(LocalReferenceNumberPage(isOnPreDraftFlow = true), formProvider(isDeferred)),
            isDeferred,
            controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftSubmit(request.ern, mode)
          )
      }
    }

  def onPreDraftSubmit(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      withGuard(isOnPreDraftFlow = true) {
        case (_, isDeferred) =>
          formProvider(isDeferred).bindFromRequest().fold(
            formWithErrors =>
              renderView(
                BadRequest,
                formWithErrors,
                isDeferred,
                controllers.sections.info.routes.LocalReferenceNumberController.onPreDraftSubmit(request.ern, mode)
              ),
            value =>
              savePreDraftAndRedirect(LocalReferenceNumberPage(isOnPreDraftFlow = true), value, mode)
          )
      }
    }


  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withGuard(isOnPreDraftFlow = false) {
        case (_, isDeferred) =>
          renderView(
            Ok,
            fillForm(LocalReferenceNumberPage(isOnPreDraftFlow = false), formProvider(isDeferred)),
            isDeferred,
            controllers.sections.info.routes.LocalReferenceNumberController.onSubmit(request.ern, arc)
          )
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withGuard(isOnPreDraftFlow = false) {
        case (_, isDeferred) =>
          formProvider(isDeferred).bindFromRequest().fold(
            formWithErrors =>
              renderView(
                BadRequest,
                formWithErrors,
                isDeferred,
                controllers.sections.info.routes.LocalReferenceNumberController.onSubmit(request.ern, arc)
              ),
            value =>
              saveAndRedirect(LocalReferenceNumberPage(isOnPreDraftFlow = false), value, CheckMode)
          )
      }
    }

  private def renderView(status: Status, form: Form[_], isDeferred: Boolean, onSubmitCall: Call)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(isDeferred, form, onSubmitCall)))

  private def withGuard(isOnPreDraftFlow: Boolean)(f: (MovementScenario, Boolean) => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    withDestinationTypePageAnswer { destinationTypePageAnswer =>
      withDeferredMovementAnswer(isOnPreDraftFlow = isOnPreDraftFlow) { isDeferred =>
        f(destinationTypePageAnswer, isDeferred)
      }
    }
  }

}
