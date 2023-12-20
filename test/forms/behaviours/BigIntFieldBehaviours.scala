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

package forms.behaviours

import play.api.data.{Form, FormError}

trait BigIntFieldBehaviours extends FieldBehaviours {

  def bigIntField(form: Form[_],
               fieldName: String,
               nonNumericError: FormError,
               wholeNumberError: FormError): Unit = {

    "not bind non-numeric numbers" in {
      val nonNumeric = "beans"
      val result = form.bind(Map(fieldName -> nonNumeric)).apply(fieldName)
      result.errors must contain only nonNumericError
    }

    "not bind decimals" in {
      val decimal: BigDecimal = 3.2
      val result = form.bind(Map(fieldName -> decimal.toString)).apply(fieldName)
      result.errors must contain only wholeNumberError
    }
  }

  def bigIntFieldWithMinimum(form: Form[_],
                          fieldName: String,
                          minimum: BigInt,
                          expectedError: FormError): Unit = {

    s"not bind integers below $minimum" in {
      val number: BigInt = minimum - 1
      val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
      result.errors must contain only expectedError
    }
  }

  def bigIntFieldWithMaximum(form: Form[_],
                          fieldName: String,
                          maximum: BigInt,
                          expectedError: FormError): Unit = {

    s"not bind integers above $maximum" in {
      val number: BigInt = maximum + 1
      val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
      result.errors must contain only expectedError
    }
  }

  def bigIntFieldWithRange(form: Form[_],
                        fieldName: String,
                        minimum: BigInt,
                        maximum: BigInt,
                        expectedError: FormError): Unit = {

    s"not bind integers outside the range $minimum to $maximum" in {
      val tooLarge = maximum + 1
      val resultLarge = form.bind(Map(fieldName -> tooLarge.toString)).apply(fieldName)
      resultLarge.errors must contain only expectedError

      val tooSmall = minimum - 1
      val resultSmall = form.bind(Map(fieldName -> tooSmall.toString)).apply(fieldName)
      resultSmall.errors must contain only expectedError
    }
  }
}
