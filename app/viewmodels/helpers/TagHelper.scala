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

package viewmodels.helpers

import play.api.i18n.Messages
import play.twirl.api.Html
import viewmodels.taskList.UpdateNeeded

import javax.inject.{Inject, Singleton}

@Singleton
class TagHelper @Inject()(tag: views.html.components.tag) {

  def updateNeededTag(withNoFloat: Boolean = true)(implicit messages: Messages): Html = tag(
    message = messages(UpdateNeeded.msgKey),
    colour = "orange",
    extraClasses = if (withNoFloat) "float-none govuk-!-margin-left-1" else ""
  )

  def incompleteTag()(implicit messages: Messages): Html = tag(
    message = messages("taskListStatus.incomplete"),
    colour = "red"
  )

}