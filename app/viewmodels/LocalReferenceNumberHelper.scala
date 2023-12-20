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

package viewmodels

import play.api.data.Form
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import views.ViewUtils

import javax.inject.Inject

class LocalReferenceNumberHelper @Inject()(p: views.html.components.p) {

  private def messageFor(key: String)(isDeferred: Boolean)(implicit messages: Messages) =
    if (isDeferred) messages(s"localReferenceNumber.deferred.$key") else messages(s"localReferenceNumber.new.$key")

  def title(form: Form[_], isDeferred: Boolean)(implicit messages: Messages): String =
    ViewUtils.title(form, messageFor("title")(isDeferred))

  def heading(isDeferred: Boolean)(implicit messages: Messages): String =
    messageFor("heading")(isDeferred)

  def content(isDeferred: Boolean)(implicit messages: Messages): Html =
    HtmlFormat.fill(
      if (isDeferred) {
        Seq(
          p() {
            Html(messageFor("p1")(isDeferred))
          }
        )
      } else {
        Seq(
          p() {
            Html(messageFor("p1")(isDeferred))
          },
          p() {
            Html(messageFor("p2")(isDeferred))
          }
        )
      })

}
