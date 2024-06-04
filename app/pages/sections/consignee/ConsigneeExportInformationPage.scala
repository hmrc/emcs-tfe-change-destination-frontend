/*
 * Copyright 2024 HM Revenue & Customs
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

package pages.sections.consignee

import models.requests.DataRequest
import models.response.emcsTfe.TraderModel
import models.sections.consignee.ConsigneeExportInformation
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation, VatNumber}
import models.sections.info.ChangeType.ChangeConsignee
import models.sections.info.movementScenario.DestinationType.Export
import pages.QuestionPage
import pages.sections.info.ChangeTypePage
import play.api.libs.json.JsPath

case object ConsigneeExportInformationPage extends QuestionPage[Set[ConsigneeExportInformation]] {
  override val toString: String = "exportInformation"
  override val path: JsPath = ConsigneeSection.path \ toString

  //scalastyle:off
  override def getValueFromIE801(implicit request: DataRequest[_]): Option[Set[ConsigneeExportInformation]] =
    request.userAnswers.get(ChangeTypePage) match {
      case Some(ChangeConsignee) => None
      case _ =>
        request.movementDetails.consigneeTrader.map {
          case TraderModel(Some(_), _, _, _, Some(_)) if request.movementDetails.destinationType == Export =>
            Set(VatNumber, EoriNumber)
          case TraderModel(Some(_), _, _, _, None) if request.movementDetails.destinationType == Export =>
            Set(VatNumber)
          case TraderModel(_, _, _, _, Some(_)) =>
            Set(EoriNumber)
          case _ =>
            Set(NoInformation)
        }
    }
}
