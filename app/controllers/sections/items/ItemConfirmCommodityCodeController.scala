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
import models.requests.{CnCodeInformationItem, DataRequest}
import models.response.referenceData.CnCodeInformation
import models.{Index, NormalMode, ReviewMode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemConfirmCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCnCodeInformationService, UserAnswersService}
import viewmodels.helpers.ItemConfirmCommodityCodeHelper
import views.html.sections.items.ItemConfirmCommodityCodeView

import javax.inject.Inject
import scala.concurrent.Future

class ItemConfirmCommodityCodeController @Inject()(override val messagesApi: MessagesApi,
                                                   override val auth: AuthAction,
                                                   override val userAllowList: UserAllowListAction,
                                                   override val getData: DataRetrievalAction,
                                                   override val userAnswersService: UserAnswersService,
                                                   override val requireData: DataRequiredAction,
                                                   val cnCodeInformationService: GetCnCodeInformationService,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   val navigator: ItemsNavigator,
                                                   val itemConfirmCommodityCodeHelper: ItemConfirmCommodityCodeHelper,
                                                   view: ItemConfirmCommodityCodeView
                                              ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) {
      implicit request =>
        validateIndexAsync(idx) {
          withCnCodeInformation(idx) {
            cnCodeInformation =>
              Ok(view(
                routes.ItemConfirmCommodityCodeController.onSubmit(request.ern, request.arc, idx),
                itemConfirmCommodityCodeHelper.summaryList(idx, cnCodeInformation, ReviewMode)
              ))
          }
        }
    }


  def onSubmit(ern: String, arc: String, idx: Index): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) {
      implicit request =>
        validateIndexAsync(idx) {
          Future.successful(Redirect(navigator.nextPage(ItemConfirmCommodityCodePage(idx), NormalMode, request.userAnswers)))
        }
    }

  private[controllers] def withCnCodeInformation(idx: Index)(f: CnCodeInformation => Result)(implicit request: DataRequest[_]): Future[Result] = {
    (request.userAnswers.get(ItemExciseProductCodePage(idx)), request.userAnswers.get(ItemCommodityCodePage(idx))) match {
      case (Some(epc), Some(commodityCode)) =>
        cnCodeInformationService.getCnCodeInformation(Seq(CnCodeInformationItem(epc, commodityCode))).map { response =>
          response.headOption match {
            case Some((_, cnCodeInfo)) =>
              f(cnCodeInfo)
            case _ =>
              logger.warn(s"[withCnCodeInformation] Could not retrieve CnCodeInformation for item productCode: '$epc' and commodityCode: '$commodityCode'")
              Redirect(controllers.routes.JourneyRecoveryController.onPageLoad())
          }
        }
      case _ =>
        logger.warn(s"[withCnCodeInformation] productCode or commodityCode missing from UserAnswers")
        Future.successful(Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.arc)))
    }
  }
}
