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

package controllers.sections.sad

import controllers.actions._
import forms.sections.sad.SadRemoveDocumentFormProvider
import models.Index
import models.requests.DataRequest
import navigation.SadNavigator
import pages.sections.sad.SadSectionItem
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.sad.SadRemoveDocumentView

import javax.inject.Inject
import scala.concurrent.Future

class SadRemoveDocumentController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             override val userAnswersService: UserAnswersService,
                                             override val userAllowList: UserAllowListAction,
                                             override val navigator: SadNavigator,
                                             override val auth: AuthAction,
                                             override val getData: DataRetrievalAction,
                                             override val requireData: DataRequiredAction,
                                             formProvider: SadRemoveDocumentFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: SadRemoveDocumentView
                                           ) extends BaseSadNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        renderView(Ok, formProvider(), idx)
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndex(idx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, idx),
          handleAnswerRemovalAndRedirect(_, idx)(ern, arc)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], idx: Index)
                        (implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(
      status(view(
        form = form,
        indexOfSad = idx
      ))
    )
  }

  private def handleAnswerRemovalAndRedirect(shouldRemoveSadDocument: Boolean, index: Index)(ern: String, arc: String)
                                            (implicit request: DataRequest[_]): Future[Result] = {
    if (shouldRemoveSadDocument) {
      val cleansedAnswers = request.userAnswers.remove(SadSectionItem(index))
      userAnswersService.set(cleansedAnswers).map {
        _ => Redirect(controllers.sections.sad.routes.SadIndexController.onPageLoad(ern, arc))
      }
    } else {
      Future(Redirect(controllers.sections.sad.routes.SadAddToListController.onPageLoad(ern, arc)))
    }
  }
}
