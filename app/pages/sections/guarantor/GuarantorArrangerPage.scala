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

package pages.sections.guarantor

import models.requests.DataRequest
import models.response.InvalidGuarantorTypeException
import models.response.emcsTfe.GuarantorType
import models.response.emcsTfe.GuarantorType.GuarantorNotRequired
import models.sections.guarantor.GuarantorArranger
import models.sections.info.movementScenario.MovementType
import pages.QuestionPage
import pages.sections.info.DestinationTypePage
import play.api.libs.json.JsPath

case object GuarantorArrangerPage extends QuestionPage[GuarantorArranger] {
  override val toString: String = "guarantorArranger"
  override val path: JsPath = GuarantorSection.path \ toString

  override def getValueFromIE801(implicit request: DataRequest[_]): Option[GuarantorArranger] = {
    if (GuarantorSection.requiresGuarantorToBeProvided) None else {
      request.movementDetails.movementGuarantee.guarantorTypeCode match {
        case GuarantorType.Consignor => Some(GuarantorArranger.Consignor)
        case GuarantorType.Consignee => Some(GuarantorArranger.Consignee)
        case GuarantorType.Owner => Some(GuarantorArranger.GoodsOwner)
        case GuarantorType.Transporter => Some(GuarantorArranger.Transporter)
        case GuarantorType.NoGuarantor | GuarantorNotRequired =>
          request.userAnswers.get(DestinationTypePage).map { destination =>
            if(destination.movementType == MovementType.UkToEu) {
              GuarantorArranger.NoGuarantorRequiredUkToEu
            } else {
              GuarantorArranger.NoGuarantorRequired
            }
          }

        case guarantorType => throw InvalidGuarantorTypeException(s"Invalid guarantor type from IE801: $guarantorType")
      }
    }
  }
}
