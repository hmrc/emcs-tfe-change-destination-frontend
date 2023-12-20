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

import forms.behaviours.StringFieldBehaviours
import forms.sections.items.ItemDensityFormProvider._
import models.GoodsType
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest

class ItemDensityFormProviderSpec extends StringFieldBehaviours with GuiceOneAppPerSuite {
  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  val testGoodsType = GoodsType.Energy

  val form = new ItemDensityFormProvider()(testGoodsType)

  ".value" - {

    val fieldName = itemDensityFormField

    "must accept value with 5 total digits and 0 decimal places" in {
      form.bind(Map(itemDensityFormField -> "12345")).value must contain(BigDecimal("12345"))
    }
    "must accept value with 5 total digits and 1 decimal places" in {
      form.bind(Map(itemDensityFormField -> "1234.5")).value must contain(BigDecimal("1234.5"))
    }
    "must accept value with 5 total digits and 2 decimal places" in {
      form.bind(Map(itemDensityFormField -> "123.45")).value must contain(BigDecimal("123.45"))
    }
    "must accept value with 5 total digits and 2 decimal places and remove leading and trailing zeroes" in {
      form.bind(Map(itemDensityFormField -> "0123.450")).value must contain(BigDecimal("123.45"))
    }
    "must reject a non-numerical value" in {
      form.bind(Map(itemDensityFormField -> "a")).error(itemDensityFormField) must contain(
        FormError(itemDensityFormField, List(errorLengthKey))
      )
    }
    "must reject a value with more than 5 total digits" in {
      form.bind(Map(itemDensityFormField -> "123456")).error(itemDensityFormField) must contain(
        FormError(itemDensityFormField, List(errorLengthKey))
      )
    }
    "must reject a value with more than 5 total digits when there are decimal places" in {
      form.bind(Map(itemDensityFormField -> "1234.56")).error(itemDensityFormField) must contain(
        FormError(itemDensityFormField, List(errorLengthKey))
      )
    }
    "must reject a value with more than 2 decimal places" in {
      form.bind(Map(itemDensityFormField -> "12.345")).error(itemDensityFormField) must contain(
        FormError(itemDensityFormField, List(errorLengthKey))
      )
    }
    "must reject a negative value" in {
      form.bind(Map(itemDensityFormField -> "-1")).error(itemDensityFormField) must contain(
        FormError(itemDensityFormField, List(errorLengthKey))
      )
    }

    behave like mandatoryField(
      form,
      itemDensityFormField,
      requiredError = FormError(fieldName, errorRequiredKey, Seq(testGoodsType.toSingularOutput()))
    )
  }
}
