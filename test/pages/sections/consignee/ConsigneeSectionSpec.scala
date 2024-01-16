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
import models.sections.ReviewAnswer
import models.sections.ReviewAnswer.{ChangeAnswers, KeepAnswers}
import models.sections.consignee.{ConsigneeExportVat, ConsigneeExportVatType}
import models.{Enumerable, ExemptOrganisationDetailsModel, UserAddress}
import play.api.test.FakeRequest

class ConsigneeSectionSpec extends SpecBase with Enumerable.Implicits {
  "isCompleted" - {
    "must return true" - {
      "when user changes no answers" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers.set(ConsigneeReviewPage, ReviewAnswer.KeepAnswers))
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExportUkEu and selects yes" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeReviewPage, ChangeAnswers)
            .set(ConsigneeExportPage, true)
            .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.No, None, None))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExportUkEu and selects no" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeReviewPage, ChangeAnswers)
            .set(ConsigneeExportPage, false)
            .set(ConsigneeExcisePage, "")
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExcise" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeReviewPage, ChangeAnswers)
            .set(ConsigneeExcisePage, "")
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }
      "when user starts on ConsigneeExemptOrganisation" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeReviewPage, ChangeAnswers)
            .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("", ""))
            .set(ConsigneeBusinessNamePage, "")
            .set(ConsigneeAddressPage, UserAddress(None, "", "", ""))
        )
        ConsigneeSection.isCompleted mustBe true
      }

      "when keep answers has been selected" in {
        implicit val dr: DataRequest[_] =
          dataRequest(FakeRequest(),
            emptyUserAnswers
              .set(ConsigneeReviewPage, KeepAnswers)
          )
        ConsigneeSection.isCompleted mustBe true
      }
    }

    "must return false" - {
      "when user hasn't answered the Review page" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)
        ConsigneeSection.isCompleted mustBe false
      }
      "when user answers doesn't contain ConsigneeExportUkEu, ConsigneeExcise or ConsigneeExemptOrganisation" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers.set(ConsigneeReviewPage, ChangeAnswers),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExportUkEu, not all answers are in IE801" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeReviewPage, ChangeAnswers)
            .set(ConsigneeExportPage, true),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExcise, not all answers are in IE801" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeReviewPage, ChangeAnswers)
            .set(ConsigneeExcisePage, ""),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe false
      }
      "when user starts on ConsigneeExemptOrganisation, not all answers are in IE801" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ConsigneeReviewPage, ChangeAnswers)
            .set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("", "")),
          movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
        )
        ConsigneeSection.isCompleted mustBe false
      }
    }
  }

  "hasConsigneeChanged" - {
    "should return true" - {
      /*      isDifferentFromIE801(ConsigneeExcisePage),
      isDifferentFromIE801(ConsigneeExemptOrganisationPage),
      isDifferentFromIE801(ConsigneeBusinessNamePage),
      isDifferentFromIE801(ConsigneeAddressPage),
      isDifferentFromIE801(ConsigneeExportVatPage)*/
      Seq(
        ConsigneeExcisePage -> emptyUserAnswers.set(ConsigneeExcisePage, "excise number changed"),
        ConsigneeExemptOrganisationPage -> emptyUserAnswers.set(ConsigneeExemptOrganisationPage, ExemptOrganisationDetailsModel("member state changed", "certificate serial number changed")),
        ConsigneeBusinessNamePage -> emptyUserAnswers.set(ConsigneeBusinessNamePage, "business name changed"),
        ConsigneeAddressPage -> emptyUserAnswers.set(ConsigneeAddressPage, testUserAddress),
        ConsigneeExportVatPage -> emptyUserAnswers.set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesVatNumber, Some("vat number changed"), None))
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
          .set(ConsigneeExportVatPage, ConsigneeExportVat(ConsigneeExportVatType.YesEoriNumber, None, Some(maxGetMovementResponse.consigneeTrader.get.eoriNumber.get)))

        ConsigneeSection.hasConsigneeChanged(dataRequest(FakeRequest(), userAnswers)) mustBe false
      }

      "when there are no user answers for the Consignee section" in {

        ConsigneeSection.hasConsigneeChanged(dataRequest(FakeRequest())) mustBe false
      }
    }
  }
}
