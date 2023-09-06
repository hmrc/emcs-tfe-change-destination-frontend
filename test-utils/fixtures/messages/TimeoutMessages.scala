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
object TimeoutMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val p1: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val title = titleHelper("For your security, we signed you out")
    override val heading = "For your security, we signed you out"
    override val p1 = "We have not saved your answers."
  }

  object Welsh extends ViewMessages with BaseWelsh {
    override val title = titleHelper("For your security, we signed you out")
    override val heading = "For your security, we signed you out"
    override val p1 = "We have not saved your answers."
  }
}