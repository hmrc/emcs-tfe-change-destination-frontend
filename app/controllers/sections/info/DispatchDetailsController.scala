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
import forms.sections.info.DispatchDetailsFormProvider
import models.requests.DataRequest
import models.{CheckMode, Mode}
import navigation.InformationNavigator
import pages.sections.info.{DeferredMovementPage, DispatchDetailsPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import services.{PreDraftService, UserAnswersService}
import views.html.sections.info.DispatchDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class DispatchDetailsController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           val preDraftService: PreDraftService,
                                           val navigator: InformationNavigator,
                                           val auth: AuthAction,
                                           val getPreDraftData: PreDraftDataRetrievalAction,
                                           val requirePreDraftData: PreDraftDataRequiredAction,
                                           val getData: DataRetrievalAction,
                                           val requireData: DataRequiredAction,
                                           formProvider: DispatchDetailsFormProvider,
                                           val userAnswersService: UserAnswersService,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: DispatchDetailsView,
                                           val userAllowList: UserAllowListAction
                                         ) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {

  def onPreDraftPageLoad(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      renderViewPreDraft(
        Ok,
        fillForm(DispatchDetailsPage(), formProvider()),
        controllers.sections.info.routes.DispatchDetailsController.onPreDraftSubmit(request.ern, mode),
        mode
      )
    }


  def onPreDraftSubmit(ern: String, mode: Mode): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderViewPreDraft(
            BadRequest,
            formWithErrors,
            controllers.sections.info.routes.DispatchDetailsController.onPreDraftSubmit(request.ern, mode),
            mode
          ),
        value =>
          savePreDraftAndRedirect(DispatchDetailsPage(), value, mode)
      )
    }

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      renderView(
        Ok,
        fillForm(DispatchDetailsPage(isOnPreDraftFlow = false), formProvider()),
        controllers.sections.info.routes.DispatchDetailsController.onSubmit(request.ern, arc),
        CheckMode
      )
    }


  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          renderView(
            BadRequest,
            formWithErrors,
            controllers.sections.info.routes.DispatchDetailsController.onSubmit(request.ern, arc),
            CheckMode
          ),
        value =>
          saveAndRedirect(DispatchDetailsPage(isOnPreDraftFlow = false), value, CheckMode)
      )
    }

  def renderViewPreDraft(status: Status, form: Form[_], onSubmitCall: Call, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {
    withAnswerAsync(
      page = DeferredMovementPage(),
      redirectRoute = controllers.sections.info.routes.DeferredMovementController.onPreDraftPageLoad(request.ern, mode)
    ) { deferredMovement =>
      Future.successful(
        status(view(
          form = form,
          deferredMovement = deferredMovement,
          onSubmitCall = onSubmitCall,
          skipQuestionCall = navigator.nextPage(DispatchDetailsPage(), mode, request.userAnswers)
        ))
      )
    }
  }

  def renderView(status: Status, form: Form[_], onSubmitCall: Call, mode: Mode)
                (implicit request: DataRequest[_]): Future[Result] = {
    withAnswerAsync(
      page = DeferredMovementPage(false)
    ) { deferredMovement =>
      Future.successful(
        status(view(
          form = form,
          deferredMovement = deferredMovement,
          onSubmitCall = onSubmitCall,
          skipQuestionCall = navigator.nextPage(DispatchDetailsPage(isOnPreDraftFlow = false), mode, request.userAnswers)
        ))
      )
    }
  }


}
