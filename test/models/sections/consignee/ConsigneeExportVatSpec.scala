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
import fixtures.ConsigneeExportVatFixtures
import play.api.libs.json.{JsSuccess, Json}

class ConsigneeExportVatSpec extends SpecBase with ConsigneeExportVatFixtures {

  "ConsigneeExportVat" - {

    "ConsigneeExportVat with a VAT number" - {

      "should read from json" in {
        Json.fromJson[ConsigneeExportVat](exportTypeVatJson) mustBe JsSuccess(exportTypeVatModel)
      }
      "should write to json" in {
        Json.toJson(exportTypeVatModel) mustBe exportTypeVatJson
      }
    }

    "ConsigneeExportVat with an EORI number" - {

      "should read from json" in {
        Json.fromJson[ConsigneeExportVat](exportTypeEoriJson) mustBe JsSuccess(exportTypeEoriModel)
      }
      "should write to json" in {
        Json.toJson(exportTypeEoriModel) mustBe exportTypeEoriJson
      }
    }

    "ConsigneeExportVat with neither a VAT or EORI number" - {

      "should read from json" in {
        Json.fromJson[ConsigneeExportVat](exportTypeNoJson) mustBe JsSuccess(exportTypeNoModel)
      }
      "should write to json" in {
        Json.toJson(exportTypeNoModel) mustBe exportTypeNoJson
      }
    }

  }
}
