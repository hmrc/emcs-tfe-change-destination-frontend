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
import models.sections.items
import models.sections.items.ItemDegreesPlatoModel
import play.api.data.FormError

class ItemDegreesPlatoFormProviderSpec extends BooleanFieldBehaviours {

  val form = new ItemDegreesPlatoFormProvider()()

  "when binding 'Yes'" - {

    "when degrees plato is non-numeric" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          ItemDegreesPlatoFormProvider.hasDegreesPlatoField -> "true",
          ItemDegreesPlatoFormProvider.degreesPlatoField -> "banana"
        ))

        boundForm.errors mustBe Seq(FormError(
          ItemDegreesPlatoFormProvider.degreesPlatoField,
          ItemDegreesPlatoFormProvider.nonNumericErrorKey
        ))
      }
    }

    "when degrees plato has too many decimal places (> 2DP)" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          ItemDegreesPlatoFormProvider.hasDegreesPlatoField -> "true",
          ItemDegreesPlatoFormProvider.degreesPlatoField -> "1.123"
        ))

        boundForm.errors mustBe Seq(FormError(
          ItemDegreesPlatoFormProvider.degreesPlatoField,
          ItemDegreesPlatoFormProvider.maxDecimalPlacesErrorKey,
          Seq(ItemDegreesPlatoFormProvider.maxDecimalPlacesValue)
        ))
      }
    }

    "when degrees plato is outside of range (< min)" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          ItemDegreesPlatoFormProvider.hasDegreesPlatoField -> "true",
          ItemDegreesPlatoFormProvider.degreesPlatoField -> (ItemDegreesPlatoFormProvider.minValue - 0.01).toString()
        ))

        boundForm.errors mustBe Seq(FormError(
          ItemDegreesPlatoFormProvider.degreesPlatoField,
          ItemDegreesPlatoFormProvider.rangeErrorKey,
          Seq(ItemDegreesPlatoFormProvider.minValue, ItemDegreesPlatoFormProvider.maxValue)
        ))
      }
    }

    "when degrees plato is outside of range (> max)" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          ItemDegreesPlatoFormProvider.hasDegreesPlatoField -> "true",
          ItemDegreesPlatoFormProvider.degreesPlatoField -> (ItemDegreesPlatoFormProvider.maxValue + 0.01).toString()
        ))

        boundForm.errors mustBe Seq(FormError(
          ItemDegreesPlatoFormProvider.degreesPlatoField,
          ItemDegreesPlatoFormProvider.rangeErrorKey,
          Seq(ItemDegreesPlatoFormProvider.minValue, ItemDegreesPlatoFormProvider.maxValue)
        ))
      }
    }

    "when degrees plato is valid" - {

      "must bind the form successfully when true with value" in {

        val boundForm = form.bind(Map(
          ItemDegreesPlatoFormProvider.hasDegreesPlatoField -> "true",
          ItemDegreesPlatoFormProvider.degreesPlatoField -> "5"
        ))

        boundForm.errors mustBe Seq()

        boundForm.value mustBe Some(items.ItemDegreesPlatoModel(hasDegreesPlato = true, Some(BigDecimal(5))))
      }
    }
  }

  "when binding 'No'" - {

    "must bind the form successfully when false with value (should be transformed to None on bind)" in {

      val boundForm = form.bind(Map(
        ItemDegreesPlatoFormProvider.hasDegreesPlatoField -> "false",
        ItemDegreesPlatoFormProvider.degreesPlatoField -> "5"
      ))

      boundForm.errors mustBe Seq()

      boundForm.value mustBe Some(ItemDegreesPlatoModel(hasDegreesPlato = false, None))
    }

    "must bind the form successfully when false with NO value" in {

      val boundForm = form.bind(Map(
        ItemDegreesPlatoFormProvider.hasDegreesPlatoField -> "false"
      ))

      boundForm.errors mustBe Seq()

      boundForm.value mustBe Some(ItemDegreesPlatoModel(hasDegreesPlato = false, None))
    }
  }
}
