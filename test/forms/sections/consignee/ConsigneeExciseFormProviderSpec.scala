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

package forms.sections.consignee

import forms.ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError

class ConsigneeExciseFormProviderSpec extends StringFieldBehaviours with GuiceOneAppPerSuite {

  val fieldName = "value"

  "ConsigneeExciseFormProvider" - {
    val form = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = false)
    val dynamicForm = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = true)

    "when a value is not provided" - {
      "must error with the expected msg key" in {
        val boundForm = form.bind(Map(fieldName -> ""))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.noInput", Seq()))
      }
      "must error with the expected msg key for the dynamic form" in {
        val boundForm = dynamicForm.bind(Map(fieldName -> ""))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryConsignee.error.noInput", Seq()))
      }
    }

    "when the value is too long" - {
      "must error with the expected msg key" in {
        val maxLength = 13
        val boundForm = form.bind(Map(fieldName -> "A" * (maxLength + 1)))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.tooLong", Seq(maxLength)))
      }
      "must error with the expected msg key for the dynamic form" in {
        val maxLength = 16
        val boundForm = dynamicForm.bind(Map(fieldName -> "A" * (maxLength + 1)))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryConsignee.error.tooLong", Seq(maxLength)))
      }
    }

    "when the value contains invalid characters" - {
      "must error with the expected msg key" in {
        val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+"))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.invalidCharacters", Seq(ALPHANUMERIC_REGEX)))
      }
      "must error with the expected msg key for the dynamic form" in {
        val boundForm = dynamicForm.bind(Map(fieldName -> "!@£$%^&*()_+"))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryConsignee.error.invalidCharacters", Seq(ALPHANUMERIC_REGEX)))
      }
    }

    "when ERN starts with lowercase" - {
      "must return success" in {
        val boundForm = form.bind(Map(fieldName -> "gbwk123456789"))
        boundForm.errors.headOption mustBe None
        boundForm.value mustBe Some("GBWK123456789")
      }
    }

    "when ERN contains spaces" - {
      "must return success" in {
        val boundForm = form.bind(Map(fieldName -> "GBWK1 2 3 4 56789"))
        boundForm.errors.headOption mustBe None
        boundForm.value mustBe Some("GBWK123456789")
      }
    }
  }
}
