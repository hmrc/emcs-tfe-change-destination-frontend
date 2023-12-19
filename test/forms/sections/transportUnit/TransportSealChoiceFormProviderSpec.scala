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

package forms.sections.transportUnit

import base.SpecBase
import forms.behaviours.BooleanFieldBehaviours
import models.sections.transportUnit.TransportUnitType
import models.sections.transportUnit.TransportUnitType._
import play.api.data.FormError
import play.api.test.FakeRequest

class TransportSealChoiceFormProviderSpec extends SpecBase with BooleanFieldBehaviours {

  val requiredKeyContainer = "Select yes if there is a commercial seal on this container"
  val requiredKeyTractor = "Select yes if there is a commercial seal on this tractor"
  val requiredKeyFixed = "Select yes if there is a commercial seal on this fixed transport installation"
  val requiredKeyVehicle = "Select yes if there is a commercial seal on this vehicle"
  val requiredKeyTrailer = "Select yes if there is a commercial seal on this trailer"
  val invalidKey = "error.boolean"

  def form(transportUnitType: TransportUnitType) = new TransportSealChoiceFormProvider()(transportUnitType)(messages(FakeRequest()))

  Map(Tractor -> requiredKeyTractor,
    Container -> requiredKeyContainer,
    FixedTransport -> requiredKeyFixed,
    Vehicle -> requiredKeyVehicle,
    Trailer -> requiredKeyTrailer
  ).foreach { case (transportUnitType, errorMessage) =>
    s".value for $transportUnitType" - {

      val fieldName = "value"

      behave like booleanField(
        form(transportUnitType),
        fieldName,
        invalidError = FormError(fieldName, invalidKey)
      )

      behave like mandatoryField(
        form(transportUnitType),
        fieldName,
        requiredError = FormError(fieldName, errorMessage)
      )
    }
  }
}
