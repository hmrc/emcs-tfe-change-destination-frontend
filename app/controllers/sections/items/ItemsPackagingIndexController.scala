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

import controllers.actions._
import models.{Index, NormalMode}
import navigation.ItemsNavigator
import pages.sections.items.ItemsPackagingSection
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService

import javax.inject.Inject

class ItemsPackagingIndexController @Inject()(
                                      override val userAnswersService: UserAnswersService,
                                      override val navigator: ItemsNavigator,
                                      override val auth: AuthAction,
                                      override val getData: DataRetrievalAction,
                                      override val requireData: DataRequiredAction,
                                      override val userAllowList: UserAllowListAction,
                                      val controllerComponents: MessagesControllerComponents
                                    ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, itemsIndex: Index): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      validateIndex(itemsIndex) {
        if (ItemsPackagingSection(itemsIndex).isCompleted) {
          Redirect(routes.ItemsPackagingAddToListController.onPageLoad(ern, arc, itemsIndex))
        } else {
          Redirect(controllers.sections.items.routes.ItemSelectPackagingController.onPageLoad(request.ern, request.arc,
            itemsIndex, Index(0), NormalMode))
        }
      }
    }

}
