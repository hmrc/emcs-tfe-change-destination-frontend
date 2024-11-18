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

package forms.sections.movement

import base.SpecBase
import fixtures.messages.sections.movement.InvoiceDetailsMessages
import forms.behaviours.StringFieldBehaviours
import models.sections.info.InvoiceDetailsModel
import play.api.data.FormError
import play.api.i18n.Messages

class InvoiceDetailsFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val referenceField = "invoice-reference"
  val dateField = "value"
  val dayField = "value.day"
  val monthField = "value.month"
  val yearField = "value.year"

  val form = new InvoiceDetailsFormProvider(appConfig)()

  def formAnswersMap(reference: String = invoiceDetailsModel.reference,
                     day: String = invoiceDetailsModel.date.get.getDayOfMonth.toString,
                     month: String = invoiceDetailsModel.date.get.getMonthValue.toString,
                     year: String = invoiceDetailsModel.date.get.getYear.toString
                    ): Map[String, String] = {
    Map(
      referenceField -> reference,
      dayField -> day,
      monthField -> month,
      yearField -> year
    )
  }

  "the invoice details form" - {

    "when all fill are valid " in {

      val data = formAnswersMap()

      val expectedResult = Some(invoiceDetailsModel)
      val expectedErrors = Seq.empty

      val actualResult = form.bind(data)

      actualResult.errors mustBe expectedErrors
      actualResult.value mustBe expectedResult
    }

    "reference field" - {

      val characterLimit = 35

      "should validate" - {

        "when max characters entered" in {

          val answer = "1" * characterLimit

          val data = formAnswersMap(reference = answer)

          val expectedResult = Some(invoiceDetailsModel.copy(reference = answer))
          val expectedErrors = Seq.empty

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedErrors
          actualResult.value mustBe expectedResult
        }
      }

      "should error" - {

        "when not entered" in {

          val data = formAnswersMap(reference = "")

          val expectedResult = Seq(FormError(referenceField, s"invoiceDetails.$referenceField.error.required"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when too many characters entered" in {

          val data = formAnswersMap(reference = "1" * (characterLimit + 1))

          val expectedResult = Seq(FormError(referenceField, s"invoiceDetails.$referenceField.error.length", List(characterLimit)))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }
    }

    "the date fields" - {

      "should give an error" - {

        "when the date is invalid" in {

          val data = formAnswersMap(day = "1000", month = "1000", year = "A")

          val expectedResult = Seq(FormError(dateField, s"invoiceDetails.$dateField.error.notARealDate"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when the date is before the earliest allowed date" in {
          val data = formAnswersMap(day = "31", month = "12", year = "1999")

          val expectedResult = Seq(FormError(dateField, s"invoiceDetails.$dateField.error.earliestDate"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }

      "should NOT giv an error" - {

        "when no Invoice Date is entered" in {

          val data = formAnswersMap(day = "", month = "", year = "")

          val actualResult = form.bind(data)

          actualResult.errors mustBe Seq()
          actualResult.value mustBe Some(InvoiceDetailsModel(invoiceDetailsModel.reference, None))
        }
      }
    }

    "the day field" - {

      "should give an error" - {

        "when not supplied" in {

          val data = formAnswersMap(day = "")

          val expectedResult = Seq(FormError(dateField, s"invoiceDetails.$dateField.error.required.one", List("day")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }
    }

    "the month field" - {

      "should give an error" - {

        "when not supplied" in {

          val data = formAnswersMap(month = "")

          val expectedResult = Seq(FormError(dateField, s"invoiceDetails.$dateField.error.required.one", List("month")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }
    }

    "the year field" - {

      "should give an error" - {

        "when not entered" in {

          val data = formAnswersMap(year = "")

          val expectedResult = Seq(FormError(dateField, s"invoiceDetails.$dateField.error.required.one", List("year")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when it's not four digits long" in {
          val data = formAnswersMap(year = "23")

          val expectedResult = Seq(FormError(dateField, s"invoiceDetails.$dateField.error.yearNotFourDigits"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }
    }

    "the day and month fields" - {

      "should give an error" - {

        "when not entered" in {

          val data = formAnswersMap(day = "", month = "")

          val expectedResult = Seq(FormError(dateField, s"invoiceDetails.$dateField.error.required.two", List("day", "month")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }
    }

    "the day and year fields" - {

      "should give an error" - {

        "when not entered" in {
          val data = formAnswersMap(day = "", year = "")

          val expectedResult = Seq(FormError(dateField, s"invoiceDetails.$dateField.error.required.two", List("day", "year")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }
    }

    "the month and year" - {

      "should give an error" - {

        "when not entered" in {

          val data = formAnswersMap(month = "", year = "")

          val expectedResult = Seq(FormError(dateField, s"invoiceDetails.$dateField.error.required.two", List("month", "year")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }
    }
  }

  "Error Messages" - {

    Seq(InvoiceDetailsMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for no reference" in {

          msgs("invoiceDetails.invoice-reference.error.required") mustBe
            messagesForLanguage.referenceErrorRequired
        }

        "have the correct error message for length error" in {

          msgs("invoiceDetails.invoice-reference.error.length") mustBe
            messagesForLanguage.referenceErrorLength
        }

        "have the correct error message for no date" in {

          msgs("invoiceDetails.value.error.required.all") mustBe
            messagesForLanguage.dateErrorRequiredAll
        }

        "have the correct error message for one missing date field" in {

          msgs("invoiceDetails.value.error.required.one", "year") mustBe
            messagesForLanguage.dateErrorRequired("year")
        }

        "have the correct error message for two missing date fields" in {

          msgs("invoiceDetails.value.error.required.two", "day", "month") mustBe
            messagesForLanguage.dateErrorRequiredTwo("day", "month")
        }

        "have the correct error message when the year isn't 4 digits long" in {
          msgs("invoiceDetails.value.error.yearNotFourDigits") mustBe
            messagesForLanguage.yearIsNotFourDigitsLong
        }
      }
    }
  }
}