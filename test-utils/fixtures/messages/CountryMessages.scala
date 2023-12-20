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

object CountryMessages {

  sealed trait ViewMessages { _: i18n =>
    val austria: String = "Austria"
    val belgium: String = "Belgium"
    val bulgaria: String = "Bulgaria"
    val czechia: String = "Czechia"
    val germany: String = "Germany"
    val greece: String = "Greece"
    val spain: String = "Spain"
    val italy: String = "Italy"
    val luxembourg: String = "Luxembourg"
    val malta: String = "Malta"
    val netherlands: String = "Netherlands"
    val poland: String = "Poland"
    val portugal: String = "Portugal"
    val romania: String = "Romania"
  }

  object English extends ViewMessages with BaseEnglish
}
