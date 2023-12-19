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

package forms.sections.items

import forms.behaviours.BooleanFieldBehaviours
import forms.sections.items.ItemQuantityFormProvider._
import play.api.data.FormError

class ItemQuantityFormProviderSpec extends BooleanFieldBehaviours {

  val form = new ItemQuantityFormProvider()()


  "when binding valid values" - {

    "must bind successfully" - {

      "0dp" in {

        val boundForm = form.bind(Map(fieldName -> "1"))
        boundForm.errors mustBe Seq()
        boundForm.value mustBe Some(BigDecimal(1))
      }

      "1dp" in {

        val boundForm = form.bind(Map(fieldName -> "1.1"))
        boundForm.errors mustBe Seq()
        boundForm.value mustBe Some(BigDecimal(1.1))
      }

      "2dp" in {

        val boundForm = form.bind(Map(fieldName -> "1.12"))
        boundForm.errors mustBe Seq()
        boundForm.value mustBe Some(BigDecimal(1.12))
      }

      "max possible combination of numerics (15 precision, 3 scale)" in {

        val boundForm = form.bind(Map(fieldName -> "999999999999.999"))
        boundForm.errors mustBe Seq()
        boundForm.value mustBe Some(BigDecimal("999999999999.999"))
      }
    }
  }

  "when binding invalid values" - {

    "when empty" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(fieldName -> ""))
        boundForm.errors mustBe Seq(FormError(fieldName, requiredErrorKey))
      }
    }

    "when non-numeric" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(fieldName -> "banana"))
        boundForm.errors mustBe Seq(FormError(fieldName, nonNumericErrorKey))
      }
    }

    "when too many decimal places" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(fieldName -> "1.1234"))
        boundForm.errors mustBe Seq(FormError(fieldName, maxDecimalPlacesErrorKey, Seq(maxDecimalPlacesValue)))
      }
    }

    "when too small" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(fieldName -> (minValue - 0.001).toString()))
        boundForm.errors mustBe Seq(FormError(
          fieldName,
          rangeErrorKey,
          Seq(minValue, maxValue)
        ))
      }
    }

    "when too large" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(fieldName -> (maxValue + 0.001).toString()))
        boundForm.errors mustBe Seq(FormError(
          fieldName,
          rangeErrorKey,
          Seq(minValue, maxValue)
        ))
      }
    }
  }
}
