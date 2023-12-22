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

package models.submitChangeDestination

import config.AppConfig
import models.requests.DataRequest
import models.sections.info.DispatchPlace
import models.sections.info.movementScenario.{MovementScenario, MovementType}
import models.{NorthernIrelandRegisteredConsignor, NorthernIrelandWarehouseKeeper, UserType}
import pages.sections.exportInformation.ExportCustomsOfficePage
import pages.sections.info.{DestinationTypePage, DispatchPlacePage}
import play.api.i18n.Messages
import play.api.libs.json.{Json, OFormat}
import utils.ModelConstructorHelpers

//TODO: THIS MODEL IS ACTUALLY SUBMIT CREATE MOVEMENT
//      It has been copied over from the Create Movement Frontend and then renamed - but the fields need updating as part
//      Of the submission story for CoD that gets played. Then these comments can be removed.
case class SubmitChangeDestinationModel(
                                movementType: MovementType,
                                attributes: AttributesModel,
                                consigneeTrader: Option[TraderModel],
                                consignorTrader: TraderModel,
                                placeOfDispatchTrader: Option[TraderModel],
                                complementConsigneeTrader: Option[ComplementConsigneeTraderModel],
                                deliveryPlaceTrader: Option[TraderModel],
                                deliveryPlaceCustomsOffice: Option[OfficeModel],
                                competentAuthorityDispatchOffice: OfficeModel,
                                transportArrangerTrader: Option[TraderModel],
                                firstTransporterTrader: Option[TraderModel],
                                headerEadEsad: HeaderEadEsadModel,
                                transportMode: TransportModeModel,
                                movementGuarantee: MovementGuaranteeModel,
                                eadEsadDraft: EadEsadDraftModel,
                                transportDetails: Seq[TransportDetailsModel]
                              )

object SubmitChangeDestinationModel extends ModelConstructorHelpers {
  implicit val fmt: OFormat[SubmitChangeDestinationModel] = Json.format

  private[submitChangeDestination]def dispatchOffice(implicit request: DataRequest[_], appConfig: AppConfig): OfficeModel = {

    val userType = UserType(request.ern)

    // TODO will need to align for duty paid traders
    OfficeModel(
      if(userType == NorthernIrelandRegisteredConsignor) {
        DispatchPlace.NorthernIreland + appConfig.destinationOfficeSuffix
      } else if(userType == NorthernIrelandWarehouseKeeper) {
        mandatoryPage(DispatchPlacePage) + appConfig.destinationOfficeSuffix
      } else {
        DispatchPlace.GreatBritain + appConfig.destinationOfficeSuffix
      }
    )
  }

  def apply(implicit request: DataRequest[_], appConfig: AppConfig): SubmitChangeDestinationModel = {

    val movementScenario: MovementScenario = mandatoryPage(DestinationTypePage)

    SubmitChangeDestinationModel(
      movementType = movementScenario.movementType,
      attributes = AttributesModel.apply(movementScenario.destinationType),
      consigneeTrader = TraderModel.applyConsignee,
      consignorTrader = TraderModel.applyConsignor,
      placeOfDispatchTrader = TraderModel.applyPlaceOfDispatch,
      complementConsigneeTrader = ComplementConsigneeTraderModel.apply,
      deliveryPlaceTrader = TraderModel.applyDeliveryPlace(movementScenario),
      deliveryPlaceCustomsOffice = request.userAnswers.get(ExportCustomsOfficePage).map(OfficeModel(_)),
      competentAuthorityDispatchOffice = dispatchOffice,
      transportArrangerTrader = TraderModel.applyTransportArranger,
      firstTransporterTrader = TraderModel.applyFirstTransporter,
      headerEadEsad = HeaderEadEsadModel.apply(movementScenario.destinationType),
      transportMode = TransportModeModel.apply,
      movementGuarantee = MovementGuaranteeModel.apply,
      eadEsadDraft = EadEsadDraftModel.apply,
      transportDetails = TransportDetailsModel.apply
    )
  }
}
