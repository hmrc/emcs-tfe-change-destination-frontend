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

package models

import base.SpecBase
import models.UserType._

class UserTypeSpec extends SpecBase {

  ".apply" - {

    Seq(
      "GBRC" -> GreatBritainRegisteredConsignor,
      "XIRC" -> NorthernIrelandRegisteredConsignor,
      "XIPA" -> NorthernIrelandCertifiedConsignor,
      "XIPC" -> NorthernIrelandTemporaryCertifiedConsignor,
      "GBWK" -> GreatBritainWarehouseKeeper,
      "XIWK" -> NorthernIrelandWarehouseKeeper,
      "XI00" -> NorthernIrelandWarehouse,
      "GB00" -> GreatBritainWarehouse,
      "HMRC" -> Unknown
    ).foreach { ernToUserType =>

      s"should return ${ernToUserType._2} when the ERN starts with ${ernToUserType._1}" in {

        UserType.apply(ernToUserType._1) mustBe ernToUserType._2
      }

    }
  }

  def ernTypeTest(isNorthernIrelandErnResult: Boolean, isGreatBritainErnResult: Boolean)(userType: UserType): Unit = {

    s"for UserType: $userType" - {

      "when isNorthernIrelandErn is called" - {

        s"must return $isNorthernIrelandErnResult" in {

          userType.isNorthernIrelandErn mustBe isNorthernIrelandErnResult
        }
      }

      "when isGreatBritainErn is called" - {

        s"must return $isGreatBritainErnResult" in {

          userType.isGreatBritainErn mustBe isGreatBritainErnResult
        }
      }
    }
  }

  Seq(
    GreatBritainRegisteredConsignor -> (false, true),
    NorthernIrelandRegisteredConsignor -> (true, false),
    NorthernIrelandCertifiedConsignor -> (true, false),
    NorthernIrelandTemporaryCertifiedConsignor -> (true, false),
    GreatBritainWarehouseKeeper -> (false, true),
    NorthernIrelandWarehouseKeeper -> (true, false),
    NorthernIrelandWarehouse -> (true, false),
    GreatBritainWarehouse -> (false, true),
    Unknown -> (false, false)
  ).foreach { userTypeToExpectedResults =>
    ernTypeTest(userTypeToExpectedResults._2._1, userTypeToExpectedResults._2._2)(userTypeToExpectedResults._1)
  }
}
