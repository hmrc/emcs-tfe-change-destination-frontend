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

package forms.sections.consignee

import forms.ALPHANUMERIC_REGEX
import forms.mappings.Mappings
import play.api.data.Form

import javax.inject.Inject

class ConsigneeExciseFormProvider @Inject() extends Mappings {


  def apply(isNorthernIrishTemporaryRegisteredConsignee: Boolean): Form[String] = {
    val maxLengthValue = if (isNorthernIrishTemporaryRegisteredConsignee) 16 else 13

    val noInputErrorKey = if (isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryConsignee.error.noInput"
    } else {
      "consigneeExcise.error.noInput"
    }

    val tooLongErrorKey = if (isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryConsignee.error.tooLong"
    }
    else {
      "consigneeExcise.error.tooLong"
    }

    val invalidCharactersErrorKey = if (isNorthernIrishTemporaryRegisteredConsignee) {
      "consigneeExcise.temporaryConsignee.error.invalidCharacters"
    }
    else {
      "consigneeExcise.error.invalidCharacters"
    }

    Form(
      "value" -> text(noInputErrorKey)
        .verifying(maxLength(maxLengthValue, tooLongErrorKey))
        .verifying(regexpUnlessEmpty(ALPHANUMERIC_REGEX, invalidCharactersErrorKey))
    )
  }


}
