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

package forms.sections.info

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import utils.DateTimeUtils

class DispatchDetailsFormProviderSpec extends SpecBase with StringFieldBehaviours with DateTimeUtils {

  val dateField = "value"
  val dayField = "value.day"
  val monthField = "value.month"
  val yearField = "value.year"
  val timeField = "time"

  val form = new DispatchDetailsFormProvider(appConfig)()

  def formAnswersMap(day: String = dispatchDetailsModel.date.getDayOfMonth.toString,
                     month: String = dispatchDetailsModel.date.getMonthValue.toString,
                     year: String = dispatchDetailsModel.date.getYear.toString,
                     time: String = dispatchDetailsModel.time.formatTimeForUIOutput()
                    ): Map[String, String] = {
    Map(
      dayField -> day,
      monthField -> month,
      yearField -> year,
      timeField -> time
    )
  }

  "the dispatch details form" - {

    "when all filled in are valid " in {

      val data = formAnswersMap()

      val expectedResult = Some(dispatchDetailsModel)
      val expectedErrors = Seq.empty

      val actualResult = form.bind(data)

      actualResult.errors mustBe expectedErrors
      actualResult.value mustBe expectedResult
    }

    "the date fields" - {

      "should give an error" - {

        "when not entered" in {

          val data = formAnswersMap(day = "", month = "", year = "")

          val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.all"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when the date is invalid" in {

          val data = formAnswersMap(day = "1000", month = "1000", year = "1000")

          val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.invalid"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when the date is before the earliest allowed date" in {
          val data = formAnswersMap(day = "31", month = "12", year = "1999")

          val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.earliestDate"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }

      "the day field" - {

        "should give an error" - {

          "when not supplied" in {

            val data = formAnswersMap(day = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required", List("day")))

            val actualResult = form.bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the month field" - {

        "should give an error" - {

          "when not supplied" in {

            val data = formAnswersMap(month = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required", List("month")))

            val actualResult = form.bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the year field" - {

        "should give an error" - {

          "when not entered" in {

            val data = formAnswersMap(year = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required", List("year")))

            val actualResult = form.bind(data)

            actualResult.errors mustBe expectedResult
          }

          "when it's not four digits long" in {
            val data = formAnswersMap(year = "23")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.yearNotFourDigits"))

            val actualResult = form.bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the day and month fields" - {

        "should give an error" - {

          "when not entered" in {

            val data = formAnswersMap(day = "", month = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.two", List("day", "month")))

            val actualResult = form.bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the day and year fields" - {

        "should give an error" - {

          "when not entered" in {
            val data = formAnswersMap(day = "", year = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.two", List("day", "year")))

            val actualResult = form.bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }

      "the month and year" - {

        "should give an error" - {

          "when not entered" in {

            val data = formAnswersMap(month = "", year = "")

            val expectedResult = Seq(FormError(dateField, s"dispatchDetails.$dateField.error.required.two", List("month", "year")))

            val actualResult = form.bind(data)

            actualResult.errors mustBe expectedResult
          }
        }
      }
    }

    "the time field" - {

      "should error" - {

        "when not entered" in {

          val data = formAnswersMap(time = "")

          val expectedResult = Seq(FormError(timeField, s"dispatchDetails.$timeField.error.required"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when an incorrect formatted time is entered" in {

          val data = formAnswersMap(time = "Ten past six")

          val expectedResult = Seq(FormError(timeField, s"dispatchDetails.$timeField.error.invalid"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }

    }
  }

}
