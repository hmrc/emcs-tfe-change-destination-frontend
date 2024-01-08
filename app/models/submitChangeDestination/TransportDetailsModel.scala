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

import models.Index
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import models.sections.transportUnit.TransportSealTypeModel
import pages.sections.transportUnit._
import play.api.libs.json.{Json, OFormat}
import queries.TransportUnitsCount
import utils.{JsonOptionFormatter, Logging, ModelConstructorHelpers}

//TODO: REFACTOR FOR ACTUAL COD SUBMISSION
case class TransportDetailsModel(
    transportUnitCode: String,
    identityOfTransportUnits: Option[String],
    commercialSealIdentification: Option[String],
    complementaryInformation: Option[String],
    sealInformation: Option[String]
)

object TransportDetailsModel extends ModelConstructorHelpers with Logging with JsonOptionFormatter {

  def apply(implicit request: DataRequest[_]): Seq[TransportDetailsModel] = {
    request.userAnswers.get(TransportUnitsCount) match {
      case Some(0) | None =>
        logger.error("TransportUnitSection should contain at least one item")
        throw MissingMandatoryPage("TransportUnitSection should contain at least one item")
      case Some(value) =>
        (0 until value)
          .map(Index(_))
        .map {
          idx =>
            val sealType: Option[TransportSealTypeModel] = request.userAnswers.get(TransportSealTypePage(idx))
            TransportDetailsModel(
              transportUnitCode = mandatoryPage(TransportUnitTypePage(idx)).toString,
              identityOfTransportUnits = request.userAnswers.get(TransportUnitIdentityPage(idx)),
              commercialSealIdentification = sealType.map(_.sealType),
              complementaryInformation = request.userAnswers.get(TransportUnitGiveMoreInformationPage(idx)).flatten,
              sealInformation = sealType.flatMap(_.moreInfo)
            )
        }
    }
  }

  implicit val fmt: OFormat[TransportDetailsModel] = Json.format
}
