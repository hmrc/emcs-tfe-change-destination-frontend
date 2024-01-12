/*
 * Copyright 2024 HM Revenue & Customs
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

package viewmodels.checkAnswers.sections.info

import base.SpecBase
import fixtures.messages.sections.info.ChangeDestinationTypeMessages
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryList, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ChangeDestinationTypeHelperSpec extends SpecBase {

  lazy val helper = new ChangeDestinationTypeHelper()

  "ChangeDestinationTypeHelper" - {

    ".summaryList" - {

      "must return a summary list with a destination type row" - {
        Seq(ChangeDestinationTypeMessages.English).foreach { implicit messagesForLanguage =>

          MovementScenario.values.foreach { movementScenario =>

            s"for movement scenario: $movementScenario" in {
              implicit val request: DataRequest[_] = dataRequest(FakeRequest(), answers = emptyUserAnswers.set(DestinationTypePage, movementScenario))
              implicit val msgs: Messages = messages(request)

              helper.summaryList() mustBe SummaryList(
                rows = Seq(
                  SummaryListRowViewModel(
                    key = Key(Text(messagesForLanguage.summaryListKey)),
                    value = Value(movementScenario.stringValue.capitalize),
                    actions = Seq()
                  )
                ),
                classes = " govuk-!-margin-bottom-9"
              )
            }
          }
        }
      }
    }
  }
}
