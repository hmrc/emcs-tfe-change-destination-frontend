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

package controllers.sections.info

import controllers.BasePreDraftNavigationController
import controllers.actions._
import controllers.actions.predraft.{PreDraftAuthActionHelper, PreDraftDataRequiredAction, PreDraftDataRetrievalAction}
import models.sections.info.ChangeType.ChangeConsignee
import models.sections.info.movementScenario.DestinationType.UnknownDestination
import navigation.InformationNavigator
import pages.sections.info.{ChangeDestinationTypePage, ChangeTypePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{PreDraftService, UserAnswersService}

import javax.inject.Inject
import scala.concurrent.Future

class InfoIndexController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     override val userAnswersService: UserAnswersService,
                                     override val userAllowList: UserAllowListAction,
                                     override val navigator: InformationNavigator,
                                     override val auth: AuthAction,
                                     override val preDraftService: PreDraftService,
                                     override val getPreDraftData: PreDraftDataRetrievalAction,
                                     override val requirePreDraftData: PreDraftDataRequiredAction,
                                     override val withMovement: MovementAction,
                                     val controllerComponents: MessagesControllerComponents
                                   ) extends BasePreDraftNavigationController with PreDraftAuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovementAsync(ern, arc) { implicit request =>
      if (request.movementDetails.destinationType == UnknownDestination) {
        preDraftService.set(request.userAnswers
          .set(ChangeTypePage, ChangeConsignee)
          .set(ChangeDestinationTypePage, true)
        ).map { _ =>
          Redirect(routes.NewDestinationTypeController.onPreDraftPageLoad(ern, arc))
        }
      } else {
        Future.successful(Redirect(routes.ChangeTypeController.onPageLoad(ern, arc)))
      }
    }
}
