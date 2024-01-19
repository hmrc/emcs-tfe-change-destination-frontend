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
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{NoGuarantorRequired, NoGuarantorRequiredUkToEu}
import models.sections.info.movementScenario.{MovementScenario, MovementType}
import pages.sections.guarantor.{GuarantorArrangerPage, GuarantorRequiredPage}
import pages.sections.info.DestinationTypePage
import play.api.libs.json.{Json, OFormat}
import utils.ModelConstructorHelpers

case class MovementGuaranteeModel(
                                   guarantorTypeCode: GuarantorArranger,
                                   guarantorTrader: Option[Seq[TraderModel]]
                                 )

object MovementGuaranteeModel extends ModelConstructorHelpers {

  def apply(implicit request: DataRequest[_]): MovementGuaranteeModel = {
      val guarantorRequired: Boolean = mandatoryPage(GuarantorRequiredPage)

      if (!guarantorRequired) {
        val movementScenario: MovementScenario = mandatoryPage(DestinationTypePage)
        MovementGuaranteeModel(
          guarantorTypeCode = if (movementScenario.movementType == MovementType.UkToEu) NoGuarantorRequiredUkToEu else NoGuarantorRequired,
          guarantorTrader = None
        )
      } else {
        val guarantorArranger: GuarantorArranger = mandatoryPage(GuarantorArrangerPage)
        MovementGuaranteeModel(
          guarantorTypeCode = guarantorArranger,
          guarantorTrader = Some(Seq(TraderModel.applyGuarantor(guarantorArranger)))
        )
      }
    }

  implicit val fmt: OFormat[MovementGuaranteeModel] = Json.format
}
