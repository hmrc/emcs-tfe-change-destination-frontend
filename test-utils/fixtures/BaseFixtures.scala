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

package fixtures

import models.UserAnswers
import models.response.referenceData.TraderKnownFacts
import play.api.mvc.Call

import java.time.Instant
import java.time.temporal.ChronoUnit

trait BaseFixtures {

  val testCredId: String = "credId"
  val testInternalId: String = "internalId"
  val testErn: String = "ern"
  val testArc: String = "arc"
  val testConfirmationReference = "UYVQBLMXCYK6HAEBZI7TSWAQ6XDTXFYU"
  val testReceiptDate = "2023-06-07T10:11:12.000"
  val testOnwardRoute = Call("GET", "/foo")

  val emptyUserAnswers: UserAnswers = UserAnswers(
    ern = testErn,
    arc = testArc,
    lastUpdated = Instant.now().truncatedTo(ChronoUnit.MILLIS)
  )

  val testMinTraderKnownFacts: TraderKnownFacts = TraderKnownFacts(
    traderName = "testTraderName",
    addressLine1 = None,
    addressLine2 = None,
    addressLine3 = None,
    addressLine4 = None,
    addressLine5 = None,
    postcode = None
  )

}
