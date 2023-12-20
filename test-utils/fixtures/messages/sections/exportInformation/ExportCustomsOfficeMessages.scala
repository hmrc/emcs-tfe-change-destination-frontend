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

package fixtures.messages.sections.exportInformation

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ExportCustomsOfficeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading: String = "Enter the customs office code for the place where the export declaration is lodged"
    val title: String = titleHelper(heading)

    def hint(euExport: Boolean): String =
      if(euExport) {
        "This code is also known as a customs office reference number. It starts with 2 letters representing the member state. For example, GBSOU001. Search for EU customs office codes here (opens in new tab)."
      } else {
        "This code is also known as a customs office reference number. It starts with 2 letters representing the member state. For example, GBSOU001. Search for UK customs office codes here (opens in new tab)."
      }

    val errorRequired = "Enter the customs office code"
    val errorLength = (int: Int) => s"Customs office code must be $int characters"
    val errorInvalidCharacter = "Customs office code must not contain < and > and : and ;"
    val errorCustomOfficeRegex = "Customs office code must start with 2 capital letters followed by 6 mixed numbers and letters."

    val cyaLabel = "Customs office code"
    val cyaChangeHidden = "customs office code"
  }

  object English extends ViewMessages with BaseEnglish
}
