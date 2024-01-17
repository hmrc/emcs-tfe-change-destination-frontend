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

package fixtures.messages

object ContinueDraftMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading = "Do you want to continue with this draft change of destination?"
    val title: String = titleHelper(heading)

    val p1 = "A change of destination is already in progress for this movement. You can continue with this draft, or start a new one."
    val insetText = "If you choose to start a new change of destination this draft will be deleted."

    override val yes = "Continue with this draft"
    override val no = "Start a new change of destination"

    val errorRequired: String = "Select if you want to continue with this draft"
  }

  object English extends ViewMessages with BaseEnglish
}
