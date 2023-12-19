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

package views.components

import base.SpecBase
import fixtures.messages.TaskListStatusMessages
import org.jsoup.Jsoup
import play.api.i18n.Messages
import viewmodels.taskList._

class TaskListSpec extends SpecBase {

  "TaskList" - {

    Seq(TaskListStatusMessages.English).foreach { messagesForLanguage =>

      s"when rendering with language code '${messagesForLanguage.lang.code}'" - {

        "Should render the task list component as expected" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          val taskList = app.injector.instanceOf[views.html.components.taskList]

          val html = taskList(TaskList(Seq(
            TaskListSection("Test 1", Seq(
              TaskListSectionRow("Row 1", "ern-1", Some("link"), None, Some(Completed)),
              TaskListSectionRow("Row 2", "ern-2", Some("link"), None, Some(InProgress)),
              TaskListSectionRow("Row 3", "ern-2", Some("link"), None, Some(NotStarted)),
              TaskListSectionRow("Row 4", "ern-4", None, None, Some(CannotStartYet)),
              TaskListSectionRow("Row 4", "ern-4", None, None, None),
            )),
            TaskListSection("Test 2", Seq(
              TaskListSectionRow("Row 1", "ern-1", None, None, Some(CannotStartYet))
            ))
          )))

          val doc = Jsoup.parse(html.toString())

          val sectionSelector: Int => String = i => s"ol.app-task-list > li:nth-of-type($i)"
          val sectionRow: Int => String = i => s"ul.app-task-list__items > li:nth-of-type($i)"

          "for the first section" - {

            val section = doc.select(sectionSelector(1))

            "should have the correct heading" in {
              section.select("h2").text() mustBe "Test 1"
            }

            "for the 1st row" - {

              val row = section.select(sectionRow(1))

              "should have the correct text" in {
                row.select("span > a").text() mustBe "Row 1"
              }

              "should have the correct link" in {
                row.select("span > a").attr("href") mustBe "link"
              }

              "should have the correct status text AND colour" in {
                val tag = row.select("strong.app-task-list__tag")
                tag.text() mustBe messagesForLanguage.completed
                tag.hasClass("govuk-tag") mustBe true
              }
            }

            "for the 2nd row" - {

              val row = section.select(sectionRow(2))

              "should have the correct text" in {
                row.select("span > a").text() mustBe "Row 2"
              }

              "should have the correct link" in {
                row.select("span > a").attr("href") mustBe "link"
              }

              "should have the correct status text AND colour" in {
                val tag = row.select("strong.app-task-list__tag")
                tag.text() mustBe messagesForLanguage.inProgress
                tag.hasClass("govuk-tag--blue") mustBe true
              }
            }

            "for the 3rd row" - {

              val row = section.select(sectionRow(3))

              "should have the correct text" in {
                row.select("span > a").text() mustBe "Row 3"
              }

              "should have the correct link" in {
                row.select("span > a").attr("href") mustBe "link"
              }

              "should have the correct status text AND colour" in {
                val tag = row.select("strong.app-task-list__tag")
                tag.text() mustBe messagesForLanguage.notStartedYet
                tag.hasClass("govuk-tag--grey") mustBe true
              }
            }

            "for the 4th row" - {

              val row = section.select(sectionRow(4))

              "should have the correct text" in {
                row.select("span").text() mustBe "Row 4"
              }

              "should have the correct status text AND colour" in {
                val tag = row.select("strong.app-task-list__tag")
                tag.text() mustBe messagesForLanguage.cannotStartYet
                tag.hasClass("govuk-tag--grey") mustBe true
              }
            }
          }

          "for the second section" - {

            val section = doc.select(sectionSelector(2))

            "should have the correct heading" in {
              section.select("h2").text() mustBe "Test 2"
            }

            "for the 1st row" - {

              val row = section.select(sectionRow(1))

              "should have the correct text" in {
                row.select("span").text() mustBe "Row 1"
              }

              "should have the correct status text AND colour" in {
                val tag = row.select("strong.app-task-list__tag")
                tag.text() mustBe messagesForLanguage.cannotStartYet
                tag.hasClass("govuk-tag--grey") mustBe true
              }
            }
          }
        }
      }
    }
  }
}
