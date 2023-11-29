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

package models.sections.consignee

import base.SpecBase
import play.api.libs.json.{JsObject, JsSuccess, Json}

class ExemptOrganisationDetailsModelSpec extends SpecBase {

  val exemptOrganisationDetailsModel: ExemptOrganisationDetailsModel = ExemptOrganisationDetailsModel(
    memberState = "member state",
    certificateSerialNumber = "serial number"
  )

  val exemptOrganisationDetailsJson: JsObject = Json.obj(
    "memberState" -> "member state",
    "certificateSerialNumber" -> "serial number"
  )

  "OrganisationDetailsModel" - {

    "should read from json" in {
      Json.fromJson[ExemptOrganisationDetailsModel](exemptOrganisationDetailsJson) mustBe JsSuccess(exemptOrganisationDetailsModel)
    }

    "should write to json" in {
      Json.toJson(exemptOrganisationDetailsModel) mustBe exemptOrganisationDetailsJson
    }
  }
}
