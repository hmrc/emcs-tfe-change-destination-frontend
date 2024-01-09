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
import fixtures.messages.sections.destination.DestinationAddressMessages
import models.{CheckMode, UserAddress}
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeAddressPage
import pages.sections.destination.{DestinationAddressPage, DestinationConsigneeDetailsPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DestinationAddressSummarySpec extends SpecBase with Matchers with UserAddressFixtures {

  "DestinationAddressSummary" - {

    Seq(DestinationAddressMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer in the user answers (defaulting to 801)" - {

          "must output the 801 data" in {

            val userAddressFrom801 = UserAddress(Some("DeliveryPlaceTraderStreetNumber"), "DeliveryPlaceTraderStreetName", "DeliveryPlaceTraderCity", "DeliveryPlaceTraderPostcode")

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            DestinationAddressSummary.row() mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(userAddressFrom801.toCheckYourAnswersFormat),
              actions = Seq(
                ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.destination.routes.DestinationAddressController.onPageLoad(testErn, testArc, CheckMode).url,
                  id = "changeDestinationAddress"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
              )
            )

          }
        }

        "when the DestinationConsigneeDetailsPage has been answered no" - {

          "when there is no Destination Address given (in either 801 or user answers)" - {

            s"must output ${messagesForLanguage.cyaDestinationNotProvided}" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationConsigneeDetailsPage, false),
                movementDetails = maxGetMovementResponse.copy(deliveryPlaceTrader = None)
              )

              DestinationAddressSummary.row() mustBe SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(HtmlContent(messagesForLanguage.cyaDestinationNotProvided)),
                actions = Seq.empty
              )
            }
          }

          "when Destination Address has been answered" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationAddressPage, userAddressModelMax)
              )

              DestinationAddressSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(userAddressModelMax.toCheckYourAnswersFormat),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.destination.routes.DestinationAddressController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeDestinationAddress"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
            }
          }
        }

        "when the DestinationConsigneeDetailsPage has been answered yes" - {

          "when there is no Consignee Address given" - {

            s"must output ${messagesForLanguage.cyaConsigneeNotProvided}" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationConsigneeDetailsPage, true),
                movementDetails = maxGetMovementResponse.copy(consigneeTrader = None)
              )

              DestinationAddressSummary.row() mustBe SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(HtmlContent(messagesForLanguage.cyaConsigneeNotProvided)),
                actions = Seq.empty
              )
            }
          }

          "when Consignee Address has been answered" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationConsigneeDetailsPage, true)
                .set(ConsigneeAddressPage, userAddressModelMax)
              )

              DestinationAddressSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value(userAddressModelMax.toCheckYourAnswersFormat),
                  actions = Seq.empty
                )
            }
          }
        }
      }
    }
  }
}
