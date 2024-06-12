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

package pages.sections.consignee

import base.SpecBase
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation, VatNumber}
import models.{ExemptOrganisationDetailsModel, UserAddress}
import play.api.test.FakeRequest

class ConsigneeSectionSpec extends SpecBase {
  "isCompleted" - {
    "must return true" - {
      "when user starts on ConsigneeExportInformation and selects only VAT number (and provided it)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber))
            .set(ConsigneeExportVatPage, testVatNumber)
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExportInformation and selects only EORI number (and provided it)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(EoriNumber))
            .set(ConsigneeExportEoriPage, testEoriNumber)
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExportInformation and selects both VAT / EORI number (and provided then)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
            .set(ConsigneeExportVatPage, testVatNumber)
            .set(ConsigneeExportEoriPage, testEoriNumber)
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExportInformation and selects Not provided" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(NoInformation))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExcise" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExcisePage, "")
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", "")),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExemptOrganisation" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("", ""))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", "")),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when user answers doesn't contain ConsigneeExportUkEu, ConsigneeExcise or ConsigneeExemptOrganisation" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers,
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExportInformation and selects only VAT number (and has NOT provided it)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", "")),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExportInformation and selects only EORI number (and has NOT provided it)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(EoriNumber))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", "")),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe false
      }

      "when user starts on ConsigneeExportInformation and selects both VAT / EORI number (and has NOT provided VAT)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
            .set(ConsigneeExportEoriPage, testEoriNumber)
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe false
      }

      "when user starts on ConsigneeExportInformation and selects both VAT / EORI number (and has NOT provided EORI)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
            .set(ConsigneeExportVatPage, testVatNumber)
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", "")),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExportInformation and selects both VAT / EORI number (and has NOT provided either)" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExcise, not all answers are in IE801" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExcisePage, ""),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExemptOrganisation, not all answers are in IE801" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("", "")),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe false
      }
    }
  }

  "hasConsigneeChanged" - {
    "should return true" - {
      Seq(
        ConsigneeExcisePage -> emptyUserAnswers.set(ConsigneeExcisePage, "excise number changed"),
        ConsigneeExemptOrganisationPage -> emptyUserAnswers.set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("member state changed", "certificate serial number changed")),
        ConsigneeBusinessNamePage -> emptyUserAnswers.set(ConsigneeBusinessNamePage, "business name changed"),
        ConsigneeAddressPage -> emptyUserAnswers.set(ConsigneeAddressPage, testUserAddress),
        ConsigneeExportVatPage -> emptyUserAnswers.set(ConsigneeExportVatPage, "vat no"),
        ConsigneeExportEoriPage -> emptyUserAnswers.set(ConsigneeExportEoriPage, "eori no")
      ).foreach { pageToUserAnswers =>

        s"when the answer for ${pageToUserAnswers._1} has changed" in {

          ConsigneeSection.hasConsigneeChanged(dataRequest(FakeRequest(), pageToUserAnswers._2)) mustBe true
        }
      }
    }

    "should return false" - {

      "when all the user answers are the same as 801" in {

        val userAnswers = emptyUserAnswers
          .set(ConsigneeExcisePage, maxGetMovementResponse.consigneeTrader.get.traderExciseNumber.get)
          .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel(maxGetMovementResponse.memberStateCode.get, maxGetMovementResponse.serialNumberOfCertificateOfExemption.get))
          .set(ConsigneeBusinessNamePage, maxGetMovementResponse.consigneeTrader.get.traderName.get)
          .set(ConsigneeAddressPage, maxGetMovementResponse.consigneeTrader.get.address.map(UserAddress.userAddressFromTraderAddress).get)
          .set(ConsigneeExportInformationPage, Set(EoriNumber))

        ConsigneeSection.hasConsigneeChanged(dataRequest(FakeRequest(), userAnswers)) mustBe false
      }

      "when there are no user answers for the Consignee section" in {

        ConsigneeSection.hasConsigneeChanged(dataRequest(FakeRequest())) mustBe false
      }
    }
  }
}
