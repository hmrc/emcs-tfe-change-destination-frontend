package controllers

import controllers.BaseNavigationController
import controllers.actions._
import forms.$className$FormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.$className$Page
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.$className$View

import scala.concurrent.Future

class $className$Controller @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val betaAllowList: BetaAllowListAction,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: Navigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       formProvider: $className$FormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: $className$View
                                     ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      Ok(view(fillForm($className$Page, formProvider()), mode))
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          saveAndRedirect($className$Page, value, mode)
      )
    }
}
