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

package pages.sections.guarantor

import base.SpecBase
import models.UserAddress
import models.requests.DataRequest
import models.response.emcsTfe.GuarantorType.NoGuarantor
import models.response.emcsTfe.{GuarantorType, MovementGuaranteeModel}
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.sections.info.movementScenario.MovementScenario._
import models.sections.journeyType.HowMovementTransported.FixedTransportInstallations
import models.sections.transportUnit.TransportUnitType.{FixedTransport, Tractor}
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import pages.sections.transportUnit.TransportUnitTypePage
import play.api.test.FakeRequest

class GuarantorSectionSpec extends SpecBase {

  ".isCompleted" - {

    "when the original movement has no guarantor" - {

      "when the movement now requires a guarantor" - {

        "must return true" - {

          "when Consignor is selected" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers
                .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                .set(GuarantorArrangerPage, Consignor),
              ern = testGreatBritainWarehouseErn,
              movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
            )
            GuarantorSection.isCompleted mustBe true
          }

          "when Consignee is selected (GB to GB, spirits, requires guarantor)" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers
                .set(DestinationTypePage, UkTaxWarehouse.GB)
                .set(GuarantorArrangerPage, Consignee),
              ern = testGreatBritainWarehouseErn,
              movementDetails = maxGetMovementResponse.copy(
                movementGuarantee = MovementGuaranteeModel(NoGuarantor, None),
                items = Seq(maxGetMovementResponse.items.head.copy(productCode = "S200"))
              )
            )
            GuarantorSection.isCompleted mustBe true
          }

