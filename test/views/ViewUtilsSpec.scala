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

import base.ViewSpecBase
import play.api.data.Form
import play.api.i18n.Messages
import play.api.data.Forms._

class ViewUtilsSpec extends ViewSpecBase with ViewBehaviours {

  ".title" in {
    case class UserData(name: String, age: Int)

    val userForm = Form(
      mapping(
        "name" -> text,
        "age" -> number
      )(UserData.apply)(UserData.unapply)
    )

    implicit val msgs: Messages = messages(app)

    ViewUtils.title(userForm, "TITLE") mustBe " TITLE - Excise Movement and Control System - GOV.UK"
  }

  ".titleNoForm" in {

    implicit val msgs: Messages = messages(app)
    ViewUtils.titleNoForm("TITLE") mustBe "TITLE - Excise Movement and Control System - GOV.UK"
  }

}
