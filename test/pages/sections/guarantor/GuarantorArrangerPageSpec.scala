/*
 * Copyright 2024 HM Revenue & Customs
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
import models.response.InvalidGuarantorTypeException
import models.response.emcsTfe.GuarantorType._
import models.response.emcsTfe.{GetMovementResponse, GuarantorType, TraderModel}
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{NoGuarantorRequired, NoGuarantorRequiredUkToEu}
import play.api.test.FakeRequest

class GuarantorArrangerPageSpec extends SpecBase {

  val guarantorTraders: Seq[TraderModel] = maxGetMovementResponse.movementGuarantee.guarantorTrader.get

  private def movementResponseWithGuaranteeSet(guarantorType: GuarantorType): GetMovementResponse = {
    maxGetMovementResponse.copy(movementGuarantee = maxGetMovementResponse.movementGuarantee.copy(guarantorType))
  }

  "getValueFromIE801" - {
    "must return Some(Consignor)" - {
      "when guarantor trader is defined and has a trader name (getting the first guarantor)" in {
        GuarantorArrangerPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = movementResponseWithGuaranteeSet(Consignor))) mustBe Some(GuarantorArranger.Consignor)
      }
    }

    "must return Some(Consignee)" - {
      "when guarantor trader is Consignee" in {
        GuarantorArrangerPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = movementResponseWithGuaranteeSet(Consignee))) mustBe Some(GuarantorArranger.Consignee)
      }
    }

    "must return Some(GoodsOwner)" - {
      "when guarantor trader is Owner" in {
        GuarantorArrangerPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = movementResponseWithGuaranteeSet(Owner))) mustBe Some(GuarantorArranger.GoodsOwner)
      }
    }

    "must return Some(Transporter)" - {
      "when guarantor trader is Transporter" in {
        GuarantorArrangerPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = movementResponseWithGuaranteeSet(Transporter))) mustBe Some(GuarantorArranger.Transporter)
      }
    }

    "must return Some(NoGuarantorRequiredUkToEu)" - {
      "when guarantor trader is NoGuarantor and destination type is UKtoEU" in {
        GuarantorArrangerPage.getValueFromIE801(dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers,
          ern = testNorthernIrelandWarehouseKeeperErn,
          movementDetails = movementResponseWithGuaranteeSet(NoGuarantor))) mustBe Some(NoGuarantorRequiredUkToEu)
      }

      "when guarantor trader is GuarantorNotRequired and destination type is UKtoEU" in {
        GuarantorArrangerPage.getValueFromIE801(dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers,
          ern = testNorthernIrelandWarehouseKeeperErn,
          movementDetails = movementResponseWithGuaranteeSet(GuarantorNotRequired))) mustBe Some(NoGuarantorRequiredUkToEu)
      }
    }

    "must return Some(NoGuarantorRequired)" - {
      "when guarantor trader is NoGuarantor and destination type is NOT UKtoEU" in {
        GuarantorArrangerPage.getValueFromIE801(dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers,
          ern = testErn,
          movementDetails = movementResponseWithGuaranteeSet(NoGuarantor))) mustBe Some(NoGuarantorRequired)
      }

      "when guarantor trader is GuarantorNotRequired and destination type is NOT UKtoEU" in {
        GuarantorArrangerPage.getValueFromIE801(dataRequest(
          request = FakeRequest(),
          answers = emptyUserAnswers,
          ern = testErn,
          movementDetails = movementResponseWithGuaranteeSet(GuarantorNotRequired))) mustBe Some(NoGuarantorRequired)
      }
    }

    "must throw an exception" - {
      "when guarantor trader is not Consignor, Consignee, Owner, Transporter, NoGuarantor or GuarantorNotRequired" in {
        intercept[InvalidGuarantorTypeException](GuarantorArrangerPage.getValueFromIE801(dataRequest(FakeRequest(),
          movementDetails = movementResponseWithGuaranteeSet(JointConsignorConsignee)))).getMessage mustBe s"Invalid guarantor type from IE801: $JointConsignorConsignee"
      }
    }
  }
}
