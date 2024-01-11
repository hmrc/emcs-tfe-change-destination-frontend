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

package pages.sections.destination

import base.SpecBase
import fixtures.UserAddressFixtures
import models.requests.DataRequest
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario._
import pages.QuestionPage
import pages.sections.info.DestinationTypePage
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import utils.JsonOptionFormatter
import viewmodels.taskList._

class DestinationSectionSpec extends SpecBase with UserAddressFixtures with JsonOptionFormatter {

  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "status" - {

    "must return Complete" - {
      "when keep answers has been selected" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(DestinationReviewPage, KeepAnswers)
          )
        DestinationSection.isCompleted mustBe true
      }
    }

    "when shouldStartFlowAtDestinationWarehouseExcise" - {
      "must return Completed" - {
        "when mandatory pages have an answer and DestinationConsigneeDetailsPage = true, and the section has been reviewed" in {
          Seq(
            GbTaxWarehouse,
            EuTaxWarehouse
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseExcise === true,
                s"shouldStartFlowAtDestinationWarehouseExcise returned false for MovementScenario $destinationTypePageAnswer"
              )

              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationWarehouseExcisePage, "")
                .set(DestinationConsigneeDetailsPage, true)
                .set(DestinationReviewPage, ChangeAnswers)
              )

              DestinationSection.status mustBe Completed
          }
        }

        "when mandatory pages have an answer and DestinationConsigneeDetailsPage = false, and the section has been reviewed" in {
          Seq(
            GbTaxWarehouse,
            EuTaxWarehouse
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseExcise === true,
                s"shouldStartFlowAtDestinationWarehouseExcise returned false for MovementScenario $destinationTypePageAnswer"
              )

              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationWarehouseExcisePage, "")
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationBusinessNamePage, "")
                .set(DestinationAddressPage, testUserAddress)
                .set(DestinationReviewPage, ChangeAnswers)
              )

              DestinationSection.status mustBe Completed
          }
        }
      }

      "must return InProgress" - {
        "when some, but not all, mandatory pages have an answer and DestinationConsigneeDetailsPage = true" in {
          Seq(
            GbTaxWarehouse,
            EuTaxWarehouse
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseExcise === true,
                s"shouldStartFlowAtDestinationWarehouseExcise returned false for MovementScenario $destinationTypePageAnswer"
              )

              val baseUserAnswers = emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationWarehouseExcisePage, "")
                .set(DestinationConsigneeDetailsPage, true)
                .set(DestinationReviewPage, ChangeAnswers)

              Seq(
                DestinationConsigneeDetailsPage,
              ).foreach {
                page =>
                  implicit val dr: DataRequest[_] = dataRequest(request, baseUserAnswers
                    .remove(page)
                  )

                  DestinationSection.status mustBe InProgress
              }
          }
        }

        "when some, but not all, mandatory pages have an answer and DestinationConsigneeDetailsPage = false" in {
          Seq(
            GbTaxWarehouse,
            EuTaxWarehouse
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseExcise === true,
                s"shouldStartFlowAtDestinationWarehouseExcise returned false for MovementScenario $destinationTypePageAnswer"
              )

              val baseUserAnswers = emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationWarehouseExcisePage, "")
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationBusinessNamePage, "")
                .set(DestinationAddressPage, testUserAddress)
                .set(DestinationReviewPage, ChangeAnswers)

              Seq[QuestionPage[_]](
                DestinationConsigneeDetailsPage,
                DestinationBusinessNamePage,
                DestinationAddressPage
              ).foreach {
                page =>
                  implicit val dr: DataRequest[_] =
                    dataRequest(request, baseUserAnswers.remove(page), movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None))

                  DestinationSection.status mustBe InProgress
              }
          }
        }
      }

      "must return NotStarted" - {
        "when no mandatory pages have an answer" in {
          Seq(
            GbTaxWarehouse,
            EuTaxWarehouse
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseExcise === true,
                s"shouldStartFlowAtDestinationWarehouseExcise returned false for MovementScenario $destinationTypePageAnswer"
              )

              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationReviewPage, ChangeAnswers),
                movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None)
              )

              DestinationSection.status mustBe NotStarted
          }
        }
      }
    }

    "when shouldStartFlowAtDestinationWarehouseVat" - {
      "must return Completed" - {
        "when mandatory pages have an answer, DestinationDetailsChoicePage = true and DestinationConsigneeDetailsPage = true " +
          "and the section has been reviewed" in {
          Seq(
            RegisteredConsignee,
            TemporaryRegisteredConsignee,
            ExemptedOrganisation
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
              )

              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, true)
                .set(DestinationReviewPage, ChangeAnswers)
              )

              DestinationSection.status mustBe Completed
          }
        }

        "when mandatory pages have an answer, DestinationDetailsChoicePage = true and DestinationConsigneeDetailsPage = false " +
          "and the section has been reviewed" in {
          Seq(
            RegisteredConsignee,
            TemporaryRegisteredConsignee,
            ExemptedOrganisation
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
              )

              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationBusinessNamePage, "")
                .set(DestinationAddressPage, testUserAddress)
                .set(DestinationReviewPage, ChangeAnswers)
              )

              DestinationSection.status mustBe Completed
          }
        }

        "when mandatory pages have an answer and DestinationDetailsChoicePage = false " +
          "and the section has been reviewed" in {
          Seq(
            RegisteredConsignee,
            TemporaryRegisteredConsignee,
            ExemptedOrganisation
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
              )

              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationDetailsChoicePage, false)
                .set(DestinationReviewPage, ChangeAnswers)
              )

              DestinationSection.status mustBe Completed
          }
        }
      }

      "must return InProgress" - {
        "when some, but not all, mandatory pages have an answer, DestinationDetailsChoicePage = true, DestinationConsigneeDetailsPage = false" in {
          Seq(
            RegisteredConsignee,
            TemporaryRegisteredConsignee,
            ExemptedOrganisation
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
              )

              val baseUserAnswers = emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationBusinessNamePage, "")
                .set(DestinationAddressPage, testUserAddress)
                .set(DestinationReviewPage, ChangeAnswers)

              Seq(DestinationBusinessNamePage, DestinationAddressPage).foreach {
                page =>
                  implicit val dr: DataRequest[_] = dataRequest(request, baseUserAnswers
                    .remove(page), movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None)
                  )

                  DestinationSection.status mustBe InProgress
              }

          }
        }

        "when some, but not all, mandatory pages have an answer, DestinationDetailsChoicePage = true, DestinationConsigneeDetailsPage = missing" in {
          Seq(
            RegisteredConsignee,
            TemporaryRegisteredConsignee,
            ExemptedOrganisation
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
              )
              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationReviewPage, ChangeAnswers)
              )

              DestinationSection.status mustBe InProgress
          }

        }

        "when mandatory pages are missing but DestinationWarehouseVatPage has an answer" in {
          Seq(
            RegisteredConsignee,
            TemporaryRegisteredConsignee,
            ExemptedOrganisation
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
              )
              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationWarehouseVatPage, "")
                .set(DestinationReviewPage, ChangeAnswers)
              )

              DestinationSection.status mustBe InProgress
          }
        }
      }

      "must return NotStarted" - {
        "when no mandatory pages have an answer" in {
          Seq(
            RegisteredConsignee,
            TemporaryRegisteredConsignee,
            ExemptedOrganisation
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationWarehouseVat === true,
                s"shouldStartFlowAtDestinationWarehouseVat returned false for MovementScenario $destinationTypePageAnswer"
              )

              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationReviewPage, ChangeAnswers),
                movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None)
              )

              DestinationSection.status mustBe NotStarted
          }
        }
      }
    }

    "when shouldStartFlowAtDestinationBusinessName" - {
      "must return Completed" - {
        "when mandatory pages have an answer and the section has been reviewed" in {
          Seq(
            DirectDelivery
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationBusinessName === true,
                s"shouldStartFlowAtDestinationBusinessName returned false for MovementScenario $destinationTypePageAnswer"
              )
              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationBusinessNamePage, "")
                .set(DestinationAddressPage, testUserAddress)
                .set(DestinationReviewPage, ChangeAnswers)
              )

              DestinationSection.status mustBe Completed
          }
        }
      }

      "must return InProgress" - {
        "when some, but not all, mandatory pages have an answer" in {
          Seq(
            DirectDelivery
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationBusinessName === true,
                s"shouldStartFlowAtDestinationBusinessName returned false for MovementScenario $destinationTypePageAnswer"
              )

              val baseUserAnswers = emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationBusinessNamePage, "")
                .set(DestinationAddressPage, testUserAddress)
                .set(DestinationReviewPage, ChangeAnswers)


              Seq(DestinationBusinessNamePage, DestinationAddressPage).foreach {
                page =>
                  implicit val dr: DataRequest[_] = dataRequest(request, baseUserAnswers
                    .remove(page), movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None)
                  )

                  DestinationSection.status mustBe InProgress
              }
          }
        }
      }

      "must return NotStarted" - {
        "when no mandatory pages have an answer" in {
          Seq(
            DirectDelivery
          ).foreach {
            implicit destinationTypePageAnswer =>
              assert(
                DestinationSection.shouldStartFlowAtDestinationBusinessName === true,
                s"shouldStartFlowAtDestinationBusinessName returned false for MovementScenario $destinationTypePageAnswer"
              )
              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, destinationTypePageAnswer)
                .set(DestinationReviewPage, ChangeAnswers),
                movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None)
              )

              DestinationSection.status mustBe NotStarted
          }
        }
      }
    }

    "when canBeCompletedForTraderAndDestinationType = false" - {
      "must return NotStarted" in {
        MovementScenario.values
          .filterNot(Seq(GbTaxWarehouse, EuTaxWarehouse, RegisteredConsignee, TemporaryRegisteredConsignee, ExemptedOrganisation, DirectDelivery).contains)
          .foreach {
            movementScenario =>
              implicit val dr: DataRequest[_] = dataRequest(request, emptyUserAnswers
                .set(DestinationTypePage, movementScenario)
                .set(DestinationAddressPage, testUserAddress)
                .set(DestinationBusinessNamePage, "")
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationWarehouseExcisePage, "")
                .set(DestinationWarehouseVatPage, "")
                .set(DestinationReviewPage, ChangeAnswers),
                movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None)
              )

              DestinationSection.status mustBe NotStarted
          }
      }
    }

    "when DestinationTypePage is missing" - {
      "must return NotStarted" in {
        DestinationSection.status(dataRequest(request, emptyUserAnswers.set(DestinationReviewPage, ChangeAnswers), movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None))) mustBe NotStarted
      }
    }
  }
}

