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

import controllers.BaseNavigationController
import controllers.actions._
import models.NormalMode
import models.requests.DataRequest
import navigation.DocumentsNavigator
import pages.sections.documents.DocumentsCertificatesPage
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.DocumentsCount
import services.UserAnswersService

import javax.inject.Inject

class DocumentsIndexController @Inject()(
                                          override val userAnswersService: UserAnswersService,
                                          override val navigator: DocumentsNavigator,
                                          override val auth: AuthAction,
                                          override val getData: DataRetrievalAction,
                                          override val requireData: DataRequiredAction,
                                          override val userAllowList: UserAllowListAction,
                                          val controllerComponents: MessagesControllerComponents
                                        ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      request.userAnswers.get(DocumentsCertificatesPage) match {
        case Some(false) => Redirect(routes.DocumentsCheckAnswersController.onPageLoad(ern, arc))
        case Some(true) => hasDocumentsRouting()
        case _ => Redirect(routes.DocumentsCertificatesController.onPageLoad(ern, arc, NormalMode))
      }
    }

  private def hasDocumentsRouting()(implicit request: DataRequest[_]): Result = {
    request.userAnswers.get(DocumentsCount) match {
      case None | Some(0) =>
        Redirect(routes.DocumentsCertificatesController.onPageLoad(request.ern, request.arc, NormalMode))
      case Some(_) =>
        Redirect(routes.DocumentsAddToListController.onPageLoad(request.ern, request.arc))
    }
  }
}
