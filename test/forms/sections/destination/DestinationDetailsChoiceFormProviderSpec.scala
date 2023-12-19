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

package forms.sections.destination

import base.SpecBase
import forms.behaviours.BooleanFieldBehaviours
import models.sections.info.movementScenario.MovementScenario.RegisteredConsignee
import play.api.data.{Form, FormError}
import play.api.test.FakeRequest

class DestinationDetailsChoiceFormProviderSpec extends SpecBase with BooleanFieldBehaviours {

  val requiredErrorMessage = "Select ‘yes’ to enter the address and business name of the registered consignee"
  val invalidErrorMessage = "error.boolean"

  val form: Form[Boolean] = new DestinationDetailsChoiceFormProvider().apply(RegisteredConsignee)(messages(FakeRequest()))

  ".value" - {

    val fieldNameKey = "value"

    behave like booleanField(
      form,
      fieldNameKey,
      invalidError = FormError(fieldNameKey, invalidErrorMessage)
    )

    behave like mandatoryField(
      form,
      fieldNameKey,
      requiredError = FormError(fieldNameKey, requiredErrorMessage)
    )
  }
}
