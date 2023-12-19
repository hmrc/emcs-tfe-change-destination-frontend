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
import fixtures.messages.sections.items.ItemBulkPackagingSelectMessages
import forms.behaviours.OptionFieldBehaviours
import models.GoodsType.Wine
import models.response.referenceData.BulkPackagingType
import models.sections.items.ItemBulkPackagingCode
import play.api.data.{Form, FormError}
import play.api.i18n.Messages

class ItemBulkPackagingSelectFormProviderSpec extends SpecBase with OptionFieldBehaviours with ItemFixtures {

  implicit val msgs: Messages = messages(Seq(ItemBulkPackagingSelectMessages.English.lang))

  val form: Form[BulkPackagingType] = new ItemBulkPackagingSelectFormProvider().apply(Wine, bulkPackagingTypes)

  ".value" - {

    val fieldName = "value"
    val requiredKey = "itemBulkPackagingSelect.error.required"

    behave like optionsField[ItemBulkPackagingCode](
      form,
      fieldName,
      validValues  = ItemBulkPackagingCode.values,
      invalidError = FormError(fieldName, "error.invalid", Seq(Wine.toSingularOutput()))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(Wine.toSingularOutput()))
    )
  }
}
