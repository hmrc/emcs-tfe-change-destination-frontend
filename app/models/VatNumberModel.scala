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

package models

import config.Constants.NONGBVAT
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class VatNumberModel(hasVatNumber: Boolean,
                          vatNumber: Option[String])

object VatNumberModel {

  implicit val writes: Writes[VatNumberModel] = Json.format[VatNumberModel]

  //Custom reads to support production deployment for users that already have drafts in the old format
  implicit val reads: Reads[VatNumberModel] =
    Json.reads[VatNumberModel].orElse {
      JsPath.readNullable[String].map {
        case Some(vatNumber) if vatNumber != NONGBVAT =>
          VatNumberModel(hasVatNumber = true, Some(vatNumber))
        case _ =>
          VatNumberModel(hasVatNumber = false, None)
      }
    }
}