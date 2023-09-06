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

import fixtures.i18n

object InternalServerErrorMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val p1: String
    val p2: String
    val p3: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title: String = titleHelper("Sorry, there is a problem with the service")
    override val heading = "Sorry, there is a problem with the service"
    override val p1 = "Try again later."
    override val p2 = "Use fallback procedures for the Excise Movement and Control System (EMCS) if you need to create a movement and the digital service is unavailable."
    override val p3 = "Contact the EMCS helpdesk if you need to speak to someone about your account."
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title: String = titleHelper("Sorry, there is a problem with the service")
    override val heading = "Sorry, there is a problem with the service"
    override val p1 = "Try again later."
    override val p2 = "Use fallback procedures for the Excise Movement and Control System (EMCS) if you need to create a movement and the digital service is unavailable."
    override val p3 = "Contact the EMCS helpdesk if you need to speak to someone about your account."
  }
}