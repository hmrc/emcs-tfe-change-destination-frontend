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

package forms.sections.info

import base.SpecBase
import forms.behaviours.OptionFieldBehaviours
import models.UserType.GreatBritainWarehouse
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import models.sections.info.movementScenario.MovementScenario
import play.api.data.FormError
import play.api.test.FakeRequest

class DestinationTypeFormProviderSpec extends OptionFieldBehaviours with SpecBase {

  implicit val dataRequest: DataRequest[_] = dataRequest(FakeRequest())

  val form = new DestinationTypeFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like optionsField[MovementScenario](
      form,
      fieldName,
      validValues = MovementScenario.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    Seq("GBWK", "XIWK").foreach {
      ern =>
        s"for ERN starting with $ern" - {
          val form = new DestinationTypeFormProvider()()(dataRequest(FakeRequest(), ern = s"${ern}123"))
          val requiredKey = "destinationType.error.required.movement"
          behave like mandatoryField(
            form,
            fieldName,
            requiredError = FormError(fieldName, requiredKey)
          )
        }
    }

    Seq("GBRC", "XIRC").foreach {
      ern =>
        s"for ERN starting with $ern" - {
          val form = new DestinationTypeFormProvider()()(dataRequest(FakeRequest(), ern = s"${ern}123"))
          val requiredKey = "destinationType.error.required.import"
          behave like mandatoryField(
            form,
            fieldName,
            requiredError = FormError(fieldName, requiredKey)
          )
        }
    }

    "for ERN starting with anything else" - {
      "not bind when key is not present at all" in {
        val result = intercept[InvalidUserTypeException] {
          val form = new DestinationTypeFormProvider()()(dataRequest(FakeRequest(), ern = "GB00123"))
          form.bind(emptyForm).apply(fieldName)
        }
        result.getMessage mustEqual s"[DestinationTypeFormProvider][apply] invalid UserType for CAM journey: $GreatBritainWarehouse"
      }

      "not bind blank values" in {
        val result = intercept[InvalidUserTypeException] {
          val form = new DestinationTypeFormProvider()()(dataRequest(FakeRequest(), ern = "GB00123"))
          form.bind(Map(fieldName -> "")).apply(fieldName)
        }
        result.getMessage mustEqual s"[DestinationTypeFormProvider][apply] invalid UserType for CAM journey: $GreatBritainWarehouse"
      }
    }
  }
}
