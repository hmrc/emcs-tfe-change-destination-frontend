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

package forms.sections.items

import base.SpecBase
import fixtures.messages.sections.items.ItemWineOriginMessages
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError
import play.api.i18n.Messages

class ItemWineOriginFormProviderSpec extends SpecBase with StringFieldBehaviours {

  private val countries = Seq(countryModelAU, countryModelBR)
  val requiredKey = "itemWineOrigin.error.required"

  val form = new ItemWineOriginFormProvider().apply(countries)

  ".value" - {

    val fieldName = "country"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      "BR"
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind when the value provided is not in the countries list" in {
      val result = form.bind(Map(fieldName -> "ZZ")).apply(fieldName)
      result.errors mustEqual Seq(FormError(fieldName, requiredKey, Seq()))
    }
  }

  "Error Messages" - {

    Seq(ItemWineOriginMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        "have the correct error message for alphanumeric" in {

          msgs(requiredKey) mustBe messagesForLanguage.errorRequired
        }
      }
    }
  }
}
