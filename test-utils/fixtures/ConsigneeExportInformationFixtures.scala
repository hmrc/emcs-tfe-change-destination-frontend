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

package fixtures

import models.sections.consignee.ConsigneeExportInformation
import models.sections.consignee.ConsigneeExportInformationType.{No, YesEoriNumber, YesVatNumber}
import play.api.libs.json.{JsObject, Json}

trait ConsigneeExportInformationFixtures {

  val exportTypeVatModel: ConsigneeExportInformation = ConsigneeExportInformation(
    exportType = YesVatNumber,
    vatNumber = Some("1234567890"),
    eoriNumber = None
  )

  val exportTypeVatJson: JsObject = Json.obj(
    "exportType" -> YesVatNumber.toString,
    "vatNumber" -> "1234567890"
  )

  val exportTypeEoriModel: ConsigneeExportInformation = ConsigneeExportInformation(
    exportType = YesEoriNumber,
    vatNumber = None,
    eoriNumber = Some("1234567890")
  )

  val exportTypeEoriJson: JsObject = Json.obj(
    "exportType" -> YesEoriNumber.toString,
    "eoriNumber" -> "1234567890"
  )

  val exportTypeNoModel: ConsigneeExportInformation = ConsigneeExportInformation(
    exportType = No,
    vatNumber = None,
    eoriNumber = None
  )

  val exportTypeNoJson: JsObject = Json.obj(
    "exportType" -> No.toString
  )

}
