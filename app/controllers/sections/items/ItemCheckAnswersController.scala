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
import models.{Index, NormalMode}
import navigation.ItemsNavigator
import pages.sections.items.{ItemCheckAnswersPage, ItemCommodityCodePage, ItemExciseProductCodePage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCnCodeInformationService, UserAnswersService}
import views.html.sections.items.ItemCheckAnswersView

import javax.inject.Inject
import scala.concurrent.Future

class ItemCheckAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            override val userAnswersService: UserAnswersService,
                                            override val userAllowList: UserAllowListAction,
                                            override val navigator: ItemsNavigator,
                                            override val auth: AuthAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            val getCnCodeInformationService: GetCnCodeInformationService,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: ItemCheckAnswersView
                                          ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index): Action[AnyContent] = authorisedDataRequestAsync(ern, arc) { implicit request =>
    validateIndexAsync(idx) {
      for {
        cnCodeInformationO <- getCnCodeInformation(idx)
      } yield {
        cnCodeInformationO match {
          case Some(cnCodeInformation) => renderView(idx, cnCodeInformation)
          case None => Redirect(routes.ItemsIndexController.onPageLoad(request.ern, request.arc))
        }
      }
    }
  }

  def onSubmit(ern: String, arc: String, idx: Index): Action[AnyContent] = authorisedDataRequest(ern, arc) { implicit request =>
    validateIndex(idx) {
      Redirect(navigator.nextPage(ItemCheckAnswersPage(idx), NormalMode, request.userAnswers))
    }
  }

  private def getCnCodeInformation(idx: Index)(implicit request: DataRequest[_]): Future[Option[CnCodeInformation]] = {
    for {
      epc <- request.userAnswers.get(ItemExciseProductCodePage(idx))
      cnCode <- request.userAnswers.get(ItemCommodityCodePage(idx))
    } yield {
      getCnCodeInformationService.getCnCodeInformation(Seq(CnCodeInformationItem(epc, cnCode))).map {
        case (_, cnCodeInformation) :: Nil =>
          Some(cnCodeInformation)
        case answer =>
          logger.warn(s"[getCnCodeInformation] Unexpected value returned from GetCnCodeInformationService: $answer")
          None
      }
    }
  }.getOrElse({
    logger.warn("[getCnCodeInformation] error retrieving CN Code Information ")
    Future.successful(None)
  })

  private def renderView(idx: Index, cnCodeInformation: CnCodeInformation)(implicit request: DataRequest[_]): Result = Ok(view(
    idx,
    cnCodeInformation,
    routes.ItemCheckAnswersController.onSubmit(request.ern, request.arc, idx)
  ))

}
