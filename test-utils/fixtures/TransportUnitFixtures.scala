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

package fixtures

import models.sections.transportUnit.TransportSealTypeModel
import play.api.libs.json.Json

trait TransportUnitFixtures {

  val transportSealTypeModelMax = TransportSealTypeModel(
    sealType = "Seal Type",
    moreInfo = Some("more info")
  )

  val transportSealTypeJsonMax = Json.obj(
    "sealType" -> "Seal Type",
    "moreInfo" -> "more info"
  )

  val transportSealTypeModelMin = TransportSealTypeModel(
    sealType = "Seal Type",
    moreInfo = None
  )

  val transportSealTypeJsonMin = Json.obj(
    "sealType" -> "Seal Type"
  )
}
