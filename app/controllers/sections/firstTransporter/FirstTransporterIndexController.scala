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

package controllers.sections.firstTransporter

import controllers.BaseNavigationController
import controllers.actions._
import models.NormalMode
import navigation.FirstTransporterNavigator
import pages.sections.firstTransporter.FirstTransporterSection
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService

import javax.inject.Inject

class FirstTransporterIndexController @Inject()(
                                                 override val userAnswersService: UserAnswersService,
                                                 override val navigator: FirstTransporterNavigator,
                                                 override val auth: AuthAction,
                                                 override val getData: DataRetrievalAction,
                                                 override val requireData: DataRequiredAction,
                                                 override val withMovement: MovementAction,
                                                 override val userAllowList: UserAllowListAction,
                                                 val controllerComponents: MessagesControllerComponents
                                               ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      if (FirstTransporterSection.isCompleted || FirstTransporterSection.needsReview) {
        Redirect(controllers.sections.firstTransporter.routes.FirstTransporterCheckAnswersController.onPageLoad(ern, arc))
      } else {
        Redirect(controllers.sections.firstTransporter.routes.FirstTransporterNameController.onPageLoad(ern, arc, NormalMode))
      }
    }
}
