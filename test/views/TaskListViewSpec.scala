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

package views

import base.SpecBase
import fixtures.messages.TaskListMessages
import models.MovementValidationFailure
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.UkTaxWarehouse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.TaskListView

class TaskListViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  lazy val view: TaskListView = app.injector.instanceOf[TaskListView]

  "TaskListView" - {

    Seq(TaskListMessages.English).foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "for a validation failure scenario" - {

          val validationFailures: Seq[MovementValidationFailure] = Seq(
            //scalastyle:off magic.number
            MovementValidationFailure(Some(12), Some("This is an error.")),
            MovementValidationFailure(Some(13), Some("This is an error.")),
            MovementValidationFailure(Some(14), Some("This is an error."))
            //scalastyle:on magic.number
          )

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            ern = testGreatBritainErn,
            answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB).copy(validationErrors = validationFailures)
          )

          implicit val doc: Document = Jsoup.parse(view().toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleImportFor("Tax warehouse in Great Britain"),
            Selectors.notificationBannerTitle -> messagesForLanguage.important,
            Selectors.notificationBannerContent -> messagesForLanguage.notificationBannerValidationFailuresContent,
            // errorType: 12
            Selectors.notificationBannerError(1) -> "This is an error.",
            // errorType: 13
            Selectors.notificationBannerError(2) -> "This is an error.",
            // errorType: 14
            Selectors.notificationBannerError(3) -> "errors.validation.notificationBanner.14.content",
          ))
        }

        "for a draft change of destination (not in error)" - {

          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(),
            ern = testGreatBritainErn,
            answers = emptyUserAnswers.set(DestinationTypePage, UkTaxWarehouse.GB)
          )

          implicit val doc: Document = Jsoup.parse(view().toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.titleImportFor("Tax warehouse in Great Britain"),
            Selectors.h2(1) -> messagesForLanguage.changeOfDestinationSection(testArc),
            Selectors.h1 -> messagesForLanguage.headingImportFor("Tax warehouse in Great Britain")
          ))
        }
      }
    }

  }
}
