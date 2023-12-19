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
import forms.sections.items.ItemPackagingSealTypeFormProvider

object ItemPackagingSealTypeMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>
    val heading: String = "What type of commercial seal is on this packaging?"
    val title: String = titleHelper(heading)

    def p(packagingDescription: String) = s"The packaging type is: $packagingDescription."

    val p2 = "Give more information (optional)"
    val hint = "Describe the seal so that it can be identified if the packaging has been tampered with. Include a reference number if there is one."

    val errorSealTypeRequired = "Enter the type of commercial seal on the packaging"
    val errorSealTypeInvalid = "Commercial seal type cannot contain < and > and : and ;"
    val errorSealTypeLength = s"Commercial seal type must be ${ItemPackagingSealTypeFormProvider.maxLengthSealTypeField} characters or less"

    val errorSealInformationInvalid = "Description cannot contain < and > and : and ;"
    val errorSealInformationLength = s"Commercial seal details must be ${ItemPackagingSealTypeFormProvider.maxLengthSealInformationField} characters or less"
    val errorSealInformationAlphanumeric = "Description must include letters and numbers"

    val cyaLabelSealType = "Commercial seal type"
    val cyaSealTypeHiddenChange = "commercial seal type"
    val cyaLabelSealInformation = "Commercial seal information (optional)"
    val cyaSealInformationHiddenChange = "information about commercial seal"
    val cyaSealInformationAddMoreInfo = "Enter more information about the commercial seal (optional)"

  }

  object English extends ViewMessages with BaseEnglish
}
