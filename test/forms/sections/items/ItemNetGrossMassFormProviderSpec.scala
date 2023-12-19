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

import forms.FormSpec
import models.sections.items.ItemNetGrossMassModel
import play.api.data.{Form, FormBinding, FormError}
import play.api.mvc.{AnyContentAsFormUrlEncoded, PlayBodyParsers}
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, stubControllerComponents}

class ItemNetGrossMassFormProviderSpec extends FormSpec {

  val formProvider = new ItemNetGrossMassFormProvider()
  val form: Form[ItemNetGrossMassModel] = formProvider.form

  lazy val cc: PlayBodyParsers = stubControllerComponents().parsers
  implicit val stubFormBinding: FormBinding = cc.formBinding(cc.DefaultMaxTextLength)

  "when binding valid values" - {
    "must bind successfully" - {
      "netMass = 6dp, grossMass = 0dp" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "9999999999.999999", formProvider.grossMassField -> "9999999999999999")

        formProvider.enhancedBindFromRequest().value.value mustBe
          ItemNetGrossMassModel(BigDecimal("9999999999.999999"), BigDecimal("9999999999999999"))
      }
      "netMass = 6dp, grossMass = 1" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "0.000001", formProvider.grossMassField -> "1")

        formProvider.enhancedBindFromRequest().value.value mustBe
          ItemNetGrossMassModel(BigDecimal("0.000001"), BigDecimal("1"))
      }
      "netMass = 5dp, grossMass = 1dp" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "99999999999.99999", formProvider.grossMassField -> "999999999999999.9")

        formProvider.enhancedBindFromRequest().value.value mustBe
          ItemNetGrossMassModel(BigDecimal("99999999999.99999"), BigDecimal("999999999999999.9"))
      }
      "netMass = 4dp, grossMass = 3dp" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "999999999999.9999", formProvider.grossMassField -> "9999999999999.999")

        formProvider.enhancedBindFromRequest().value.value mustBe
          ItemNetGrossMassModel(BigDecimal("999999999999.9999"), BigDecimal("9999999999999.999"))
      }
      "netMass = 2dp, grossMass = 2dp" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "99999999999999.99", formProvider.grossMassField -> "99999999999999.99")

        formProvider.enhancedBindFromRequest().value.value mustBe
          ItemNetGrossMassModel(BigDecimal("99999999999999.99"), BigDecimal("99999999999999.99"))
      }
    }

    "must return form errors" - {
      "netMass has 7 decimal places and grossMass 16 digits and 2 decimal" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "0.0000001", formProvider.grossMassField -> "9999999999999999.99")

        formProvider.enhancedBindFromRequest().errors mustBe Seq(
          FormError(formProvider.netMassField, "itemNetGrossMass.netMass.error.decimals", List(formProvider.maxDecimalPlaces)),
          FormError(formProvider.grossMassField, "itemNetGrossMass.grossMass.error.high")
        )
      }
      "netMass is less than 0 and grossMass not a number" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "-25", formProvider.grossMassField -> "AB12345")

        formProvider.enhancedBindFromRequest().errors mustBe Seq(
          FormError(formProvider.netMassField, "itemNetGrossMass.netMass.error.low"),
          FormError(formProvider.grossMassField, "itemNetGrossMass.grossMass.error.invalid")
        )
      }
      "netMass and grossMass are empty" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "", formProvider.grossMassField -> "")

        formProvider.enhancedBindFromRequest().errors mustBe Seq(
          FormError(formProvider.netMassField, "itemNetGrossMass.netMass.error.required"),
          FormError(formProvider.grossMassField, "itemNetGrossMass.grossMass.error.required")
        )
      }
      "netMass is empty and grossMass is valid" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "", formProvider.grossMassField -> "12")

        formProvider.enhancedBindFromRequest().errors mustBe Seq(
          FormError(formProvider.netMassField, "itemNetGrossMass.netMass.error.required")
        )
      }
      "grossMass is empty and netMass is invalid" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "12", formProvider.grossMassField -> "ABCD")

        formProvider.enhancedBindFromRequest().errors mustBe Seq(
          FormError(formProvider.grossMassField, "itemNetGrossMass.grossMass.error.invalid")
        )
      }
      "grossMass is less than netMass" in {
        implicit val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest(POST, "/")
          .withFormUrlEncodedBody(formProvider.netMassField -> "100", formProvider.grossMassField -> "12")

        formProvider.enhancedBindFromRequest().errors mustBe Seq(
          FormError(formProvider.grossMassField, "itemNetGrossMass.grossMass.error.lessThanNetMass")
        )
      }
    }
  }
}
