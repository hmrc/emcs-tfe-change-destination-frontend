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

import forms.XSS_REGEX
import forms.behaviours.BooleanFieldBehaviours
import models.sections.items.ItemBrandNameModel
import play.api.data.FormError

class ItemBrandNameFormProviderSpec extends BooleanFieldBehaviours {

  val form = new ItemBrandNameFormProvider()()

  "when binding 'Yes'" - {

    "when brand name contains invalid characters" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          ItemBrandNameFormProvider.hasBrandNameField -> "true",
          ItemBrandNameFormProvider.brandNameField -> "<"
        ))

        boundForm.errors mustBe Seq(FormError(
          ItemBrandNameFormProvider.brandNameField,
          ItemBrandNameFormProvider.brandNameInvalid,
          Seq(XSS_REGEX)
        ))
      }
    }

    "when brand name is too long" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          ItemBrandNameFormProvider.hasBrandNameField -> "true",
          ItemBrandNameFormProvider.brandNameField -> "a" * (ItemBrandNameFormProvider.brandNameMaxLength + 1)
        ))

        boundForm.errors mustBe Seq(FormError(
          ItemBrandNameFormProvider.brandNameField,
          ItemBrandNameFormProvider.brandNameLength,
          Seq(ItemBrandNameFormProvider.brandNameMaxLength)
        ))
      }
    }

    "when brand name is valid" - {

      "must bind the form successfully when true with value" in {

        val boundForm = form.bind(Map(
          ItemBrandNameFormProvider.hasBrandNameField -> "true",
          ItemBrandNameFormProvider.brandNameField -> "brand"
        ))

        boundForm.errors mustBe Seq()

        boundForm.value mustBe Some(ItemBrandNameModel(hasBrandName = true, Some("brand")))
      }
    }
  }

  "when binding 'No'" - {

    "must bind the form successfully when false with value (should be transformed to None on bind)" in {

      val boundForm = form.bind(Map(
        ItemBrandNameFormProvider.hasBrandNameField -> "false",
        ItemBrandNameFormProvider.brandNameField -> "brand"
      ))

      boundForm.errors mustBe Seq()

      boundForm.value mustBe Some(ItemBrandNameModel(hasBrandName = false, None))
    }

    "must bind the form successfully when false with NO value" in {

      val boundForm = form.bind(Map(
        ItemBrandNameFormProvider.hasBrandNameField -> "false"
      ))

      boundForm.errors mustBe Seq()

      boundForm.value mustBe Some(ItemBrandNameModel(hasBrandName = false, None))
    }
  }
}
