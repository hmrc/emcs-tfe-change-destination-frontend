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

package viewmodels.helpers

import models.sections.transportUnit.TransportUnitType
import pages.Page
import play.api.data.Form
import play.api.i18n.Messages
import views.ViewUtils

import javax.inject.Inject

class TransportUnitTypeHelper @Inject()() {

  private def messageFor(page: String, key: String, transportUnitType: TransportUnitType)(implicit messages: Messages) =
    messages(s"$page.$key", messages(s"$page.transportUnitType.$transportUnitType"))

  def title(form: Form[_], transportUnitType: TransportUnitType, page: Page)(implicit messages: Messages): String =
    ViewUtils.title(form, messageFor(page.toString, "title", transportUnitType))

  def heading(transportUnitType: TransportUnitType, page: Page)(implicit messages: Messages): String =
    messageFor(page.toString, "heading", transportUnitType)

}
