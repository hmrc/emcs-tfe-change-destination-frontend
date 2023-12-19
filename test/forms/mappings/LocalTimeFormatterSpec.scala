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

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.data.{Form, FormError}

import java.time.LocalTime

class LocalTimeFormatterSpec extends AnyFreeSpec with Matchers with OptionValues with Mappings {

  val form = Form(
    "value" -> localTime(
      invalidKey = "error.invalid",
      requiredKey = "error.required"
    )
  )

  // scalastyle:off magic.number
  val time: LocalTime = LocalTime.of(6, 25)

  val validEntries = Seq(
    "05:15" -> LocalTime.of(5, 15),
    "0515" -> LocalTime.of(5, 15),
    "05.15" -> LocalTime.of(5, 15),
    "05 15" -> LocalTime.of(5, 15),
    "5:am" -> LocalTime.of(5, 0),
    "5.am" -> LocalTime.of(5, 0),
    "515am" -> LocalTime.of(5, 15),
    "515.am" -> LocalTime.of(5, 15),
    "515a.m." -> LocalTime.of(5, 15),
    "515a.m" -> LocalTime.of(5, 15),
    "515 am" -> LocalTime.of(5, 15),
    "5 15am" -> LocalTime.of(5, 15),
    "5 15 am" -> LocalTime.of(5, 15),
    "5 15" -> LocalTime.of(5, 15),
    "5:15" -> LocalTime.of(5, 15),
    "515" -> LocalTime.of(5, 15),
    "0515pm" -> LocalTime.of(17, 15),
    "05:15am" -> LocalTime.of(5, 15),
    "05.15am" -> LocalTime.of(5, 15),
    "5:00am" -> LocalTime.of(5, 0),
    "13" -> LocalTime.of(13, 0),
    "5" -> LocalTime.of(5, 0),
    "00" -> LocalTime.of(0, 0),
    "05" -> LocalTime.of(5, 0),
    "12" -> LocalTime.of(12, 0),
    "5." -> LocalTime.of(5, 0),
    "9am" -> LocalTime.of(9, 0),
    "14:00" -> LocalTime.of(14, 0),
    "12am" -> LocalTime.of(0,0),
    "12pm" -> LocalTime.of(12, 0)
  )
  // scalastyle:on magic.number

  val invalidEntries = Seq(
    "9;15am",
    "17:15am",
    "1715am",
    "515a m",
    "8am-9am",
    "tbc",
    "12noon"
  )

  ".bind" - {

    "must fail to bind an empty time" in {
      val data = Map("value" -> "")
      val result = form.bind(data)
      result.errors must contain only FormError("value", "error.required", List.empty)
    }

    invalidEntries.foreach {
      case input =>

        s"must fail to bind $input" in {
          val data = Map("value" -> input)
          val result = form.bind(data)
          result.errors must contain only FormError("value", "error.invalid", List.empty)
        }
    }

    validEntries.foreach {
      case (input, output) =>

        s"must bind $input as $output" in {
          val data = Map("value" -> input)
          val result = form.bind(data)

          result.value.value mustEqual output
        }
    }

  }


}
