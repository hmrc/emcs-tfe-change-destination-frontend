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

package models.response.referenceData

import models.sections.items.ItemBulkPackagingCode
import play.api.libs.json._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

case class BulkPackagingType(packagingType: ItemBulkPackagingCode, description: String)

object BulkPackagingType {
  implicit val format: Format[BulkPackagingType] = Json.format[BulkPackagingType]

  implicit val seqReads: Reads[Seq[BulkPackagingType]] = {
    case JsObject(underlying) => JsSuccess(underlying.map {
      case (key, value) => BulkPackagingType(JsString(key).as[ItemBulkPackagingCode], value.as[String])
    }.toSeq)
    case other =>
      JsError("Unable to read BulkPackagingType as a JSON object: " + other.toString())
  }

  def options(bulkPackagingTypes: Seq[BulkPackagingType]): Seq[RadioItem] =
    bulkPackagingTypes
      .sortBy(_.packagingType.positionInRadioList)
      .zipWithIndex
      .map {
        case (value, index) =>
          RadioItem(
            content = HtmlContent(s"${value.description} (${value.packagingType.toString})"),
            value = Some(value.packagingType.toString),
            id = Some(s"value_$index")
          )
      }
}
