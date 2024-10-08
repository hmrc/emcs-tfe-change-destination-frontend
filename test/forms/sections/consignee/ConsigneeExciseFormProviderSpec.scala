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
import fixtures.messages.sections.consignee.ConsigneeExciseMessages.English
import forms.behaviours.StringFieldBehaviours
import models.CountryModel
import models.requests.DataRequest
import models.sections.info.ChangeType
import models.sections.info.ChangeType.ChangeConsignee
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{EuTaxWarehouse, TemporaryCertifiedConsignee, TemporaryRegisteredConsignee, UkTaxWarehouse}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.sections.info.DestinationTypePage
import play.api.data.FormError
import play.api.data.validation.{Invalid, Valid, ValidationResult}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class ConsigneeExciseFormProviderSpec extends StringFieldBehaviours with GuiceOneAppPerSuite with SpecBase {

  val fieldName = "value"
  val fixedLength = 13

  val niTemporaryRegisteredConsignee: DataRequest[AnyContentAsEmpty.type] = dataRequest(
    request = FakeRequest(),
    answers = emptyUserAnswers
      .set(DestinationTypePage, TemporaryRegisteredConsignee),
    ern = testNorthernIrelandWarehouseKeeperErn
  )

  val niTemporaryCertifiedConsignee: DataRequest[AnyContentAsEmpty.type] = dataRequest(
    request = FakeRequest(),
    answers = emptyUserAnswers
      .set(DestinationTypePage, TemporaryCertifiedConsignee),
    ern = testNorthernIrelandWarehouseKeeperErn
  )

  "ConsigneeExciseFormProvider" - {
    implicit val msgs: Messages = messages(Seq(English.lang))

    "when a value is not provided" - {
      "for a Northern Ireland Temporary Registered Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(niTemporaryRegisteredConsignee)

          val boundForm = form.bind(Map(fieldName -> ""))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryRegisteredConsignee.error.noInput", Seq()))
        }
      }
      "for a Northern Ireland Temporary Certified Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(niTemporaryCertifiedConsignee)

          val boundForm = form.bind(Map(fieldName -> ""))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryCertifiedConsignee.error.noInput", Seq()))
        }
      }
      "for any other destination type" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(dataRequest(FakeRequest()))

          val boundForm = form.bind(Map(fieldName -> ""))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.noInput", Seq()))
        }
      }
    }

    "when the value entered is not the correct length" - {
      "for a Northern Ireland Temporary Registered Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(niTemporaryRegisteredConsignee)

          val boundForm = form.bind(Map(fieldName -> "12345678901234"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryRegisteredConsignee.error.length", Seq(fixedLength)))
        }
      }
      "for a Northern Ireland Temporary Certified Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(niTemporaryCertifiedConsignee)

          val boundForm = form.bind(Map(fieldName -> "12345678901234"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryCertifiedConsignee.error.length", Seq(fixedLength)))
        }
      }
      "for any other destination type" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(dataRequest(FakeRequest()))

          val boundForm = form.bind(Map(fieldName -> "12345678901234"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.length", Seq(fixedLength)))
        }
      }
    }

    "when the value contains invalid characters" - {
      "for a Northern Ireland Temporary Registered Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(niTemporaryRegisteredConsignee)

          val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+!"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryRegisteredConsignee.error.invalidCharacters", Seq("^(?s)(?=.*[A-Za-z0-9]).{1,}$")))
        }
      }
      "for a Northern Ireland Temporary Certified Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(niTemporaryCertifiedConsignee)

          val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+!"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryCertifiedConsignee.error.invalidCharacters", Seq("^(?s)(?=.*[A-Za-z0-9]).{1,}$")))
        }
      }
      "for any other destination type" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(dataRequest(FakeRequest()))

          val boundForm = form.bind(Map(fieldName -> "!@£$%^&*()_+!"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.invalidCharacters", Seq("^(?s)(?=.*[A-Za-z0-9]).{1,}$")))
        }
      }
    }

    "when the value is in the incorrect format" - {
      "for a Northern Ireland Temporary Registered Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(niTemporaryRegisteredConsignee)

          val boundForm = form.bind(Map(fieldName -> "0034567890123"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryRegisteredConsignee.error.format", Seq("[A-Z]{2}[a-zA-Z0-9]{11}")))
        }
      }
      "for a Northern Ireland Temporary Certified Consignee" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(niTemporaryCertifiedConsignee)

          val boundForm = form.bind(Map(fieldName -> "0034567890123"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.temporaryCertifiedConsignee.error.format", Seq("[A-Z]{2}[a-zA-Z0-9]{11}")))
        }
      }
      "for any other destination type" - {
        "must error with the expected msg key" in {
          val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(dataRequest(FakeRequest()))

          val boundForm = form.bind(Map(fieldName -> "0034567890123"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.format", Seq("[A-Z]{2}[a-zA-Z0-9]{11}")))
        }
      }
    }

    "when Destination is GB tax warehouse" - {

      val gbTaxWarehouseDestination: DataRequest[AnyContentAsEmpty.type] = dataRequest(
        request = FakeRequest(),
        answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB),
        ern = testNorthernIrelandWarehouseKeeperErn
      )

      val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.GB, ChangeConsignee)(gbTaxWarehouseDestination)

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
    }

    "when Destination is NI tax warehouse" - {

      val niTaxWarehouseDestination: DataRequest[AnyContentAsEmpty.type] = dataRequest(
        request = FakeRequest(),
        answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.NI),
        ern = testNorthernIrelandWarehouseKeeperErn
      )

      val form = new ConsigneeExciseFormProvider().apply(None, UkTaxWarehouse.NI, ChangeConsignee)(niTaxWarehouseDestination)

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

      val euTaxWarehouseDestination: DataRequest[AnyContentAsEmpty.type] = dataRequest(
        request = FakeRequest(),
        answers = emptyUserAnswers.set(DestinationTypePage, EuTaxWarehouse),
        ern = testNorthernIrelandWarehouseKeeperErn
      )

      val form = new ConsigneeExciseFormProvider().apply(Some(Seq(CountryModel("FR", "France"))), EuTaxWarehouse, ChangeConsignee)(euTaxWarehouseDestination)

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

      "when ERN starts with a code that is NOT in the EU member states list" - {

        "must return an error with correct message" in {

          val boundForm = form.bind(Map(fieldName -> "XE00123456789"))
          boundForm.errors.headOption mustBe Some(FormError(fieldName, "consigneeExcise.error.invalidMemberState", Seq()))
        }
      }

      "when ERN starts with a code that's in the EU member states list" - {

        "must return success" in {

          val boundForm = form.bind(Map(fieldName -> testEuErn))
          boundForm.errors.headOption mustBe None
          boundForm.value mustBe Some(testEuErn)
        }
      }
    }

    "inputIsValidForChangeType" - {
      "for movement scenarios that are tax warehouses or registered consignee" - {
        MovementScenario.taxWarehouses :+ MovementScenario.RegisteredConsignee foreach { movementScenario =>
          s"when movement scenario is $movementScenario" - {
            "and change type is ChangeConsignee" - {
              "must return Valid when the input different to the existing excise ID" in {
                implicit val dr: DataRequest[_] = dataRequest(FakeRequest())
                val result =
                  new ConsigneeExciseFormProvider()
                    .inputIsValidForChangeType(movementScenario, ChangeType.ChangeConsignee)
                    .apply("id")

                result mustBe Valid
              }
              "must return Valid when there is no existing excise ID" in {
                implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), movementDetails = maxGetMovementResponse.copy(consigneeTrader = None))
                val result =
                  new ConsigneeExciseFormProvider()
                    .inputIsValidForChangeType(movementScenario, ChangeType.ChangeConsignee)
                    .apply("id")

                result mustBe Valid
              }
              "must return Invalid when the input is the same as the existing excise ID" in {
                implicit val dr: DataRequest[_] = dataRequest(FakeRequest())
                val result: ValidationResult =
                  new ConsigneeExciseFormProvider()
                    .inputIsValidForChangeType(movementScenario, ChangeType.ChangeConsignee)
                    .apply(dr.request.movementDetails.consigneeTrader.get.traderExciseNumber.get)

                result mustBe a[Invalid]
                result.asInstanceOf[Invalid].errors.flatMap(_.messages.map(msgs(_))) mustBe Seq(English.errorSameAsExisting)
              }
            }
            "and change type is not Destination" - {
              "must return Valid" in {
                ChangeType.allValues.filterNot(_ == ChangeType.ChangeConsignee) foreach { changeType =>
                  implicit val dr: DataRequest[_] = dataRequest(FakeRequest())
                  val result =
                    new ConsigneeExciseFormProvider()
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
                new ConsigneeExciseFormProvider()
                  .inputIsValidForChangeType(movementScenario, ChangeType.ChangeConsignee)
                  .apply("id")

              result mustBe Valid
            }
          }
        }
      }
    }
  }
}
