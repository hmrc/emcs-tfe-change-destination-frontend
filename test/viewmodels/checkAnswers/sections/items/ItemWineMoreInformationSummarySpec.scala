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

package viewmodels.checkAnswers.sections.items

import base.SpecBase
import fixtures.messages.sections.items.ItemWineMoreInformationMessages
import models.CheckMode
import org.scalatest.matchers.must.Matchers
import pages.sections.items.ItemWineMoreInformationPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemWineMoreInformationSummarySpec extends SpecBase with Matchers {

  lazy val link = app.injector.instanceOf[views.html.components.link]
  lazy val summary = new ItemWineMoreInformationSummary(link)

  "ItemWineMoreInformationSummary" - {

    Seq(ItemWineMoreInformationMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "when there's no answer" - {

          "must output the expected data" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers)

            summary.row(testIndex1) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = ValueViewModel(HtmlContent(link(
                  link = controllers.sections.items.routes.ItemWineMoreInformationController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  messageKey = messagesForLanguage.cyaAddMoreInformation
                )))
              )
          }
        }

        "when there's an answer" - {

          "must output the expected row" in {

            implicit lazy val request = dataRequest(FakeRequest(), emptyUserAnswers.set(ItemWineMoreInformationPage(testIndex1), Some("Information")))

            summary.row(testIndex1) mustBe
              SummaryListRowViewModel(
                key = messagesForLanguage.cyaLabel,
                value = ValueViewModel(Text("Information")),
                actions = Seq(
                  ActionItemViewModel(
                    content = messagesForLanguage.change,
                    href = controllers.sections.items.routes.ItemWineMoreInformationController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                    id = "changeItemWineMoreInformation1"
                  ).withVisuallyHiddenText(messagesForLanguage.cyaChangeHidden)
                )
              )
          }
        }
      }
    }
  }
}
