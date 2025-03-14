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

package controllers

import controllers.actions._
import handlers.ErrorHandler
import models.NormalMode
import models.requests.DataRequest
import models.response.{SubmitChangeDestinationException, UnexpectedDownstreamSubmissionResponseError}
import models.submitChangeDestination.SubmitChangeDestinationModel
import navigation.Navigator
import pages.DeclarationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{SubmitChangeDestinationService, UserAnswersService}
import utils.Logging
import views.html.DeclarationView

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.Future
import scala.util.Try

class DeclarationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val withMovement: MovementAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       val userAnswersService: UserAnswersService,
                                       val navigator: Navigator,
                                       service: SubmitChangeDestinationService,
                                       view: DeclarationView,
                                       errorHandler: ErrorHandler
                                     ) extends BaseNavigationController with I18nSupport with AuthActionHelper with Logging {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      Ok(view(submitAction = routes.DeclarationController.onSubmit(ern, arc)))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withSubmitChangeDestinationModel { submitChangeDestinationModel =>
        service.submit(submitChangeDestinationModel, ern).flatMap {
          case Right(response) =>
            logger.debug(s"[onSubmit] response received from downstream service ${response.downstreamService}: ${response.receipt}")

            saveAndRedirect(DeclarationPage, LocalDateTime.now(), NormalMode)
          case Left(UnexpectedDownstreamSubmissionResponseError(UNPROCESSABLE_ENTITY)) =>
            // emcs-tfe returns an UNPROCESSABLE_ENTITY when downstream responds with RIM validation errors,
            // so we redirect to TaskListController to display these errors
            logger.warn(s"Received UnexpectedDownstreamDraftSubmissionResponseError(UNPROCESSABLE_ENTITY) from SubmitChangeDestinationService, " +
              s"redirecting to TaskListController")
            Future.successful(Redirect(routes.TaskListController.onPageLoad(ern, arc)))
          case Left(value) =>
            logger.warn(s"Received Left from SubmitChangeDestinationService: $value")
            throw SubmitChangeDestinationException(s"Failed to submit Change Destination to emcs-tfe for ern: '${request.ern}' & ARC: '${request.arc}'")
        }.recoverWith {
          case exception =>
            logger.error(s"Error thrown when calling Submit Change Destination: ${exception.getMessage}")
            errorHandler.internalServerErrorTemplate.map(InternalServerError(_))
        }
      }
    }

  private def withSubmitChangeDestinationModel(onSuccess: SubmitChangeDestinationModel => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    Try(SubmitChangeDestinationModel.apply).fold(exception => {
      logger.warn(s"[withSubmitChangeDestinationModel] An error was thrown constructing a submission model: ${exception.getMessage}")
      Future.successful(Redirect(routes.TaskListController.onPageLoad(request.ern, request.arc)))
    },
      onSuccess(_)
    )
}
