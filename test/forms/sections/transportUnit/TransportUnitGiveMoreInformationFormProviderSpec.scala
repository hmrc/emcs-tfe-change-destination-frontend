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

package forms.sections.transportUnit

import base.SpecBase
import fixtures.messages.sections.transportUnit.TransportUnitGiveMoreInformationMessages
import forms.behaviours.StringFieldBehaviours
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import play.api.data.FormError
import play.api.i18n.Messages

class TransportUnitGiveMoreInformationFormProviderSpec extends SpecBase with StringFieldBehaviours {

  val invalidCharacterKey = "transportUnitGiveMoreInformation.error.character"
  val lengthKey = "transportUnitGiveMoreInformation.error.length"
  val invalidCharactersKey = "transportUnitGiveMoreInformation.error.xss"
  val maxLength = 350
  implicit val msgs: Messages = messages(Seq(TransportUnitGiveMoreInformationMessages.English.lang))

  ".value" - {

    val fieldName = "value"
    "display the correct error message" - {
      val form = new TransportUnitGiveMoreInformationFormProvider()()

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        "0" * maxLength
      )

      behave like fieldWithXSSCharacters(
        form,
        fieldName,
        FormError(fieldName, invalidCharactersKey, Seq(XSS_REGEX))
      )

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength = maxLength,
        lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
      )

      "alphanumeric characters aren't used" in {
        val data = Map("value" -> "..")
        val result = form.bind(data)

        result.errors must contain only FormError("value", invalidCharacterKey, Seq(ALPHANUMERIC_REGEX))
      }
    }
  }
}
