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
import forms.sections.items.ItemQuantityFormProvider
import models.requests.{CnCodeInformationItem, DataRequest}
import models.response.referenceData.CnCodeInformation
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemCommodityCodePage, ItemExciseProductCodePage, ItemQuantityPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCnCodeInformationService, UserAnswersService}
import views.html.sections.items.ItemQuantityView

import javax.inject.Inject
import scala.concurrent.Future

class ItemQuantityController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        override val userAnswersService: UserAnswersService,
                                        override val navigator: ItemsNavigator,
                                        override val auth: AuthAction,
                                        override val getData: DataRetrievalAction,
                                        override val requireData: DataRequiredAction,
                                        override val userAllowList: UserAllowListAction,
                                        formProvider: ItemQuantityFormProvider,
                                        override val controllerComponents: MessagesControllerComponents,
                                        view: ItemQuantityView,
                                        val cnCodeInformationService: GetCnCodeInformationService
                                      ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        renderView(Ok, fillForm(ItemQuantityPage(idx), formProvider()), idx, mode)
      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        formProvider().bindFromRequest().fold(
          renderView(BadRequest, _, idx, mode),
          saveAndRedirect(ItemQuantityPage(idx), _, mode)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], idx: Index, mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    withGoodsTypeAsync(idx) { goodsType =>
      withCnCodeInformation(idx) {
        cnCodeInfo =>
          status(view(
            form = form,
            action = routes.ItemQuantityController.onSubmit(request.ern, request.arc, idx, mode),
            goodsType = goodsType,
            unitOfMeasure = cnCodeInfo.unitOfMeasure
          ))
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
