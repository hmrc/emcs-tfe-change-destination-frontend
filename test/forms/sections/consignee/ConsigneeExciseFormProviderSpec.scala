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

package forms.sections.consignee

import base.SpecBase
import forms.ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, UkTaxWarehouse}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.sections.info.DestinationTypePage
import play.api.data.FormError
import play.api.test.FakeRequest

class ConsigneeExciseFormProviderSpec extends StringFieldBehaviours with SpecBase with GuiceOneAppPerSuite {

  val fieldName = "value"

  "ConsigneeExciseFormProvider" - {
    val dr = dataRequest(request = FakeRequest(), ern = testGreatBritainWarehouseErn)
    val form = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = false)(dr)
    val dynamicForm = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = true)(dr)

    "when a value is not provided" - {
      "must error with the expected msg key" in {
        val boundForm = form.bind(Map(fieldName -> ""))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.noInput", Seq()))
      }
      "must error with the expected msg key for the dynamic form" in {
        val boundForm = dynamicForm.bind(Map(fieldName -> ""))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryConsignee.error.noInput", Seq()))
      }
    }

    "when the value is too long" - {
      "must error with the expected msg key" in {
        val maxLength = 13
        val boundForm = form.bind(Map(fieldName -> "A" * (maxLength + 1)))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.tooLong", Seq(maxLength)))
      }
      "must error with the expected msg key for the dynamic form" in {
        val maxLength = 16
        val boundForm = dynamicForm.bind(Map(fieldName -> "A" * (maxLength + 1)))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryConsignee.error.tooLong", Seq(maxLength)))
      }
    }

    "when the value contains invalid characters" - {
      "must error with the expected msg key" in {
        val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+"))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.invalidCharacters", Seq(ALPHANUMERIC_REGEX)))
      }
      "must error with the expected msg key for the dynamic form" in {
        val boundForm = dynamicForm.bind(Map(fieldName -> "!@£$%^&*()_+"))
        boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryConsignee.error.invalidCharacters", Seq(ALPHANUMERIC_REGEX)))
      }
    }

    "when ERN starts with lowercase" - {
      "must return success" in {
        val boundForm = form.bind(Map(fieldName -> "gbwk123456789"))
        boundForm.errors.headOption mustBe None
        boundForm.value mustBe Some("GBWK123456789")
      }
    }

    "when ERN contains spaces" - {
      "must return success" in {
        val boundForm = form.bind(Map(fieldName -> "GBWK1 2 3 4 56789"))
        boundForm.errors.headOption mustBe None
        boundForm.value mustBe Some("GBWK123456789")
      }
    }

    "when Destination is GB tax warehouse" - {

      val dr = dataRequest(request = FakeRequest(), answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB))
      val form = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = false)(dr)

      "when ERN does not start with GBWK or XIWK" - {

        "must return an error with correct message" in {
          Seq("GBRC123456789", "XIRC123456789", "XIPA123456789", "XIPB123456789", "XIPC123456789", "XIPD123456789").foreach(ern => {
            val boundForm = form.bind(Map(fieldName -> ern))
            boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.mustStartWithGBWKOrXIWK", Seq()))
          })
        }
      }

      "when ERN starts with GBWK or XIWK" - {

        "must return success" in {
          Seq("GBWK123456789", "XIWK123456789").foreach(ern => {
            val boundForm = form.bind(Map(fieldName -> ern))
            boundForm.errors.headOption mustBe None
            boundForm.value mustBe Some(ern)
          })
        }
      }

    }

    "when Destination is NI tax warehouse" - {

      val dr = dataRequest(request = FakeRequest(), answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.NI))
      val form = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = false)(dr)

      "when ERN does not start with XIWK" - {

        "must return an error with correct message" in {

          val boundForm = form.bind(Map(fieldName -> testGreatBritainWarehouseErn))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.mustStartWithXIWK", Seq()))
        }
      }

      "when ERN starts with XIWK" - {

        "must return success" in {

          val boundForm = form.bind(Map(fieldName -> testNorthernIrelandWarehouseKeeperErn))
          boundForm.errors.headOption mustBe None
          boundForm.value mustBe Some(testNorthernIrelandWarehouseKeeperErn)
        }
      }
    }

    "when Destination is EU tax warehouse" - {

      val dr = dataRequest(request = FakeRequest(), answers = emptyUserAnswers.set(DestinationTypePage, EuTaxWarehouse))
      val form = new ConsigneeExciseFormProvider().apply(isNorthernIrishTemporaryRegisteredConsignee = false)(dr)

      "when ERN starts with XI" - {

        "must return an error with correct message" in {

          val boundForm = form.bind(Map(fieldName -> testNorthernIrelandWarehouseKeeperErn))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.mustNotStartWithGBOrXI", Seq()))
        }
      }

      "when ERN starts with GB" - {

        "must return an error with correct message" in {

          val boundForm = form.bind(Map(fieldName -> testGreatBritainErn))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.mustNotStartWithGBOrXI", Seq()))
        }
      }

      "when ERN starts with anything else" - {

        "must return success" in {

          val boundForm = form.bind(Map(fieldName -> testEuErn))
          boundForm.errors.headOption mustBe None
          boundForm.value mustBe Some(testEuErn)
        }
      }
    }
  }
}
