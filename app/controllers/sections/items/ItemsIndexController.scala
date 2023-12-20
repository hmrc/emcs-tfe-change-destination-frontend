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

package controllers.sections.items

import controllers.BaseNavigationController
import controllers.actions._
import models.{Index, NormalMode}
import navigation.ItemsNavigator
import pages.sections.items.ItemsSectionItems
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.ItemsCount
import services.UserAnswersService

import javax.inject.Inject

class ItemsIndexController @Inject()(
                                      override val userAnswersService: UserAnswersService,
                                      override val navigator: ItemsNavigator,
                                      override val auth: AuthAction,
                                      override val getData: DataRetrievalAction,
                                      override val requireData: DataRequiredAction,
                                      override val userAllowList: UserAllowListAction,
                                      val controllerComponents: MessagesControllerComponents
                                    ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      request.userAnswers.get(ItemsCount) match {
        case Some(value) if value > 1 || ItemsSectionItems.isCompleted =>
          Redirect(routes.ItemsAddToListController.onPageLoad(request.ern, request.arc))
        case _ =>
          Redirect(routes.ItemExciseProductCodeController.onPageLoad(request.ern, request.arc, Index(0), NormalMode))
      }
    }
}
