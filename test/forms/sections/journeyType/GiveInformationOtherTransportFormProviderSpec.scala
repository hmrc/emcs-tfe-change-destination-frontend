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

package forms.sections.journeyType

import forms.behaviours.StringFieldBehaviours
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import play.api.data.FormError

class GiveInformationOtherTransportFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "giveInformationOtherTransport.error.required"
  val lengthKey = "giveInformationOtherTransport.error.length"
  val maxLength = 350

  val form = new GiveInformationOtherTransportFormProvider()()

  ".value" - {

    "field is mandatory" - {

      "form returns errors" - {

        "input field is empty" in {
          val data = Map("value" -> "")
          val result = form.bind(data)

          result.errors must contain only FormError("value", "giveInformationOtherTransport.error.required")
        }

        "input field is missing" in {
          val data = Map[String, String]()
          val result = form.bind(data)

          result.errors must contain only FormError("value", "giveInformationOtherTransport.error.required")
        }

        "input is only whitespace" in {
          val data = Map("value" ->
            """
              |
              |
              |
              |
              |
              |
              |""".stripMargin)
          val result = form.bind(data)

          result.errors must contain only FormError("value", "giveInformationOtherTransport.error.required")
        }
      }
    }

    "form returns an error when" - {


      "alpha numeric data isn't used" in {
        val data = Map("value" -> "..")
        val result = form.bind(data)

        result.errors must contain only FormError("value", "giveInformationOtherTransport.error.character", Seq(ALPHANUMERIC_REGEX))
      }

      "more than 350 characters are used" in {
        val data = Map("value" -> "a" * (maxLength + 1))
        val result = form.bind(data)

        result.errors must contain only FormError("value", "giveInformationOtherTransport.error.length", Seq(maxLength))
      }

      "invalid characters are used" in {
        val data = Map("value" -> "<>")
        val result = form.bind(data)

        result.errors mustBe Seq(
          FormError("value", "giveInformationOtherTransport.error.character", Seq(ALPHANUMERIC_REGEX))
        )
      }
    }


  }
}
