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

package controllers.sections.consignee

import controllers.BaseController
import controllers.actions._
import models.NormalMode
import navigation.ConsigneeNavigator
import pages.sections.consignee.{CheckAnswersConsigneePage, ConsigneeAddressPage, ConsigneeBusinessNamePage}
import pages.sections.info.DestinationTypePage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import viewmodels.helpers.CheckYourAnswersConsigneeHelper
import views.html.sections.consignee.CheckYourAnswersConsigneeView

import javax.inject.Inject

class CheckYourAnswersConsigneeController @Inject()(override val messagesApi: MessagesApi,
                                                    override val auth: AuthAction,
                                                    override val betaAllowList: BetaAllowListAction,
                                                    override val getData: DataRetrievalAction,
                                                    override val requireData: DataRequiredAction,
                                                    override val withMovement: MovementAction,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    val navigator: ConsigneeNavigator,
                                                    val checkYourAnswersConsigneeHelper: CheckYourAnswersConsigneeHelper,
                                                    view: CheckYourAnswersConsigneeView
                                                   ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) {
      implicit request =>
        withAnswer(ConsigneeBusinessNamePage, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, arc)) {
          _ =>
            withAnswer(ConsigneeAddressPage, controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, arc)) {
              _ =>
                withAnswer(DestinationTypePage) {
                  _ =>
                    Ok(view(
                      controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(ern, arc),
                      ern,
                      arc,
                      checkYourAnswersConsigneeHelper.summaryList
                    ))
                }
            }
        }
    }


  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) {
      implicit request =>
        Redirect(navigator.nextPage(CheckAnswersConsigneePage, NormalMode, request.userAnswers))
    }

}
