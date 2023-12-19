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

import base.SpecBase
import fixtures.ItemFixtures
import forms.behaviours.FieldBehaviours
import forms.sections.items.ItemPackagingQuantityFormProvider._
import play.api.data.FormError
import play.api.test.FakeRequest

class ItemPackagingQuantityFormProviderSpec extends SpecBase with FieldBehaviours with ItemFixtures{

  val form = new ItemPackagingQuantityFormProvider()(testGoodsTypeWine)(messages(FakeRequest()))


  "when binding valid values" - {
    "must bind successfully" - {
      "0dp" in {
        val boundForm = form.bind(Map(fieldName -> "1"))
        boundForm.errors mustBe Seq()
        boundForm.value mustBe Some("1")
      }

      "max possible combination of numerics (15 precision)" in {
        val boundForm = form.bind(Map(fieldName -> "999999999999999"))
        boundForm.errors mustBe Seq()
        boundForm.value mustBe Some("999999999999999")
      }
    }
  }

  "when binding invalid values" - {
    "when empty" - {
      "must error when binding the form" in {
        val boundForm = form.bind(Map(fieldName -> ""))
        boundForm.errors mustBe Seq(FormError(fieldName, requiredErrorKey(testGoodsTypeWine)(messages(FakeRequest()))))
      }
    }

    "when non-numeric" - {
      "must error when binding the form" in {
        val boundForm = form.bind(Map(fieldName -> "test"))
        boundForm.errors mustBe Seq(FormError(fieldName, invalidCharactersErrorKey))
      }
    }

    "when too many decimal places" - {
      "must error when binding the form" in {
        val boundForm = form.bind(Map(fieldName -> "1.2"))
        boundForm.errors mustBe Seq(FormError(fieldName, decimalPlacesErrorKey, Seq()))
      }
    }
  }
}
