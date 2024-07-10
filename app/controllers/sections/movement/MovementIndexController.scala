/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.sections.movement

import controllers.BaseController
import controllers.actions._
import models.sections.ReviewAnswer.KeepAnswers
import pages.sections.movement.{MovementReviewPage, MovementSection}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}

import javax.inject.Inject

class MovementIndexController @Inject()(
                                         override val auth: AuthAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         override val withMovement: MovementAction,
                                         override val betaAllowList: BetaAllowListAction,
                                         val controllerComponents: MessagesControllerComponents
                                       ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovement(ern, arc) { implicit request =>
      if (MovementSection.needsReview || MovementReviewPage.value.contains(KeepAnswers)) {
        Redirect(controllers.sections.movement.routes.MovementReviewAnswersController.onPageLoad(ern, arc))
      } else {
        Redirect(controllers.sections.movement.routes.MovementCheckAnswersController.onPageLoad(ern, arc))
      }
    }
}