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

package views

import base.SpecBase
import fixtures.messages.ConfirmationMessages
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ConfirmationView

import java.time.format.DateTimeFormatter

//noinspection ScalaStyle
class ConfirmationViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val insetTextParagraph = "#main-content .govuk-inset-text p"
    val insetTextListItem: Int => String = index => s"#main-content .govuk-inset-text ul li:nth-of-type($index)"
  }

  implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

  lazy val view: ConfirmationView = app.injector.instanceOf[ConfirmationView]

  "ConfirmationView" - {

    Seq(ConfirmationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when the consignee hasn't changed" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          implicit val doc: Document = Jsoup.parse(view(
            testConfirmationReference,
            testSubmissionDate.toLocalDate,
            hasConsigneeChanged = false,
            "testUrl1",
            "testUrl2",
            "testUrl3"
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.movementInformationHeader,
            Selectors.p(1) -> messagesForLanguage.printText,
            Selectors.h2(2) -> messagesForLanguage.whatHappensNextHeader,
            Selectors.p(2) -> messagesForLanguage.p1ConsigneeSame,
            Selectors.insetTextParagraph -> messagesForLanguage.insetP,
            Selectors.insetTextListItem(1) -> messagesForLanguage.insetBullet1,
            Selectors.insetTextListItem(2) -> messagesForLanguage.insetBullet2,
            Selectors.p(3) -> messagesForLanguage.p2,
            Selectors.p(4) -> messagesForLanguage.p3,
            Selectors.h2(3) -> messagesForLanguage.explainDelayHeader,
            Selectors.p(5) -> messagesForLanguage.explainDelayP,
            Selectors.p(6) -> messagesForLanguage.p4,
            Selectors.p(7) -> messagesForLanguage.returnToAccountLink,
            Selectors.p(8) -> messagesForLanguage.feedbackLink,
          ))

          "have correct summary list" in {
            val summaryList = doc.getElementsByClass("govuk-summary-list").first
            summaryList.getElementsByClass("govuk-summary-list__key").get(0).text mustBe messagesForLanguage.arc
            summaryList.getElementsByClass("govuk-summary-list__value").get(0).text mustBe testConfirmationReference
            summaryList.getElementsByClass("govuk-summary-list__key").get(1).text mustBe messagesForLanguage.dateOfSubmission
            summaryList.getElementsByClass("govuk-summary-list__value").get(1).text mustBe testSubmissionDate.toLocalDate.format(DateTimeFormatter.ofPattern("dd LLLL yyyy"))
          }
        }

        "when the consignee has changed" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          implicit val doc: Document = Jsoup.parse(view(
            testConfirmationReference,
            testSubmissionDate.toLocalDate,
            hasConsigneeChanged = true,
            "testUrl1",
            "testUrl2",
            "testUrl3"
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.movementInformationHeader,
            Selectors.p(1) -> messagesForLanguage.printText,
            Selectors.h2(2) -> messagesForLanguage.whatHappensNextHeader,
            Selectors.p(2) -> messagesForLanguage.p1ConsigneeChanged,
            Selectors.insetTextParagraph -> messagesForLanguage.insetP,
            Selectors.insetTextListItem(1) -> messagesForLanguage.insetBullet1,
            Selectors.insetTextListItem(2) -> messagesForLanguage.insetBullet2,
            Selectors.p(3) -> messagesForLanguage.p2,
            Selectors.p(4) -> messagesForLanguage.p3,
            Selectors.h2(3) -> messagesForLanguage.explainDelayHeader,
            Selectors.p(5) -> messagesForLanguage.explainDelayP,
            Selectors.h2(4) -> messagesForLanguage.submissionUnsuccessfulHeader,
            Selectors.p(6) -> messagesForLanguage.submissionUnsuccessfulP1,
            Selectors.p(7) -> messagesForLanguage.submissionUnsuccessfulP2,
            Selectors.p(8) -> messagesForLanguage.submissionUnsuccessfulP3,
            Selectors.p(9) -> messagesForLanguage.p4,
            Selectors.p(10) -> messagesForLanguage.returnToAccountLink,
            Selectors.p(11) -> messagesForLanguage.feedbackLink,
          ))

          "have correct summary list" in {
            val summaryList = doc.getElementsByClass("govuk-summary-list").first
            summaryList.getElementsByClass("govuk-summary-list__key").get(0).text mustBe messagesForLanguage.arc
            summaryList.getElementsByClass("govuk-summary-list__value").get(0).text mustBe testConfirmationReference
            summaryList.getElementsByClass("govuk-summary-list__key").get(1).text mustBe messagesForLanguage.dateOfSubmission
            summaryList.getElementsByClass("govuk-summary-list__value").get(1).text mustBe testSubmissionDate.toLocalDate.format(DateTimeFormatter.ofPattern("dd LLLL yyyy"))
          }
        }
      }
    }
  }
}
