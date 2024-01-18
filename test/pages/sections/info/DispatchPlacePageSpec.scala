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

package pages.sections.info

import base.SpecBase
import models.sections.info.DispatchPlace
import play.api.test.FakeRequest

class DispatchPlacePageSpec extends SpecBase {

  "getValueFromIE801" - {

    "must return Some(DispatchPlace.GreatBritain)" - {
      "when the first two characters of the competentAuthorityDispatchOfficeReferenceNumber are GB" in {
        DispatchPlacePage.getValueFromIE801(
          dataRequest(
            FakeRequest(),
            movementDetails = maxGetMovementResponse.copy(competentAuthorityDispatchOfficeReferenceNumber = Some("GB123456"))
          )) mustBe Some(DispatchPlace.GreatBritain)
      }
    }

    "must return Some(DispatchPlace.NorthernIreland)" - {
      "when the first two characters of the competentAuthorityDispatchOfficeReferenceNumber are XI" in {
        DispatchPlacePage.getValueFromIE801(
          dataRequest(
            FakeRequest(),
            movementDetails = maxGetMovementResponse.copy(competentAuthorityDispatchOfficeReferenceNumber = Some("XI123456"))
          )) mustBe Some(DispatchPlace.NorthernIreland)
      }
    }

    "must return None" - {

      "when the first two characters of the competentAuthorityDispatchOfficeReferenceNumber are not XI or GB" in {
        DispatchPlacePage.getValueFromIE801(
          dataRequest(
            FakeRequest(),
            movementDetails = maxGetMovementResponse.copy(competentAuthorityDispatchOfficeReferenceNumber = Some("AB123456"))
          )) mustBe None
      }

      "when the competentAuthorityDispatchOfficeReferenceNumber is empty" in {
        DispatchPlacePage.getValueFromIE801(
          dataRequest(
            FakeRequest(),
            movementDetails = maxGetMovementResponse.copy(competentAuthorityDispatchOfficeReferenceNumber = None)
          )) mustBe None
      }

    }
    
  }

}
