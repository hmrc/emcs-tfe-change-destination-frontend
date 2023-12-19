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

import models.{Checkboxable, DynamicEnumerableType}
import play.api.libs.json._

case class WineOperations(code: String, description: String) extends Checkboxable[WineOperations] {
  override def toString: String = code

  override val messageKeyPrefix: String = WineOperations.messageKeyPrefix
}

object WineOperations extends DynamicEnumerableType[WineOperations] {
  val nonWineOperationCode = "0"

  implicit val format: Format[WineOperations] = Json.format[WineOperations]

  val messageKeyPrefix = "itemWineOperationsChoice"

  implicit val seqReads: Reads[Seq[WineOperations]] = {
    case JsObject(underlying) => JsSuccess(underlying.map {
      case (key, value) => WineOperations(key, value.as[String])
    }.toSeq)
    case other =>
      JsError("Unable to read WineOperations as a JSON object: " + other.toString())
  }
}
