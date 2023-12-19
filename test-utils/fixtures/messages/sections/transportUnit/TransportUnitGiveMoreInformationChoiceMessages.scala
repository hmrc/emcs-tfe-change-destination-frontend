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

package fixtures.messages.sections.transportUnit

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object TransportUnitGiveMoreInformationChoiceMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def title(transportUnitType: String): String = titleHelper(s"Do you know any more information about this $transportUnitType?")
    def heading(transportUnitType: String): String = s"Do you know any more information about this $transportUnitType?"
    val hintText = "For example, the identity of the subsequent transporter or information about subsequent transport units."
    def errorRequired(transportUnitType: String): String = s"Select yes if you know more information about this $transportUnitType"
  }

  object English extends ViewMessages with BaseEnglish


}
