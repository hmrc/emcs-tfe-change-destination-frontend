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

class ConfirmationViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "ConfirmationView" - {

    Seq(ConfirmationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when movement was Satisfactory" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

          val view = app.injector.instanceOf[ConfirmationView]

          implicit val doc: Document = Jsoup.parse(view(
            testConfirmationReference,
            testSubmissionDate.toLocalDate,
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
            Selectors.p(2) -> messagesForLanguage.p1,
            Selectors.p(3) -> messagesForLanguage.p2,
            Selectors.p(4) -> messagesForLanguage.p3,
            Selectors.p(5) -> messagesForLanguage.p4,
            Selectors.p(6) -> messagesForLanguage.returnToAccountLink,
            Selectors.p(7) -> messagesForLanguage.feedbackLink,
          ))

          "have correct summary list" in {
            val summaryList = doc.getElementsByClass("govuk-summary-list").first
            summaryList.getElementsByClass("govuk-summary-list__key").get(0).text mustBe messagesForLanguage.localReferenceNumber
            summaryList.getElementsByClass("govuk-summary-list__value").get(0).text mustBe testConfirmationReference
            summaryList.getElementsByClass("govuk-summary-list__key").get(1).text mustBe messagesForLanguage.dateOfSubmission
            summaryList.getElementsByClass("govuk-summary-list__value").get(1).text mustBe testSubmissionDate.toLocalDate.format(DateTimeFormatter.ofPattern("dd LLLL yyyy"))
          }
        }
      }
    }
  }
}
