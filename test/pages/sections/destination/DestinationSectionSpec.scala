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
import play.api.test.FakeRequest
import utils.JsonOptionFormatter

class DestinationSectionSpec extends SpecBase with UserAddressFixtures with JsonOptionFormatter {

  "isCompleted" - {

    "when DestinationWarehouseExcisePage is given" - {

      "when DestinationConsigneeDetails is false" - {

        "when Destination Business Name and address is given" - {

          "must return true" in {

            val userAnswers = emptyUserAnswers
              .set(DestinationWarehouseExcisePage, "excise")
              .set(DestinationConsigneeDetailsPage, false)
              .set(DestinationBusinessNamePage, "business name")
              .set(DestinationAddressPage, userAddressModelMax)

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            DestinationSection.isCompleted mustBe true
          }
        }

        "when Destination Business Name is NOT given" - {

          "must return false" in {

            val userAnswers = emptyUserAnswers
              .set(DestinationWarehouseExcisePage, "excise")
              .set(DestinationConsigneeDetailsPage, false)
              .set(DestinationAddressPage, userAddressModelMax)

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            DestinationSection.isCompleted mustBe false
          }
        }

        "when Destination Address is NOT given" - {

          "must return false" in {

            val userAnswers = emptyUserAnswers
              .set(DestinationWarehouseExcisePage, "excise")
              .set(DestinationConsigneeDetailsPage, false)
              .set(DestinationBusinessNamePage, "business name")

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            DestinationSection.isCompleted mustBe false
          }
        }

        "when Destination Business Name and Destination Address are NOT given" - {

          "must return false" in {

            val userAnswers = emptyUserAnswers
              .set(DestinationWarehouseExcisePage, "excise")
              .set(DestinationConsigneeDetailsPage, false)

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            DestinationSection.isCompleted mustBe false
          }
        }
      }

      "when DestinationConsigneeDetails is true" - {

        "must return true" in {

          val userAnswers = emptyUserAnswers
            .set(DestinationWarehouseExcisePage, "excise")
            .set(DestinationConsigneeDetailsPage, true)

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

          DestinationSection.isCompleted mustBe true
        }
      }
    }

    "when DestinationWarehouseVatPage is given" - {

      "when DestinationDetailsChoice is true" - {

        "when DestinationConsigneeDetails is false" - {

          "when Destination Business Name and address is given" - {

            "must return true" in {

              val userAnswers = emptyUserAnswers
                .set(DestinationWarehouseVatPage, "vat")
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationBusinessNamePage, "business name")
                .set(DestinationAddressPage, userAddressModelMax)

              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

              DestinationSection.isCompleted mustBe true
            }
          }

          "when Destination Business Name is NOT given" - {

            "must return false" in {

              val userAnswers = emptyUserAnswers
                .set(DestinationWarehouseVatPage, "vat")
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationAddressPage, userAddressModelMax)

              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

              DestinationSection.isCompleted mustBe false
            }
          }

          "when Destination Address is NOT given" - {

            "must return false" in {

              val userAnswers = emptyUserAnswers
                .set(DestinationWarehouseVatPage, "vat")
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationBusinessNamePage, "business name")

              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

              DestinationSection.isCompleted mustBe false
            }
          }

          "when Destination Business Name and Destination Address are NOT given" - {

            "must return false" in {

              val userAnswers = emptyUserAnswers
                .set(DestinationWarehouseVatPage, "vat")
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, false)

              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

              DestinationSection.isCompleted mustBe false
            }
          }
        }

        "when DestinationConsigneeDetails is true" - {

          "must return true" in {

            val userAnswers = emptyUserAnswers
              .set(DestinationWarehouseVatPage, "vat")
              .set(DestinationDetailsChoicePage, true)
              .set(DestinationConsigneeDetailsPage, true)

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            DestinationSection.isCompleted mustBe true
          }

        }
      }

      "when DestinationDetailsChoice is false" - {

        "must return true" in {

          val userAnswers = emptyUserAnswers
            .set(DestinationWarehouseVatPage, "vat")
            .set(DestinationDetailsChoicePage, false)

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

          DestinationSection.isCompleted mustBe true
        }
      }

      "when DestinationDetailsChoice is NOT given" - {

        "must return true" in {

          val userAnswers = emptyUserAnswers
            .set(DestinationWarehouseVatPage, "vat")

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

          DestinationSection.isCompleted mustBe false
        }
      }
    }

    "when DestinationWarehouseVatPage is NOT given (optional)" - {

      "when DestinationDetailsChoice is true" - {

        "when DestinationConsigneeDetails is false" - {

          "when Destination Business Name and address is given" - {

            "must return true" in {

              val userAnswers = emptyUserAnswers
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationBusinessNamePage, "business name")
                .set(DestinationAddressPage, userAddressModelMax)

              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

              DestinationSection.isCompleted mustBe true
            }
          }

          "when Destination Business Name is NOT given" - {

            "must return false" in {

              val userAnswers = emptyUserAnswers
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationAddressPage, userAddressModelMax)

              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

              DestinationSection.isCompleted mustBe false
            }
          }

          "when Destination Address is NOT given" - {

            "must return false" in {

              val userAnswers = emptyUserAnswers
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationBusinessNamePage, "business name")

              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

              DestinationSection.isCompleted mustBe false
            }
          }

          "when Destination Business Name and Destination Address are NOT given" - {

            "must return false" in {

              val userAnswers = emptyUserAnswers
                .set(DestinationDetailsChoicePage, true)
                .set(DestinationConsigneeDetailsPage, false)

              implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

              DestinationSection.isCompleted mustBe false
            }
          }
        }

        "when DestinationConsigneeDetails is true" - {

          "must return true" in {

            val userAnswers = emptyUserAnswers
              .set(DestinationDetailsChoicePage, true)
              .set(DestinationConsigneeDetailsPage, true)

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            DestinationSection.isCompleted mustBe true
          }

        }
      }

      "when DestinationDetailsChoice is false" - {

        "must return true" in {

          val userAnswers = emptyUserAnswers
            .set(DestinationDetailsChoicePage, false)

          implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

          DestinationSection.isCompleted mustBe true
        }
      }
    }
  }
}

