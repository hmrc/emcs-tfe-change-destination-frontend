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

import models.ExemptOrganisationDetailsModel
import models.requests.DataRequest
import pages.sections.consignee.ConsigneeExemptOrganisationPage
import play.api.libs.json.{Json, OFormat}

//TODO: REFACTOR FOR ACTUAL COD SUBMISSION
case class ComplementConsigneeTraderModel(
                                           memberStateCode: String,
                                           serialNumberOfCertificateOfExemption: Option[String]
                                         )

object ComplementConsigneeTraderModel {
  implicit val fmt: OFormat[ComplementConsigneeTraderModel] = Json.format

  private def exemptOrganisationDetailsModelToComplementConsigneeTraderModel(
                                                                              exemptOrganisationDetailsModel: ExemptOrganisationDetailsModel
                                                                            ): ComplementConsigneeTraderModel =
    ComplementConsigneeTraderModel(
      exemptOrganisationDetailsModel.memberState,
      Some(exemptOrganisationDetailsModel.certificateSerialNumber)
    )

  def apply(implicit request: DataRequest[_]): Option[ComplementConsigneeTraderModel] = {
    request.userAnswers.get(ConsigneeExemptOrganisationPage).map(exemptOrganisationDetailsModelToComplementConsigneeTraderModel)
  }
}
