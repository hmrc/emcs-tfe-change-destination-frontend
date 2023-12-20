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
import models.requests.DataRequest
import models.{CheckMode, UserAnswers}
import org.scalatest.matchers.must.Matchers
import pages.sections.consignor.ConsignorAddressPage
import pages.sections.dispatch.{DispatchAddressPage, DispatchUseConsignorDetailsPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DispatchAddressSummarySpec extends SpecBase with Matchers with UserAddressFixtures {

  class Test(userAnswers: UserAnswers) {
    implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)
  }

  "DispatchBusinessAddressSummary" - {

    Seq(DispatchCheckAnswersMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "must output no row" - {
          "when there's no answer for DispatchUseConsignorDetailsPage" in new Test(emptyUserAnswers) {
            DispatchAddressSummary.row() mustBe None
          }
          "when DispatchUseConsignorDetailsPage is false and there's no answer for DispatchAddressPage" in new Test(
            emptyUserAnswers.set(DispatchUseConsignorDetailsPage, false)
          ) {
            DispatchAddressSummary.row() mustBe None
          }
        }

        s"must output the expected row for DispatchAddress" - {

          "when DispatchUseConsignorDetailsPage is false and there's an answer for DispatchAddressPage" in new Test(
            emptyUserAnswers
              .set(DispatchUseConsignorDetailsPage, false)
              .set(DispatchAddressPage, userAddressModelMax)
          ) {
            DispatchAddressSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.addressLabel,
                  value = ValueViewModel(userAddressModelMax.toCheckYourAnswersFormat),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.dispatch.routes.DispatchAddressController.onPageLoad(testErn, testDraftId, CheckMode).url,
                      id = "changeDispatchAddress"
                    ).withVisuallyHiddenText(messagesForLanguage.addressChangeHidden)
                  )
                )
              )
          }

          "when DispatchUseConsignorDetailsPage is true and there's an answer for ConsignorAddressPage" in new Test(
            emptyUserAnswers
              .set(DispatchUseConsignorDetailsPage, true)
              .set(ConsignorAddressPage, userAddressModelMax)
          ) {
            DispatchAddressSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.addressLabel,
                  value = ValueViewModel(userAddressModelMax.toCheckYourAnswersFormat),
                  actions = Seq()
                )
              )
          }

          "when DispatchUseConsignorDetailsPage is true and there's no answer for ConsignorAddressPage" in new Test(
            emptyUserAnswers
              .set(DispatchUseConsignorDetailsPage, true)
          ) {
            DispatchAddressSummary.row() mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.addressLabel,
                  value = ValueViewModel(messagesForLanguage.consignorSectionNotComplete),
                  actions = Seq()
                )
              )
          }
        }
      }
    }
  }
}
