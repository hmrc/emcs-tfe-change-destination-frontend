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
import forms.behaviours.StringFieldBehaviours
import models.GoodsType.Wine
import models.response.referenceData.ItemPackaging
import play.api.data.{Form, FormError}
import play.api.test.FakeRequest

class ItemSelectPackagingFormProviderSpec extends SpecBase with StringFieldBehaviours with ItemFixtures {

  val requiredKey = "itemSelectPackaging.error.required"

  val form: Form[ItemPackaging] = new ItemSelectPackagingFormProvider().apply(Wine, testItemPackagingTypes)(messages(FakeRequest()))

  ".apply" - {

    val fieldName = "packaging"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "AE"
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(Wine.toSingularOutput()(messages(FakeRequest()))))
    )

    "not bind when the value provided is not in the item packaging list" in {
      val result = form.bind(Map(fieldName -> "ZZ")).apply(fieldName)
      result.errors mustEqual Seq(FormError(fieldName, requiredKey, Seq(Wine.toSingularOutput()(messages(FakeRequest())))))
    }
  }
}
