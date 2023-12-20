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

package controllers.sections.documents

import controllers.actions._
import forms.sections.documents.DocumentReferenceFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.DocumentsNavigator
import pages.sections.documents.DocumentReferencePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.documents.DocumentReferenceView

import javax.inject.Inject
import scala.concurrent.Future

class DocumentReferenceController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val navigator: DocumentsNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             override val userAllowList: UserAllowListAction,
                                             formProvider: DocumentReferenceFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: DocumentReferenceView
                                           ) extends BaseDocumentsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        Future(renderView(Ok, fillForm(DocumentReferencePage(idx), formProvider()), idx, mode))
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            Future(renderView(BadRequest, formWithErrors, idx, mode)),
          saveAndRedirect(DocumentReferencePage(idx), _, mode)
        )
      }
    }

  def renderView(status: Status, form: Form[_], idx: Index, mode: Mode)(implicit request: DataRequest[_]): Result =
    status(view(
      form = form,
      onSubmitCall = controllers.sections.documents.routes.DocumentReferenceController.onSubmit(request.ern, request.arc, idx, mode)
    ))
}
