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

object JourneyRecoveryMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: String
    val title: String
    val p1: String
    val p2: String
    val numbered1: String
    val numbered2: String
    val numbered3: String
    val p3: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title: String = titleHelper("There is a problem")
    override val heading = "There is a problem"
    override val p1 = "You have not added any information to cancel a movement yet."
    override val p2 = "To submit a cancellation of a movement you must:"
    override val numbered1 = "Go to the ‘View all movements’ page"
    override val numbered2 = "Select to view the details of the movement you want to delay"
    override val numbered3 = "Select to ‘Submit a cancellation of a movement’"
    override val p3 = "You can also choose to view all your account information, or sign out."
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title: String = titleHelper("There is a problem")
    override val heading = "There is a problem"
    override val p1 = "You have not added any information to cancel a movement yet."
    override val p2 = "To submit a cancellation of a movement you must:"
    override val numbered1 = "Go to the ‘View all movements’ page"
    override val numbered2 = "Select to view the details of the movement you want to delay"
    override val numbered3 = "Select to ‘Submit a cancellation of a movement’"
    override val p3 = "You can also choose to view all your account information, or sign out."
  }
}
