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

package fixtures.messages.sections.items

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ItemWineMoreInformationMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    val heading: String = s"Give more information about the wine (optional)"
    val title: String = titleHelper(heading)

    val cyaLabel: String = "Wine more information"
    val cyaChangeHidden: String = "wine more information"
    val cyaAddMoreInformation: String = "Enter more information about wine product (optional)"

    val errorLength = s"Information must be 350 characters or less"
    val errorAlphanumeric = "Information must include letters and numbers"
    val errorInvalidCharacter = "Information must not contain < and > and : and ;"

  }

  object English extends ViewMessages with BaseEnglish

}
