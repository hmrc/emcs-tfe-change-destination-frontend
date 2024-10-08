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
import fixtures.messages.sections.destination.DestinationWarehouseExciseMessages.English
import forms.XSS_REGEX
import forms.behaviours.StringFieldBehaviours
import models.requests.DataRequest
import models.sections.info.ChangeType
import models.sections.info.ChangeType.ChangeConsignee
import models.sections.info.movementScenario.MovementScenario
import play.api.data.FormError
import play.api.data.validation.{Invalid, Valid, ValidationResult}
import play.api.i18n.Messages
import play.api.test.FakeRequest

class DestinationWarehouseExciseFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val requiredKey = "destinationWarehouseExcise.error.required"
  val lengthKey = "destinationWarehouseExcise.error.length"
  val maxLength = 16
  val invalidCharactersKey = "destinationWarehouseExcise.error.invalidCharacter"

  val form = new DestinationWarehouseExciseFormProvider().apply(MovementScenario.CertifiedConsignee, ChangeConsignee)(dataRequest(FakeRequest()))

  implicit val msgs: Messages = messages(Seq(English.lang))

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "0" * maxLength
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithXSSCharacters(
      form,
      fieldName,
      FormError(fieldName, invalidCharactersKey, Seq(XSS_REGEX))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error Messages" - {

    s"when output for language code '${English.lang.code}'" - {

      "have the correct required error message" in {

        msgs(requiredKey) mustBe
          English.errorRequired
      }

      "have the correct length error message" in {

        msgs(lengthKey) mustBe
          English.errorLength
      }

      "have the correct invalidCharacters error message" in {

        msgs(invalidCharactersKey) mustBe
          English.errorInvalidCharacters
      }
    }
  }

  "must transform the inputted Excise ID" - {
    "removing any spaces" in {
      implicit val dr: DataRequest[_] = dataRequest(FakeRequest())
      val form = new DestinationWarehouseExciseFormProvider().apply(MovementScenario.UkTaxWarehouse.GB, ChangeType.ChangeConsignee)
      val boundForm = form.bind(Map("value" -> "GB 0 012 3456789"))

      boundForm.value mustBe Some("GB00123456789")
    }
    "converting to uppercase" in {
      implicit val dr: DataRequest[_] = dataRequest(FakeRequest())
      val form = new DestinationWarehouseExciseFormProvider().apply(MovementScenario.UkTaxWarehouse.GB, ChangeType.ChangeConsignee)
      val boundForm = form.bind(Map("value" -> "gb 0 012 3456789"))

      boundForm.value mustBe Some("GB00123456789")
    }
  }

  "inputIsValidForDestinationType" - {
    "for destination tax warehouse in GB" - {
      "must return Valid when the input starts with GB00" in {
        val result =
          new DestinationWarehouseExciseFormProvider()
            .inputIsValidForDestinationType(MovementScenario.UkTaxWarehouse.GB)
            .apply("GB00123456789")

        result mustBe Valid
      }
      "must return Invalid when the input starts with XI00" in {
        val result: ValidationResult =
          new DestinationWarehouseExciseFormProvider()
            .inputIsValidForDestinationType(MovementScenario.UkTaxWarehouse.GB)
            .apply("XI00123456789")

        result mustBe a[Invalid]
        result.asInstanceOf[Invalid].errors.flatMap(_.messages.map(msgs(_))) mustBe Seq(English.errorInvalidGB00)
      }
      "must return Invalid when the input doesn't start with GB00" in {
        val result: ValidationResult =
          new DestinationWarehouseExciseFormProvider()
            .inputIsValidForDestinationType(MovementScenario.UkTaxWarehouse.GB)
            .apply("FR00123456789")

        result mustBe a[Invalid]
        result.asInstanceOf[Invalid].errors.flatMap(_.messages.map(msgs(_))) mustBe Seq(English.errorInvalidGB00)
      }
      "must return Invalid when the input is empty" in {
        val result: ValidationResult =
          new DestinationWarehouseExciseFormProvider()
            .inputIsValidForDestinationType(MovementScenario.UkTaxWarehouse.GB)
            .apply("")

        result.asInstanceOf[Invalid].errors.flatMap(_.messages.map(msgs(_))) mustBe Seq(English.errorInvalidGB00)
      }
    }
    "for destination tax warehouse in XI" - {
      "must return Valid when the input starts with XI00" in {
        val result =
          new DestinationWarehouseExciseFormProvider()
            .inputIsValidForDestinationType(MovementScenario.UkTaxWarehouse.NI)
            .apply("XI00123456789")

        result mustBe Valid
      }
      "must return Invalid when the input starts with GB00" in {
        val result: ValidationResult =
          new DestinationWarehouseExciseFormProvider()
            .inputIsValidForDestinationType(MovementScenario.UkTaxWarehouse.NI)
            .apply("GB00123456789")

        result mustBe a[Invalid]
        result.asInstanceOf[Invalid].errors.flatMap(_.messages.map(msgs(_))) mustBe Seq(English.errorInvalidXI00)
      }
      "must return Invalid when the input doesn't start with XI00" in {
        val result: ValidationResult =
          new DestinationWarehouseExciseFormProvider()
            .inputIsValidForDestinationType(MovementScenario.UkTaxWarehouse.NI)
            .apply("FR00123456789")

        result mustBe a[Invalid]
        result.asInstanceOf[Invalid].errors.flatMap(_.messages.map(msgs(_))) mustBe Seq(English.errorInvalidXI00)
      }
      "must return Invalid when the input is empty" in {
        val result: ValidationResult =
          new DestinationWarehouseExciseFormProvider()
            .inputIsValidForDestinationType(MovementScenario.UkTaxWarehouse.NI)
            .apply("")

        result.asInstanceOf[Invalid].errors.flatMap(_.messages.map(msgs(_))) mustBe Seq(English.errorInvalidXI00)
      }
    }
    "for other destination types" - {
      MovementScenario.values.filterNot(MovementScenario.UkTaxWarehouse.values.contains).foreach {
        movementScenario =>
          s"when destination type is $movementScenario" - {
            "must return Valid when the input doesn't start with XI or GB" in {
              val result =
                new DestinationWarehouseExciseFormProvider()
                  .inputIsValidForDestinationType(movementScenario)
                  .apply("FR00123456789")

              result mustBe Valid
            }
            "must return Invalid when the input starts with GB" in {
              val result: ValidationResult =
                new DestinationWarehouseExciseFormProvider()
                  .inputIsValidForDestinationType(movementScenario)
                  .apply("GB00123456789")

              result.asInstanceOf[Invalid].errors.flatMap(_.messages.map(msgs(_))) mustBe Seq(English.errorInvalidXIOrGB)
            }
            "must return Invalid when the input starts with XI" in {
              val result: ValidationResult =
                new DestinationWarehouseExciseFormProvider()
                  .inputIsValidForDestinationType(movementScenario)
                  .apply("XI00123456789")

              result.asInstanceOf[Invalid].errors.flatMap(_.messages.map(msgs(_))) mustBe Seq(English.errorInvalidXIOrGB)
            }
          }
      }
    }
  }

  "inputIsValidForChangeType" - {
    "for movement scenarios that are tax warehouses or registered consignee" - {
      MovementScenario.taxWarehouses :+ MovementScenario.RegisteredConsignee foreach { movementScenario =>
        s"when movement scenario is $movementScenario" - {
          "and change type is Destination" - {
            "must return Valid when the input different to the existing excise ID" in {
              implicit val dr: DataRequest[_] = dataRequest(FakeRequest())
              val result =
                new DestinationWarehouseExciseFormProvider()
                  .inputIsValidForChangeType(movementScenario, ChangeType.Destination)
                  .apply("id")

              result mustBe Valid
            }
            "must return Valid when there is no existing excise ID" in {
              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None))
              val result =
                new DestinationWarehouseExciseFormProvider()
                  .inputIsValidForChangeType(movementScenario, ChangeType.Destination)
                  .apply("id")

              result mustBe Valid
            }
            "must return Invalid when the input is the same as the existing excise ID" in {
              implicit val dr: DataRequest[_] = dataRequest(FakeRequest())
              val result: ValidationResult =
                new DestinationWarehouseExciseFormProvider()
                  .inputIsValidForChangeType(movementScenario, ChangeType.Destination)
                  .apply(dr.request.movementDetails.deliveryPlaceTrader.get.traderExciseNumber.get)

              result mustBe a[Invalid]
              result.asInstanceOf[Invalid].errors.flatMap(_.messages.map(msgs(_))) mustBe Seq(English.errorSameAsExisting)
            }
          }
          "and change type is not Destination" - {
            "must return Valid" in {
              ChangeType.allValues.filterNot(_ == ChangeType.Destination) foreach { changeType =>
                implicit val dr: DataRequest[_] = dataRequest(FakeRequest())
                val result =
                  new DestinationWarehouseExciseFormProvider()
                    .inputIsValidForChangeType(movementScenario, changeType)
                    .apply("id")

                result mustBe Valid
              }
            }
          }
        }
      }
    }

    "for movement scenarios that are not tax warehouses or registered consignee" - {
      MovementScenario.values.filterNot(MovementScenario.taxWarehouses :+ MovementScenario.RegisteredConsignee contains _) foreach { movementScenario =>
        s"when movement scenario is $movementScenario" - {
          "must return Valid" in {
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest())
            val result =
              new DestinationWarehouseExciseFormProvider()
                .inputIsValidForChangeType(movementScenario, ChangeType.Destination)
                .apply("id")

            result mustBe Valid
          }
        }
      }
    }
  }
}