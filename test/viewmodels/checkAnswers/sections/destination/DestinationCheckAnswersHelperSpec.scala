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

package viewmodels.checkAnswers.sections.destination

import base.SpecBase
import fixtures.UserAddressFixtures
import models.UserAnswers
import models.requests.DataRequest
import models.sections.info.movementScenario.DestinationType.UnknownDestination
import org.scalamock.scalatest.MockFactory
import pages.sections.destination._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.all.FluentSummaryList

class DestinationCheckAnswersHelperSpec extends SpecBase with MockFactory with UserAddressFixtures {

  class Setup(userAnswers: UserAnswers) {
    implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(
      FakeRequest(),
      userAnswers,
      movementDetails = maxGetMovementResponse.copy(destinationType = UnknownDestination, deliveryPlaceTrader = None)
    )
    implicit val msgs: Messages = messages(FakeRequest())
    lazy val dispatchCheckAnswersSummary = new DestinationCheckAnswersHelper()
  }

  "CheckAnswersDestinationHelper" - {

    ".summaryList should return the correct rows when" - {

      "all questions have been answered" - {

        "the Excise Number has been provided" in new Setup(emptyUserAnswers
          .set(DestinationDetailsChoicePage, true)
          .set(DestinationConsigneeDetailsPage, false)
          .set(DestinationBusinessNamePage, "name")
          .set(DestinationWarehouseExcisePage, "excise")
          .set(DestinationAddressPage, userAddressModelMax)
        ) {

          val expectedResult = SummaryList(Seq(
            DestinationDetailsChoiceSummary.row(),
            DestinationConsigneeDetailsSummary.row(),
            Some(DestinationBusinessNameSummary.row()),
            DestinationWarehouseExciseSummary.row(),
            Some(DestinationAddressSummary.row())
          ).flatten).withCssClass("govuk-!-margin-bottom-9")


          dispatchCheckAnswersSummary.summaryList() mustBe expectedResult
        }

        "the VAT Number has been provided" in new Setup(emptyUserAnswers
          .set(DestinationDetailsChoicePage, true)
          .set(DestinationConsigneeDetailsPage, false)
          .set(DestinationBusinessNamePage, "name")
          .set(DestinationWarehouseVatPage, "vat")
          .set(DestinationAddressPage, userAddressModelMax)
        ) {

          val expectedResult = SummaryList(Seq(
            DestinationDetailsChoiceSummary.row(),
            DestinationConsigneeDetailsSummary.row(),
            Some(DestinationBusinessNameSummary.row()),
            DestinationWarehouseVatSummary.row(),
            Some(DestinationAddressSummary.row())
          ).flatten).withCssClass("govuk-!-margin-bottom-9")


          dispatchCheckAnswersSummary.summaryList() mustBe expectedResult
        }
      }

      "not all questions have been answered" - {

        "the Excise Number has been provided" in new Setup(emptyUserAnswers
          .set(DestinationBusinessNamePage, "name")
          .set(DestinationWarehouseExcisePage, "excise")
          .set(DestinationAddressPage, userAddressModelMax)
        ) {

          val expectedResult = SummaryList(Seq(
            Some(DestinationBusinessNameSummary.row()),
            DestinationWarehouseExciseSummary.row(),
            Some(DestinationAddressSummary.row())
          ).flatten).withCssClass("govuk-!-margin-bottom-9")


          dispatchCheckAnswersSummary.summaryList() mustBe expectedResult
        }

        "the VAT Number has been provided" in new Setup(emptyUserAnswers
          .set(DestinationWarehouseVatPage, "vat")
        ) {

          val expectedResult = SummaryList(Seq(
            Some(DestinationBusinessNameSummary.row()),
            DestinationWarehouseVatSummary.row(),
            Some(DestinationAddressSummary.row())
          ).flatten).withCssClass("govuk-!-margin-bottom-9")


          dispatchCheckAnswersSummary.summaryList() mustBe expectedResult
        }
      }

      "no questions have been answered" - {

        "the Excise Number has been provided" in new Setup(emptyUserAnswers) {

          val expectedResult = SummaryList(Seq(
            Some(DestinationBusinessNameSummary.row()),
            Some(DestinationAddressSummary.row())
          ).flatten).withCssClass("govuk-!-margin-bottom-9")


          dispatchCheckAnswersSummary.summaryList() mustBe expectedResult
        }
      }
    }
  }
}
