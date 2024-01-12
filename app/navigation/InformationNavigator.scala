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

package navigation

import controllers.routes
import models._
import models.requests.DataRequest
import models.sections.info.ChangeType.Consignee
import pages._
import pages.sections.info._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}


//TODO: This naviagtion is made up for now, it just allows you to get through a section to the draft movement page
//      The draft movment page will actually become the Review Change of Destination Task List page and this section
//      Will have new pages added to it to determine what type of Change of Destination is happening
@Singleton
class InformationNavigator @Inject()() extends BaseNavigator {

  // noinspection ScalaStyle
  private def normalRoutes(implicit request: DataRequest[_]): Page => UserAnswers => Call = {

    case ChangeTypePage => (userAnswers: UserAnswers) =>
      userAnswers.get(ChangeTypePage) match {
          case Some(Consignee) =>
            controllers.sections.info.routes.ChangeDestinationTypeController.onPageLoad(userAnswers.ern, userAnswers.arc)
          case _ =>
            controllers.routes.DraftMovementController.onPageLoad(request.ern, request.arc)
        }

    case ChangeDestinationTypePage =>
      (userAnswers: UserAnswers) => userAnswers.get(ChangeDestinationTypePage) match {
        case Some(wantsToChangeDestinationType) =>
          if(wantsToChangeDestinationType) {
            controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(userAnswers.ern, userAnswers.arc)
          } else {
            controllers.routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.arc)
          }
        case _ => controllers.sections.info.routes.ChangeDestinationTypeController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }

    case DispatchPlacePage =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(userAnswers.ern, userAnswers.arc)

    case DestinationTypePage =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.InvoiceDetailsController.onPreDraftPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case InvoiceDetailsPage =>
      (userAnswers: UserAnswers) => controllers.sections.info.routes.DispatchDetailsController.onPreDraftPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)

    case DispatchDetailsPage(_) =>
      (userAnswers: UserAnswers) => controllers.routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.arc)

    case _ =>
      (userAnswers: UserAnswers) => controllers.routes.IndexController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => controllers.routes.DraftMovementController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private[navigation] val reviewRouteMap: Page => UserAnswers => Call = {
    case _ =>
      (userAnswers: UserAnswers) => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers)(implicit request: DataRequest[_]): Call = mode match {
    case NormalMode =>
      normalRoutes(request)(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case ReviewMode =>
      reviewRouteMap(page)(userAnswers)
  }

}
