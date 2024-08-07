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

package viewmodels.checkAnswers.sections.journeyType

import base.SpecBase
import fixtures.messages.sections.journeyType.HowMovementTransportedMessages
import models.CheckMode
import models.response.emcsTfe.{GuarantorType, TransportModeModel}
import models.sections.info.movementScenario.MovementScenario.DirectDelivery
import models.sections.journeyType.HowMovementTransported.{AirTransport, FixedTransportInstallations}
import org.scalatest.matchers.must.Matchers
import pages.sections.guarantor.GuarantorRequiredPage
import pages.sections.info.DestinationTypePage
import pages.sections.journeyType.HowMovementTransportedPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Value
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class HowMovementTransportedSummarySpec extends SpecBase with Matchers {

  ".row" - {

    Seq(HowMovementTransportedMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            HowMovementTransportedSummary.row(onReviewPage = false) mustBe None
          }
        }
        "when there's an answer" - {

          "when the onReviewPage is set to false" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(HowMovementTransportedPage, AirTransport))

              HowMovementTransportedSummary.row(onReviewPage = false) mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(messagesForLanguage.radioOption1)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.journeyType.routes.HowMovementTransportedController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = HowMovementTransportedPage
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
              )
            }
          }

          "when the onReviewPage is set to true" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(HowMovementTransportedPage, AirTransport))

              HowMovementTransportedSummary.row(onReviewPage = true) mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(messagesForLanguage.radioOption1)),
                  actions = Seq()
                )
              )
            }
          }

          "when the movement is NiToEu, GuarantorRequired is false, and the journey type is FixedTransportInstallations" - {

            "must output the expected row (without the change link)" in {

              implicit lazy val request = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers
                  .set(DestinationTypePage, DirectDelivery)
                  .set(GuarantorRequiredPage, false),
                ern = testNorthernIrelandWarehouseKeeperErn,
                movementDetails = maxGetMovementResponse.copy(
                  transportMode = TransportModeModel(FixedTransportInstallations.toString, None)
                )
              )

              HowMovementTransportedSummary.row(onReviewPage = false) mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(messagesForLanguage.radioOption2)),
                  actions = Seq()
                )
              )
            }
          }

          "when the movement is NiToEu, NoGuarantor from ie801, and the journey type is FixedTransportInstallations" - {

            "must output the expected row (without the change link)" in {

              implicit lazy val request = dataRequest(
                request = FakeRequest(),
                answers = emptyUserAnswers.set(DestinationTypePage, DirectDelivery),
                ern = testNorthernIrelandWarehouseKeeperErn,
                movementDetails = maxGetMovementResponse.copy(
                  movementGuarantee = maxGetMovementResponse.movementGuarantee.copy(guarantorTypeCode = GuarantorType.NoGuarantor),
                  transportMode = TransportModeModel(FixedTransportInstallations.toString, None)
                )
              )

              HowMovementTransportedSummary.row(onReviewPage = false) mustBe Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(HtmlContent(messagesForLanguage.radioOption2)),
                  actions = Seq()
                )
              )
            }
          }
        }
      }
    }
  }
}
