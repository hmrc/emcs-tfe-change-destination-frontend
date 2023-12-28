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
import fixtures.messages.sections.destination.DestinationBusinessNameMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.consignee.ConsigneeBusinessNamePage
import pages.sections.destination.{DestinationBusinessNamePage, DestinationConsigneeDetailsPage}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class DestinationBusinessNameSummarySpec extends SpecBase with Matchers {

  "DestinationBusinessNameSummary" - {

    Seq(DestinationBusinessNameMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output row with 'Not provided' and change link" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            DestinationBusinessNameSummary.row() mustBe SummaryListRowViewModel(
              key = messagesForLanguage.cyaLabel,
              value = Value(messagesForLanguage.cyaDestinationNotProvided),
              actions = Seq(
                ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.destination.routes.DestinationBusinessNameController.onPageLoad(testErn, testArc, CheckMode).url,
                  id = "changeDestinationBusinessName"
                ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
              )
            )

          }
        }

        "when the DestinationConsigneeDetailsPage has been answered no" - {

          "when there is no Destination BusinessName given" - {

            s"must output ${messagesForLanguage.cyaDestinationNotProvided}" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationConsigneeDetailsPage, false)
              )

              DestinationBusinessNameSummary.row() mustBe SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(messagesForLanguage.cyaDestinationNotProvided),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.destination.routes.DestinationBusinessNameController.onPageLoad(testErn, testArc, CheckMode).url,
                    id = "changeDestinationBusinessName"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
            }
          }

          "when Destination BusinessName has been answered" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationConsigneeDetailsPage, false)
                .set(DestinationBusinessNamePage, "destination name")
              )

              DestinationBusinessNameSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value("destination name"),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.destination.routes.DestinationBusinessNameController.onPageLoad(testErn, testArc, CheckMode).url,
                      id = "changeDestinationBusinessName"
                    ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                  )
                )
            }
          }
        }

        "when the DestinationConsigneeDetailsPage has been answered yes" - {

          "when there is no Consignee BusinessName given" - {

            s"must output ${messagesForLanguage.cyaConsigneeNotProvided}" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationConsigneeDetailsPage, true)
              )

              DestinationBusinessNameSummary.row() mustBe SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = Value(messagesForLanguage.cyaConsigneeNotProvided),
                actions = Seq.empty
              )
            }
          }

          "when Consignee Business Name has been answered" - {

            "must output the expected row" in {

              implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers
                .set(DestinationConsigneeDetailsPage, true)
                .set(ConsigneeBusinessNamePage, "consignee name")
              )

              DestinationBusinessNameSummary.row() mustBe
                SummaryListRowViewModel(
                  key = messagesForLanguage.cyaLabel,
                  value = Value("consignee name"),
                  actions = Seq.empty
                )
            }
          }
        }
      }
    }
  }
}
