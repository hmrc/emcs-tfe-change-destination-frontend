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

import config.AppConfig
import controllers.actions._
import handlers.ErrorHandler
import models.NormalMode
import models.requests.DataRequest
import models.response.MissingMandatoryPage
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
import scala.util.{Failure, Success, Try}

class DeclarationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val auth: AuthAction,
                                       override val userAllowList: UserAllowListAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       val userAnswersService: UserAnswersService,
                                       val navigator: Navigator,
                                       service: SubmitChangeDestinationService,
                                       view: DeclarationView,
                                       errorHandler: ErrorHandler
                                     )(implicit appConfig: AppConfig) extends BaseNavigationController with I18nSupport with AuthActionHelper with Logging {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withSubmitChangeDestinationModel { _ =>
        Future.successful(Ok(view(submitAction = routes.DeclarationController.onSubmit(ern, arc))))
      }
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      withSubmitChangeDestinationModel { submitChangeDestinationModel =>
        service.submit(submitChangeDestinationModel).flatMap {
          response =>
            logger.debug(s"[onSubmit] response received from downstream service ${response.downstreamService}: ${response.receipt}")

            saveAndRedirect(DeclarationPage, LocalDateTime.now(), NormalMode)
        }.recover {
          case exception =>
            logger.error(s"Error thrown when calling Submit Change Destination: ${exception.getMessage}")
            InternalServerError(errorHandler.internalServerErrorTemplate)
        }
      }
    }

  private def withSubmitChangeDestinationModel(onSuccess: SubmitChangeDestinationModel => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    Try(SubmitChangeDestinationModel.apply) match {
      case Failure(exception: MissingMandatoryPage) =>
        logger.warn(s"[withSubmitChangeDestinationModel] MissingMandatoryPage error thrown: ${exception.message}")
        Future.successful(Redirect(routes.DraftMovementController.onPageLoad(request.ern, request.arc)))

      case Failure(exception) =>
        logger.error(s"[withSubmitChangeDestinationModel]Error thrown when creating request model to submit: ${exception.getMessage}")
        Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))

      case Success(submitChangeDestinationModel) => onSuccess(submitChangeDestinationModel)
    }
}
