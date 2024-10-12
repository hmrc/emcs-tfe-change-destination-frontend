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

package viewmodels.checkAnswers.sections.firstTransporter

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorArrangerMessages.English
import models.requests.DataRequest
import org.scalamock.scalatest.MockFactory
import pages.sections.firstTransporter.{FirstTransporterAddressPage, FirstTransporterVatPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest

class FirstTransporterCheckAnswersHelperSpec extends SpecBase with MockFactory {
  trait Test {
    implicit val msgs: Messages = messages(Seq(English.lang))
    val helper = new FirstTransporterCheckAnswersHelper()
  }

  "summaryList" - {

    "must render 3 rows" - {
      "when all answers are entered for the first transporter section" in new Test {
        implicit val request: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(FirstTransporterVatPage, "GB123456789")
            .set(FirstTransporterAddressPage, testUserAddress.copy(businessName = Some("Business name")))
        )
        helper.summaryList(onReviewPage = false)(request, msgs).rows.length mustBe 3
      }

      "when no answers are entered for the first transporter section" in new Test {
        implicit val request: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers,
          movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None)
        )
        helper.summaryList(onReviewPage = false)(request, msgs).rows.length mustBe 3
      }
    }

  }
}
