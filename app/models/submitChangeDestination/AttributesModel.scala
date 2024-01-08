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
import models.sections.info.movementScenario.DestinationType
import play.api.libs.json.{Json, OFormat}
import utils.ModelConstructorHelpers

//TODO: REFACTOR FOR ACTUAL COD SUBMISSION
case class AttributesModel(submissionMessageType: SubmissionMessageType)
object AttributesModel extends ModelConstructorHelpers {
  implicit val fmt: OFormat[AttributesModel] = Json.format

  private def deriveSubmissionMessageType(ern: String, destinationType: DestinationType): SubmissionMessageType = {
    if(ern.startsWith("XIP")) {
      SubmissionMessageType.DutyPaidB2B
    } else if(destinationType == DestinationType.Export) {
      SubmissionMessageType.Export
    } else {
      SubmissionMessageType.Standard
    }
  }

  def apply(destinationType: DestinationType)(implicit request: DataRequest[_]): AttributesModel = AttributesModel(
    submissionMessageType = deriveSubmissionMessageType(request.ern, destinationType)
  )
}
