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

package viewmodels.checkAnswers.sections.dispatch

import base.SpecBase
import fixtures.UserAddressFixtures
import fixtures.messages.sections.dispatch.DispatchCheckAnswersMessages
import models.UserAnswers
import models.requests.DataRequest
import pages.sections.dispatch.{DispatchAddressPage, DispatchBusinessNamePage, DispatchUseConsignorDetailsPage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.all.FluentSummaryList
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DispatchCheckAnswersHelperSpec extends SpecBase with UserAddressFixtures {

  class Setup(userAnswers: UserAnswers) {
    implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers)
    implicit val msgs: Messages = messages(FakeRequest())
    lazy val dispatchCheckAnswersSummary = new DispatchCheckAnswersHelper()
  }

  "CheckAnswersDispatchHelper" - {

    ".buildSummaryRows should return the correct rows" - {

      "when DispatchUseConsignorDetailsPage is false" in new Setup(emptyUserAnswers
        .set(DispatchBusinessNamePage, "Some Business Name")
        .set(DispatchUseConsignorDetailsPage, false)
        .set(DispatchAddressPage, userAddressModelMax)
      ) {

        val expectedResult: SummaryList = SummaryList(Seq(
          DispatchUseConsignorDetailsSummary.row()(fakeDataRequest, msgs),
          DispatchBusinessNameSummary.row()(fakeDataRequest, msgs),
          DispatchWarehouseExciseSummary.row()(fakeDataRequest, msgs),
          DispatchAddressSummary.row()(fakeDataRequest, msgs)
        ).flatten).withCssClass("govuk-!-margin-bottom-9")


        dispatchCheckAnswersSummary.summaryList() mustBe expectedResult
      }

      "when DispatchUseConsignorDetailsPage is true" in new Setup(emptyUserAnswers
        .set(DispatchBusinessNamePage, "Some Business Name")
        .set(DispatchUseConsignorDetailsPage, true)
        .set(DispatchAddressPage, userAddressModelMax)
      ) {

        val expectedResult: SummaryList = SummaryList(Seq(
          DispatchUseConsignorDetailsSummary.row()(fakeDataRequest, msgs),
          Some(
            SummaryListRowViewModel(
              key = DispatchCheckAnswersMessages.English.traderNameLabel,
              value = ValueViewModel(HtmlFormat.escape(testMinTraderKnownFacts.traderName).toString),
              actions = Seq()
            )
          ),
          DispatchWarehouseExciseSummary.row()(fakeDataRequest, msgs),
          Some(SummaryListRowViewModel(
            key = DispatchCheckAnswersMessages.English.addressLabel,
            value = ValueViewModel(Text(DispatchCheckAnswersMessages.English.consignorSectionNotComplete)),
            actions = Seq()
          ))
        ).flatten).withCssClass("govuk-!-margin-bottom-9")


        dispatchCheckAnswersSummary.summaryList() mustBe expectedResult
      }
    }
  }
}
