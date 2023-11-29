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
import fixtures.messages.sections.transportUnit.TransportUnitGiveMoreInformationChoiceMessages
import forms.behaviours.BooleanFieldBehaviours
import models.sections.transportUnit.TransportUnitType
import play.api.data.FormError
import play.api.i18n.Messages

class TransportUnitGiveMoreInformationChoiceFormProviderSpec extends SpecBase with BooleanFieldBehaviours {

  val requiredKey = "transportUnitGiveMoreInformationChoice.error.required"
  val invalidKey = "error.boolean"
  implicit val msgs: Messages = messages(Seq(TransportUnitGiveMoreInformationChoiceMessages.English.lang))

  ".value" - {

    val fieldName = "value"

    "display the correct error message" - {

      TransportUnitType.values.foreach(
        transportUnitType => {
          s"the transport unit type is $transportUnitType" - {
            val form = new TransportUnitGiveMoreInformationChoiceFormProvider()(transportUnitType)

            behave like booleanField(
              form,
              fieldName,
              invalidError = FormError(fieldName, invalidKey, Seq(msgs(s"transportUnitGiveMoreInformationChoice.transportUnitType.$transportUnitType")))
            )

            behave like mandatoryField(
              form,
              fieldName,
              requiredError = FormError(fieldName, requiredKey, Seq(msgs(s"transportUnitGiveMoreInformationChoice.transportUnitType.$transportUnitType")))
            )
          }
        }
      )
    }
  }
}
