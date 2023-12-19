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

package controllers.sections.info

import controllers.BaseController
import controllers.actions._
import models.{NormalMode, NorthernIrelandWarehouseKeeper}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}

import javax.inject.Inject

class InfoIndexController @Inject()(val userAllowList: UserAllowListAction,
                                    val getData: DataRetrievalAction,
                                    val requireData: DataRequiredAction,
                                    val auth: AuthAction,
                                    val controllerComponents: MessagesControllerComponents) extends BaseController with AuthActionHelper {

  def onPreDraftPageLoad(ern: String): Action[AnyContent] =
    (auth(ern) andThen userAllowList) { implicit request =>
      if (request.userTypeFromErn == NorthernIrelandWarehouseKeeper) {
        Redirect(controllers.sections.info.routes.DispatchPlaceController.onPreDraftPageLoad(ern, NormalMode))
      } else {
        Redirect(controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(ern, NormalMode))
      }
    }

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { _ =>
      Redirect(controllers.sections.info.routes.InformationCheckAnswersController.onPageLoad(ern, arc))
    }

}
