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

package controllers.sections.transportUnit

import controllers.BaseNavigationController
import controllers.actions._
import models.requests.DataRequest
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.sections.transportUnit.{TransportSealTypeModel, TransportUnitType}
import models.{Index, NormalMode, UserAnswers}
import navigation.TransportUnitNavigator
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit._
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.TransportUnitsCount
import services.UserAnswersService

import javax.inject.Inject
import scala.concurrent.Future

class TransportUnitIndexController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              override val userAnswersService: UserAnswersService,
                                              override val navigator: TransportUnitNavigator,
                                              override val auth: AuthAction,
                                              override val getData: DataRetrievalAction,
                                              override val requireData: DataRequiredAction,
                                              override val withMovement: MovementAction,
                                              override val userAllowList: UserAllowListAction,
                                              val controllerComponents: MessagesControllerComponents
                                            ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      val userAnswersWith801TransportUnits = populateTransportUnitsFrom801IfEmpty
      (userAnswersWith801TransportUnits.get(TransportUnitsCount), request.userAnswers.get(HowMovementTransportedPage)) match {
        case (_, Some(FixedTransportInstallations)) =>
          Future(Redirect(
            controllers.sections.transportUnit.routes.TransportUnitCheckAnswersController.onPageLoad(request.ern, request.arc)
          ))
        case (None | Some(0), _) =>
          Future(Redirect(
            controllers.sections.transportUnit.routes.TransportUnitTypeController.onPageLoad(request.ern, request.arc, Index(0), NormalMode)
          ))
        case _ =>
          lazy val redirectCall = Redirect(
            controllers.sections.transportUnit.routes.TransportUnitsAddToListController.onPageLoad(request.ern, request.arc)
          )
          if(userAnswersWith801TransportUnits == request.userAnswers) {
            Future(redirectCall)
          } else {
            userAnswersService.set(userAnswersWith801TransportUnits).map(_ => redirectCall)
          }
      }
    }

  private def populateTransportUnitsFrom801IfEmpty()(implicit request: DataRequest[_]): UserAnswers = {
    request.userAnswers.get(TransportUnitsCount) match {
      case None if request.movementDetails.transportDetails.nonEmpty =>
        request.movementDetails.transportDetails.zipWithIndex.foldLeft(request.userAnswers)(
          (userAnswers, indexedTransportDetails) => {
            val idx = indexedTransportDetails._2
            val baseUserAnswers = userAnswers
              .set(TransportUnitTypePage(idx), TransportUnitType.parseTransportUnitType(indexedTransportDetails._1.transportUnitCode).get)
              .set(TransportSealChoicePage(idx), indexedTransportDetails._1.commercialSealIdentification.isDefined)
              .set(TransportUnitGiveMoreInformationChoicePage(idx), indexedTransportDetails._1.complementaryInformation.isDefined)
              .set(TransportUnitGiveMoreInformationPage(idx), indexedTransportDetails._1.complementaryInformation)
            (indexedTransportDetails._1.commercialSealIdentification, indexedTransportDetails._1.identityOfTransportUnits) match {
              case (Some(sealInfo), Some(identityOfTU)) =>
                baseUserAnswers
                  .set(TransportSealTypePage(idx), TransportSealTypeModel(sealInfo, indexedTransportDetails._1.sealInformation))
                  .set(TransportUnitIdentityPage(idx), identityOfTU)
              case (Some(sealInfo), _) =>
                baseUserAnswers
                  .set(TransportSealTypePage(idx), TransportSealTypeModel(sealInfo, indexedTransportDetails._1.sealInformation))
              case (_, Some(identityOfTU)) =>
                baseUserAnswers
                  .set(TransportUnitIdentityPage(idx), identityOfTU)
              case (_, _) => baseUserAnswers
            }
          }
        )
      case _ => request.userAnswers
    }
  }
}
