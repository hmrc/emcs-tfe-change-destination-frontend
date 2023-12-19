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
import forms.sections.documents.DocumentTypeFormProvider
import models.requests.DataRequest
import models.sections.documents.DocumentType
import models.{Index, Mode, NormalMode}
import navigation.DocumentsNavigator
import pages.sections.documents._
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.DocumentsCount
import services.{GetDocumentTypesService, UserAnswersService}
import viewmodels.helpers.SelectItemHelper
import views.html.sections.documents.DocumentTypeView

import javax.inject.Inject
import scala.concurrent.Future

class DocumentTypeController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        override val userAnswersService: UserAnswersService,
                                        override val userAllowList: UserAllowListAction,
                                        override val navigator: DocumentsNavigator,
                                        override val auth: AuthAction,
                                        override val getData: DataRetrievalAction,
                                        override val requireData: DataRequiredAction,
                                        getDocumentTypesService: GetDocumentTypesService,
                                        formProvider: DocumentTypeFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: DocumentTypeView
                                      ) extends BaseDocumentsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        getDocumentTypesService.getDocumentTypes().flatMap { documentTypes =>
          renderView(Ok, fillForm(DocumentTypePage(idx), formProvider(documentTypes)), documentTypes, idx, mode)
        }
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        getDocumentTypesService.getDocumentTypes().flatMap { documentTypes =>
          formProvider(documentTypes).bindFromRequest().fold(
            renderView(BadRequest, _, documentTypes, idx, mode),
            cleanseAndRedirect(_, idx, mode)
          )
        }
      }
    }

  private def renderView(status: Status, form: Form[_], documentTypes: Seq[DocumentType], idx: Index, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] = {

    val selectItems = SelectItemHelper.constructSelectItems(
      selectOptions = documentTypes,
      defaultTextMessageKey = "documentType.select.defaultValue",
      existingAnswer = request.userAnswers.get(DocumentTypePage(idx)).map(_.code)
    )

    Future(status(view(
      form = form,
      onSubmitCall = routes.DocumentTypeController.onSubmit(request.ern, request.arc, idx, mode),
      selectOptions = selectItems
    )))
  }

  private def cleanseAndRedirect(answer: DocumentType, idx: Index, mode: Mode)
                                (implicit request: DataRequest[_]): Future[Result] =
    if (request.userAnswers.get(DocumentTypePage(idx)).contains(answer)) {
      Future(Redirect(navigator.nextPage(DocumentTypePage(idx), mode, request.userAnswers)))
    } else {

      val updatedAnswers = request.userAnswers
        .remove(ReferenceAvailablePage(idx))
        .remove(DocumentReferencePage(idx))
        .remove(DocumentDescriptionPage(idx))

      saveAndRedirect(
        page = DocumentTypePage(idx),
        answer = answer,
        currentAnswers = updatedAnswers,
        mode = NormalMode
      )
    }

  override def validateIndex(idx: Index)(f: => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    validateIndexForJourneyEntry(DocumentsCount, idx, DocumentsSection.MAX)(
      onSuccess = f,
      onFailure = Future.successful(
        Redirect(
          routes.DocumentsIndexController.onPageLoad(request.ern, request.arc)
        )
      )
    )
}
