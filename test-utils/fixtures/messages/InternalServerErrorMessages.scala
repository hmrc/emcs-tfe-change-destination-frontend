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

package fixtures.messages

import fixtures.messages.BaseEnglish.titleHelper


object InternalServerErrorMessages {

  sealed trait ViewMessages {
    _: i18n =>
    val title: String = titleHelper("Sorry, we are experiencing technical difficulties - 500")
    val heading = "Sorry, we’re experiencing technical difficulties"
    val p1 = "Please try again in a few minutes."
    val p2 = "Use fallback procedures for the Excise Movement and Control System (EMCS) if you need to create a movement and the digital service is unavailable."
    val p3 = "Contact the EMCS helpdesk if you need to speak to someone about your account."
  }

  object English extends ViewMessages with BaseEnglish
}