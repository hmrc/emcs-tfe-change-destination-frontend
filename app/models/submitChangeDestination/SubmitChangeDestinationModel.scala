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

import models.requests.DataRequest
import pages.sections.transportArranger.TransportArrangerReviewPage
import pages.sections.transportUnit.TransportUnitsReviewPage
import play.api.libs.json.{Json, OFormat}
import utils.ModelConstructorHelpers

case class SubmitChangeDestinationModel(
                                         newTransportArrangerTrader: Option[TraderModel],
                                         updateEadEsad: UpdateEadEsadModel,
                                         destinationChanged: DestinationChangedModel,
                                         newTransporterTrader: Option[TraderModel],
                                         transportDetails: Option[Seq[TransportDetailsModel]]
                                       )

object SubmitChangeDestinationModel extends ModelConstructorHelpers {

  implicit val fmt: OFormat[SubmitChangeDestinationModel] = Json.format

  def apply(implicit request: DataRequest[_]): SubmitChangeDestinationModel =
    SubmitChangeDestinationModel(
      newTransportArrangerTrader = whenSectionChanged(TransportArrangerReviewPage)(TraderModel.applyTransportArranger),
      updateEadEsad = UpdateEadEsadModel.apply,
      destinationChanged = DestinationChangedModel.apply,
      newTransporterTrader = Some(TraderModel.applyFirstTransporter),
      transportDetails = whenSectionChanged(TransportUnitsReviewPage)(TransportDetailsModel.apply)
    )
}