          Seq(GoodsOwner, Transporter) foreach {
            arranger =>

              s"when another option is selected and the rest of the Guarantor section is completed - ${arranger.getClass.getSimpleName.stripSuffix("$")}" in {
                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk)
                    .set(GuarantorArrangerPage, arranger)
                    .set(GuarantorVatPage, "")
                    .set(GuarantorAddressPage, UserAddress(None, None, "", "", "")),
                  ern = testGreatBritainWarehouseErn,
                  movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
                )
                GuarantorSection.isCompleted mustBe true
              }
          }
        }

        "must return false" - {

          "when guarantor no answers exist" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
              ern = testGreatBritainWarehouseErn,
              movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
            )
            GuarantorSection.isCompleted mustBe false
          }

          Seq(GoodsOwner, Transporter) foreach {
            arranger =>

              s"when another option is selected and the rest of the Guarantor section is not completed - ${arranger.getClass.getSimpleName.stripSuffix("$")}" in {
                implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
                  emptyUserAnswers
                    .set(GuarantorArrangerPage, arranger)
                    .set(GuarantorNamePage, "")
                    .set(GuarantorVatPage, ""),
                  movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
                )
                GuarantorSection.isCompleted mustBe false
              }
          }
        }
      }

      "when the movement does not mandate a guarantor" - {

        "when the section has been reviewed and user has selected to change details" - {

          "must return true" - {

            "when Consignor is selected" in {
              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(GuarantorReviewPage, ChangeAnswers)
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
                  .set(GuarantorArrangerPage, Consignor),
                ern = testGreatBritainWarehouseErn,
                movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
              )
              GuarantorSection.isCompleted mustBe true
            }

            "when Consignee is selected" in {
              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(GuarantorReviewPage, ChangeAnswers)
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
                  .set(GuarantorArrangerPage, Consignee),
                ern = testGreatBritainWarehouseErn,
                movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
              )
              GuarantorSection.isCompleted mustBe true
            }

            Seq(GoodsOwner, Transporter) foreach {
              arranger =>

                s"when another option is selected and the rest of the Guarantor section is completed - ${arranger.getClass.getSimpleName.stripSuffix("$")}" in {
                  implicit val dr: DataRequest[_] = dataRequest(
                    request = FakeRequest(),
                    answers = emptyUserAnswers
                      .set(GuarantorReviewPage, ChangeAnswers)
                      .set(DestinationTypePage, UkTaxWarehouse.GB)
                      .set(GuarantorArrangerPage, arranger)
                      .set(GuarantorVatPage, "")
                      .set(GuarantorAddressPage, UserAddress(None, None, "", "", "")),
                    ern = testGreatBritainWarehouseErn,
                    movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
                  )
                  GuarantorSection.isCompleted mustBe true
                }
            }
          }

          "must return false" - {

            "when guarantor no answers exist" in {

              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(GuarantorReviewPage, ChangeAnswers)
                  .set(DestinationTypePage, UkTaxWarehouse.GB),
                ern = testGreatBritainWarehouseErn,
                movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
              )
              GuarantorSection.isCompleted mustBe false
            }

            Seq(GoodsOwner, Transporter) foreach {
              arranger =>

                s"when another option is selected and the rest of the Guarantor section is not completed - ${arranger.getClass.getSimpleName.stripSuffix("$")}" in {
                  implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
                    emptyUserAnswers
                      .set(GuarantorReviewPage, ChangeAnswers)
                      .set(DestinationTypePage, UkTaxWarehouse.GB)
                      .set(GuarantorArrangerPage, arranger)
                      .set(GuarantorNamePage, "")
                      .set(GuarantorVatPage, ""),
                    movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
                  )
                  GuarantorSection.isCompleted mustBe false
                }
            }
          }
        }

        "when the section has been reviewed and the user has selected to keep the same details" - {

          "must return true" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers
                .set(GuarantorReviewPage, KeepAnswers)
                .set(DestinationTypePage, UkTaxWarehouse.GB),
              ern = testGreatBritainWarehouseErn,
              movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
            )

            GuarantorSection.isCompleted mustBe true
          }
        }
      }
    }

    "when the original movement does have a guarantor already" - {

      "when the section has been reviewed and user has selected to change details" - {

        "must return true" - {

          "when Consignor is selected" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers
                .set(GuarantorReviewPage, ChangeAnswers)
                .set(DestinationTypePage, UkTaxWarehouse.GB)
                .set(GuarantorArrangerPage, Consignor),
              ern = testGreatBritainWarehouseErn,
              movementDetails = maxGetMovementResponse
            )
            GuarantorSection.isCompleted mustBe true
          }

          "when Consignee is selected" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers
                .set(GuarantorReviewPage, ChangeAnswers)
                .set(DestinationTypePage, UkTaxWarehouse.GB)
                .set(GuarantorArrangerPage, Consignee),
              ern = testGreatBritainWarehouseErn,
              movementDetails = maxGetMovementResponse
            )
            GuarantorSection.isCompleted mustBe true
          }

          Seq(GoodsOwner, Transporter) foreach {
            arranger =>

              s"when another option is selected and the rest of the Guarantor section is completed - ${arranger.getClass.getSimpleName.stripSuffix("$")}" in {
                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(GuarantorReviewPage, ChangeAnswers)
                    .set(DestinationTypePage, UkTaxWarehouse.GB)
                    .set(GuarantorArrangerPage, arranger)
                    .set(GuarantorVatPage, "")
                    .set(GuarantorAddressPage, UserAddress(None, None, "", "", "")),
                  ern = testGreatBritainWarehouseErn,
                  movementDetails = maxGetMovementResponse
                )
                GuarantorSection.isCompleted mustBe true
              }
          }
        }

        "must return true (from IE801 values)" - {

          "when no changed guarantor answers exist" in {

            implicit val dr: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              answers = emptyUserAnswers
                .set(GuarantorReviewPage, ChangeAnswers)
                .set(DestinationTypePage, UkTaxWarehouse.GB),
              ern = testGreatBritainWarehouseErn,
              movementDetails = maxGetMovementResponse
            )
            GuarantorSection.isCompleted mustBe true
          }

          Seq(GoodsOwner, Transporter) foreach { arranger =>

            s"when another option is selected and the rest of the Guarantor section is not completed - ${arranger.getClass.getSimpleName.stripSuffix("$")}" in {

              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
                emptyUserAnswers
                  .set(GuarantorReviewPage, ChangeAnswers)
                  .set(DestinationTypePage, UkTaxWarehouse.GB)
                  .set(GuarantorArrangerPage, arranger),
                movementDetails = maxGetMovementResponse
              )
              GuarantorSection.isCompleted mustBe true
            }
          }
        }
      }

      "when the section has been reviewed and the user has selected to keep the same details" - {

        "must return true" - {

          implicit val dr: DataRequest[_] = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers
              .set(GuarantorReviewPage, KeepAnswers)
              .set(DestinationTypePage, UkTaxWarehouse.GB),
            ern = testGreatBritainWarehouseErn,
            movementDetails = maxGetMovementResponse
          )
          GuarantorSection.isCompleted mustBe true
        }
      }
    }
  }

  ".doNotRetrieveValuesFromIE801" - {

    "when the Guarantor section requires new details (Movement changed to 'Export' where Consignee was previously Guarantor)" - {

      "must return true" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
          ern = testGreatBritainWarehouseErn,
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(GuarantorType.Consignee, None))
        )
        GuarantorSection.doNotRetrieveValuesFromIE801 mustBe true
      }
    }

    "when the User has selected to supply Guarantor details (GuarantorRequired is 'true' from UserAnswers)" - {

      "must return true" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(GuarantorRequiredPage, true))
        GuarantorSection.doNotRetrieveValuesFromIE801 mustBe true
      }
    }

    "when any other scenario occurs" - {

      "must return false" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        GuarantorSection.doNotRetrieveValuesFromIE801 mustBe false
      }
    }

  }

  ".requiresNewGuarantorDetails" - {

    "when the Guarantor section requires new details (Movement changed to 'Export' where Consignee was previously Guarantor)" - {

      "must return true" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
          ern = testGreatBritainWarehouseErn,
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(GuarantorType.Consignee, None))
        )
        GuarantorSection.requiresNewGuarantorDetails mustBe true
      }
    }

    "when any other scenario occurs" - {

      "must return false" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        GuarantorSection.requiresNewGuarantorDetails mustBe false
      }
    }
  }

  ".euNoGuarantorRequired" - {

    Seq(
      DirectDelivery,
      RegisteredConsignee,
      EuTaxWarehouse,
      TemporaryRegisteredConsignee,
      CertifiedConsignee,
      TemporaryCertifiedConsignee
    ).foreach { movementScenario =>

      s"movement scenario is $movementScenario" - {

        "when the movement is from Northern Ireland to the EU, the movement is by Fixed Transport Installations and all items are energy" - {

          Seq(testNorthernIrelandWarehouseKeeperErn, testNorthernIrelandDutyPaidErn, testErn).foreach { ern =>

            s"when the ern is $ern" - {

              "must return true" in {

                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, movementScenario)
                    .set(HowMovementTransportedPage, FixedTransportInstallations),
                  ern = ern,
                  movementDetails = maxGetMovementResponse.copy(items = maxGetMovementResponse.items.map(_.copy(productCode = "E430")))
                )
                GuarantorSection.euNoGuarantorRequired mustBe true
              }
            }
          }

          s"when the ern is $testTemporaryRegisteredConsignee" - {

            "must return false" in {

              implicit val dr: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, movementScenario)
                  .set(HowMovementTransportedPage, FixedTransportInstallations)
                  .set(TransportUnitTypePage(testIndex1), FixedTransport)
                  .set(TransportUnitTypePage(testIndex2), FixedTransport),
                ern = testTemporaryRegisteredConsignee,
                movementDetails = maxGetMovementResponse.copy(items = maxGetMovementResponse.items.map(_.copy(productCode = "E430")))
              )
              GuarantorSection.euNoGuarantorRequired mustBe false
            }
          }
        }

        "when the movement is from Northern Ireland to the EU, the movement is NOT by all Fixed Transport and all items are energy" - {

          Seq(testNorthernIrelandWarehouseKeeperErn, testNorthernIrelandDutyPaidErn, testErn).foreach { ern =>

            s"when the ern is $ern" - {

              "must return false" in {

                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, movementScenario)
                    .set(HowMovementTransportedPage, FixedTransportInstallations)
                    .set(TransportUnitTypePage(testIndex1), FixedTransport)
                    .set(TransportUnitTypePage(testIndex2), Tractor),
                  ern = ern,
                  movementDetails = maxGetMovementResponse.copy(items = maxGetMovementResponse.items.map(_.copy(productCode = "E430")))
                )
                GuarantorSection.euNoGuarantorRequired mustBe false
              }
            }
          }
        }

        "when the movement is from Northern Ireland to the EU, the movement is by Fixed Transport but NOT all items are energy" - {

          Seq(testNorthernIrelandWarehouseKeeperErn, testNorthernIrelandDutyPaidErn, testErn).foreach { ern =>

            s"when the ern is $ern" - {

              "must return false" in {

                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, movementScenario)
                    .set(HowMovementTransportedPage, FixedTransportInstallations)
                    .set(TransportUnitTypePage(testIndex1), FixedTransport),
                  ern = ern,
                  movementDetails = maxGetMovementResponse.copy(
                    items = maxGetMovementResponse.items.map(_.copy(productCode = "E430")) :+ maxGetMovementResponse.items.head.copy(productCode = "S200")
                  )
                )
                GuarantorSection.euNoGuarantorRequired mustBe false
              }
            }
          }
        }
      }
    }

    Seq(
      ExemptedOrganisation,
      UnknownDestination
    ).foreach { movementScenario =>

      s"movement scenario is $movementScenario" - {

        "when the movement is from Northern Ireland to the EU, the movement is by Fixed Transport Installations and all items are energy" - {

          Seq(testNorthernIrelandWarehouseKeeperErn, testNorthernIrelandDutyPaidErn, testErn, testTemporaryRegisteredConsignee).foreach { ern =>

            s"when the ern is $ern" - {

              "must return false" in {

                implicit val dr: DataRequest[_] = dataRequest(
                  request = FakeRequest(),
                  answers = emptyUserAnswers
                    .set(DestinationTypePage, movementScenario)
                    .set(HowMovementTransportedPage, FixedTransportInstallations)
                    .set(TransportUnitTypePage(testIndex1), FixedTransport)
                    .set(TransportUnitTypePage(testIndex2), FixedTransport),
                  ern = ern,
                  movementDetails = maxGetMovementResponse.copy(items = maxGetMovementResponse.items.map(_.copy(productCode = "E430")))
                )
                GuarantorSection.euNoGuarantorRequired mustBe false
              }
            }
          }
        }
      }
    }
  }

  ".ukNoGuarantorRequired" - {

    "when the movement is from the UK to the UK and all items are beer or wine" - {

      "must return true" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB),
          ern = testGreatBritainWarehouseErn
        )
        GuarantorSection.ukNoGuarantorRequired mustBe true
      }
    }

    "when any other scenario occurs" - {

      "must return false" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        GuarantorSection.ukNoGuarantorRequired mustBe false
      }
    }
  }

  ".requiresGuarantorToBeProvided" - {

    "when the Guarantor section requires new details (Movement changed to 'Export' where Consignee was previously Guarantor)" - {

      "must return true" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers.set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
          ern = testGreatBritainWarehouseErn,
          movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(GuarantorType.Consignee, None))
        )
        GuarantorSection.requiresGuarantorToBeProvided mustBe true
      }
    }

    "when the movement is from Northern Ireland to the EU, the movement is by Fixed Transport Installations and all items are energy" - {

      "must return false" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers
            .set(HowMovementTransportedPage, FixedTransportInstallations),
          ern = testNorthernIrelandWarehouseKeeperErn,
          movementDetails = maxGetMovementResponse.copy(items = maxGetMovementResponse.items.map(_.copy(productCode = "E430"))
          )
        )
        GuarantorSection.requiresGuarantorToBeProvided mustBe false
      }
    }

    "when the movement is from the UK to the UK and all items are beer or wine" - {

      "must return false" in {

        implicit val dr: DataRequest[_] = dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB),
          ern = testGreatBritainWarehouseErn
        )
        GuarantorSection.requiresGuarantorToBeProvided mustBe false
      }
    }

    "when any other scenario occurs" - {

      "must return true" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        GuarantorSection.requiresGuarantorToBeProvided mustBe true
      }
    }
  }

}
