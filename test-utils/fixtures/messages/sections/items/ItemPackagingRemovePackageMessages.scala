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

object ItemPackagingRemovePackageMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading: String = "Are you sure you want to remove this packaging?"
    val title: String = titleHelper(heading)

    def p1(packageType: String): String = s"The packaging type is: $packageType."

    val errorRequired = "Select yes if you would like to remove this packaging"
  }

  object English extends ViewMessages with BaseEnglish

}
