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

package forms.mappings

import play.api.data.FormError
import play.api.data.format.Formatter
import utils.{DateTimeUtils, Logging}

import java.time.LocalTime
import scala.util.Try

private[mappings] class LocalTimeFormatter(
                                            invalidKey: String,
                                            requiredKey: String,
                                            args: Seq[String] = Seq.empty
                                          ) extends Formatter[LocalTime] with Formatters with Logging with DateTimeUtils {
  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalTime] = {
    data.get(key) match {
      case None => Left(Seq(FormError(key, requiredKey, args)))
      case Some(value) if value.isEmpty => Left(Seq(FormError(key, requiredKey, args)))
      case Some(value) => parseUserEnteredTime(key, value)
    }
  }


  private def parseUserEnteredTime(key: String, value: String): Either[Seq[FormError], LocalTime] = {

    val pattern = """^([0-1]?[0-9]|2[0-3])[:.\s]?(([0-5][0-9])?)[.\s]?(([ap]\.?m\.?)?)$""".r

    // scalastyle:off magic.number
    value match {
      case pattern(hour, _, minute, _, ampm) =>

        val hourAsInt = Try {hour.toInt}.getOrElse(0)
        val minuteAsInt = Try {minute.toInt}.getOrElse(0)
        val optionalAmOrPm = Option(ampm)

        val twentyFourHourTime = 13 to 23 contains(hourAsInt)

        (twentyFourHourTime, hourAsInt, minuteAsInt, optionalAmOrPm) match {
          case (true, h, m, o) =>
            if (o.isDefined) Left(Seq(FormError(key, invalidKey, args))) else Right(LocalTime.of(h, m))

          case(false, 12, m, Some("pm")) =>
            Right(LocalTime.of(12, m))

          case (false, h, m, Some("pm")) =>
            Right(LocalTime.of(h + 12, m))

          case (false, 12, m, Some("am")) =>
            Right(LocalTime.of(0, m))

          case (false, h, m, _) =>
            Right(LocalTime.of(h, m))

          case unparseableValue =>
            logger.warn(s"[parseUserEnteredTime] - Unable to parse $unparseableValue")
            Left(Seq(FormError(key, invalidKey, args)))
        }

      case _ =>
        Left(Seq(FormError(key, invalidKey, args)))
    }
    // scalastyle:on magic.number
  }


  override def unbind(key: String, time: LocalTime): Map[String, String] =
    Map(key -> time.formatTimeForUIOutput)

}

