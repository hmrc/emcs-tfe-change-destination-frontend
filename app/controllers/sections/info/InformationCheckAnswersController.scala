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
import models.NormalMode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import navigation.InformationNavigator
import pages.sections.info.{DeferredMovementPage, InformationCheckAnswersPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{PreDraftService, UserAnswersService}
import viewmodels.checkAnswers.sections.info.InformationCheckAnswersHelper
import views.html.sections.info.InformationCheckAnswersView

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

class InformationCheckAnswersController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   val preDraftService: PreDraftService,
                                                   val userAnswersService: UserAnswersService,
                                                   val navigator: InformationNavigator,
                                                   val auth: AuthAction,
                                                   val getPreDraftData: PreDraftDataRetrievalAction,
                                                   val requirePreDraftData: PreDraftDataRequiredAction,
                                                   val getData: DataRetrievalAction,
                                                   val requireData: DataRequiredAction,
                                                   val userAllowList: UserAllowListAction,
                                                   val cyaHelper: InformationCheckAnswersHelper,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: InformationCheckAnswersView
                                                 ) extends BasePreDraftNavigationController with AuthActionHelper with PreDraftAuthActionHelper {

  def onPreDraftPageLoad(ern: String): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      withGuard(isOnPreDraftFlow = true) {
        case (_, deferredMovement) =>
          Future.successful(
            Ok(view(
              cyaHelper.summaryList(deferredMovement),
              controllers.sections.info.routes.InformationCheckAnswersController.onPreDraftSubmit(ern)
            ))
          )
      }
    }

  def onPreDraftSubmit(ern: String): Action[AnyContent] =
    authorisedPreDraftDataRequestAsync(ern) { implicit request =>
      createDraftEntryAndRedirect()
    }

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      withAnswer(DeferredMovementPage(false)) {
        deferredMovement =>
          Ok(view(
            cyaHelper.summaryList(deferredMovement),
            controllers.sections.info.routes.InformationCheckAnswersController.onSubmit(ern, arc)
          ))
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      Redirect(navigator.nextPage(InformationCheckAnswersPage, NormalMode, request.userAnswers))
    }

  private def withGuard(isOnPreDraftFlow: Boolean)(f: (MovementScenario, Boolean) => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    withDestinationTypePageAnswer { destinationTypePageAnswer =>
      withDeferredMovementAnswer(isOnPreDraftFlow = isOnPreDraftFlow) { isDeferred =>
        f(destinationTypePageAnswer, isDeferred)
      }
    }
  }

  private def createDraftEntryAndRedirect()(implicit request: DataRequest[_]): Future[Result] =
    withGuard(isOnPreDraftFlow = true) {
      case (_, _) =>

        val userAnswers = request
          .userAnswers
          .set(InformationCheckAnswersPage, true)
          .copy(arc = UUID.randomUUID().toString)

        for {
          _ <- userAnswersService.set(userAnswers)
          _ <- preDraftService.clear(userAnswers.ern, request.request.sessionId)
        } yield {
          Redirect(navigator.nextPage(InformationCheckAnswersPage, NormalMode, userAnswers))
        }

    }


}
