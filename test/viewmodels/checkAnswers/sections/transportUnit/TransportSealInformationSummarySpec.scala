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

package viewmodels.checkAnswers.sections.transportUnit

import base.SpecBase
import fixtures.TransportUnitFixtures
import fixtures.messages.sections.transportUnit.TransportSealTypeMessages
import models.CheckMode
import models.requests.DataRequest
import org.scalatest.matchers.must.Matchers
import pages.sections.transportUnit.{TransportSealChoicePage, TransportSealTypePage}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Value
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.link

class TransportSealInformationSummarySpec extends SpecBase with Matchers with TransportUnitFixtures {

  implicit lazy val link: link = app.injector.instanceOf[link]

  "TransportSealInformationSummary" - {

    Seq(TransportSealTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output no row" in {

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

            TransportSealInformationSummary.row(testIndex1, hideChangeLinks = false) mustBe None
          }
        }

        "when there's an answer" - {

          "must output no rows if TransportSealChoicePage is false" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMax)
              .set(TransportSealChoicePage(testIndex1), false)
            )

            TransportSealInformationSummary.row(testIndex1, hideChangeLinks = false) mustBe None
          }

          "must output no rows if TransportSealChoicePage is not present" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMax)
            )

            TransportSealInformationSummary.row(testIndex1, hideChangeLinks = false) mustBe None
          }

          "must output the expected row for TransportSealInformation if TransportSealChoicePage is true" in {

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMax)
              .set(TransportSealChoicePage(testIndex1), true)
            )

            TransportSealInformationSummary.row(testIndex1, hideChangeLinks = false) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.moreInfoCYA,
                  value = Value(Text(transportSealTypeModelMax.moreInfo.value)),
                  actions = Seq(
                    ActionItemViewModel(
                      content = messagesForLanguage.change,
                      href = controllers.sections.transportUnit.routes.TransportSealTypeController.onPageLoad(testErn, testArc, testIndex1, CheckMode).url,
                      id = "changeTransportSealInformation1"
                    ).withVisuallyHiddenText(messagesForLanguage.moreInfoCyaChangeHidden)
                  )
                )
              )
          }

          "must output the expected row for TransportSealInformation if TransportSealChoicePage is true - " +
            "with no change link when on review page" in {

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMax)
              .set(TransportSealChoicePage(testIndex1), true)
            )

            TransportSealInformationSummary.row(testIndex1, hideChangeLinks = true) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.moreInfoCYA,
                  value = Value(Text(transportSealTypeModelMax.moreInfo.value)),
                  actions = Seq()
                )
              )
          }
        }

        "when there's an answer but no more information has been added" - {
          "must output no rows if TransportSealChoicePage is false" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMin)
              .set(TransportSealChoicePage(testIndex1), false)
            )

            TransportSealInformationSummary.row(testIndex1, hideChangeLinks = false) mustBe None
          }

          "must output no rows if TransportSealChoicePage is not present" in {
            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMin)
            )

            TransportSealInformationSummary.row(testIndex1, hideChangeLinks = false) mustBe None
          }

          "must output a row with a link to add more information and TransportSealChoicePage is true" in {

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMin)
              .set(TransportSealChoicePage(testIndex1), true)
            )

            TransportSealInformationSummary.row(testIndex1, hideChangeLinks = false) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.moreInfoCYA,
                  value = ValueViewModel(HtmlContent(link(
                    link = controllers.sections.transportUnit.routes.TransportSealTypeController.onPageLoad(testErn, testArc, testIndex1, CheckMode).url,
                    messageKey = messagesForLanguage.moreInfoCYAAddInfo
                  )))
                )
              )
          }

          "must output a row with no link to add more information and TransportSealChoicePage is true - when on review page" in {

            implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers
              .set(TransportSealTypePage(testIndex1), transportSealTypeModelMin)
              .set(TransportSealChoicePage(testIndex1), true)
            )

            TransportSealInformationSummary.row(testIndex1, hideChangeLinks = true) mustBe
              Some(
                SummaryListRowViewModel(
                  key = messagesForLanguage.moreInfoCYA,
                  value = ValueViewModel(Text(messagesForLanguage.notProvided))
                )
              )
          }
        }
      }
    }
  }
}
