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

package controllers.sections.importInformation

import controllers.BaseNavigationController
import controllers.actions._
import forms.sections.importInformation.ImportCustomsOfficeCodeFormProvider
import models.requests.DataRequest
import models.{GreatBritainRegisteredConsignor, Mode, NorthernIrelandRegisteredConsignor, UserType}
import navigation.ImportInformationNavigator
import pages.sections.importInformation.ImportCustomsOfficeCodePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.importInformation.ImportCustomsOfficeCodeView

import javax.inject.Inject
import scala.concurrent.Future

class ImportCustomsOfficeCodeController @Inject()(override val messagesApi: MessagesApi,
                                                  override val auth: AuthAction,
                                                  override val userAllowList: UserAllowListAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  override val navigator: ImportInformationNavigator,
                                                  override val userAnswersService: UserAnswersService,
                                                  formProvider: ImportCustomsOfficeCodeFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: ImportCustomsOfficeCodeView
                                                 ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) {
      implicit request =>
        getValidUserType match {
          case Some(user) =>
            renderView(Ok, fillForm(ImportCustomsOfficeCodePage, formProvider()), mode, user)
          case _ =>
            redirectToTaskList()
        }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) {
      implicit request =>
        getValidUserType match {
          case Some(user) =>
            formProvider().bindFromRequest().fold(
              formWithErrors =>
                renderView(BadRequest, formWithErrors, mode, user)
              ,
              customsOfficeCode =>
                saveAndRedirect(ImportCustomsOfficeCodePage, customsOfficeCode, mode)
            )
          case _ =>
            redirectToTaskList()
        }
    }

  private def renderView(status: Status, form: Form[_], mode: Mode, user: UserType)(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form = form,
      action = routes.ImportCustomsOfficeCodeController.onSubmit(request.ern, request.arc, mode),
      userType = user
    )))

  private def redirectToTaskList()(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(Redirect(controllers.routes.DraftMovementController.onPageLoad(request.ern, request.arc)))


  private def getValidUserType(implicit request: DataRequest[_]): Option[UserType] =
    if (request.isRegisteredConsignor && request.isNorthernIrelandErn) {
      Some(NorthernIrelandRegisteredConsignor)
    }
    else if (request.isRegisteredConsignor) {
      Some(GreatBritainRegisteredConsignor)
    }
    else {
      None
    }

}
