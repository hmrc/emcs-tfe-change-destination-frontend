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

package models.sections.journeyType

import play.api.libs.json._

import scala.util.Try

sealed trait JourneyTime {
  val time: String
  def toDownstream: String
}

object JourneyTime {

  implicit val reads: Reads[JourneyTime] = {
    case JsString(value) => value.split(" ").toList match {
      case value :: "hours" :: Nil if Try(value.toInt).isSuccess => JsSuccess(Hours(value))
      case value :: "days" :: Nil if Try(value.toInt).isSuccess => JsSuccess(Days(value))
      case other => JsError(s"Could not parse JourneyTime from JSON, received: '${other.mkString(" ")}'")
    }
    case other => JsError(s"Value is not a String: $other")
  }

  implicit val writes: Writes[JourneyTime] = (o: JourneyTime) => JsString(o.toString)

  case class Hours(time: String) extends JourneyTime {
    override def toString: String = s"$time hours"

    def toDownstream: String = s"H${"%02d".format(time.toInt)}"
  }

  case class Days(time: String) extends JourneyTime {
    override def toString: String = s"$time days"

    def toDownstream: String = s"D${"%02d".format(time.toInt)}"
  }
}
