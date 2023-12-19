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
import fixtures.messages.sections.items.ItemPackagingSealTypeMessages
import forms.behaviours.BooleanFieldBehaviours
import forms.sections.items.ItemPackagingSealTypeFormProvider._
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import play.api.data.FormError
import play.api.i18n.Messages

class ItemPackagingSealTypeFormProviderSpec extends SpecBase with BooleanFieldBehaviours {

  val form = new ItemPackagingSealTypeFormProvider()()

  "form" - {
    "packagingSealType field" - {
      "must error when no data is entered" in {
        val boundForm = form.bind(Map(packagingSealTypeField -> ""))

        boundForm.errors mustBe Seq(FormError(
          packagingSealTypeField,
          sealTypeRequiredErrorKey
        ))
      }

      "must error when there are invalid characters" in {
        val boundForm = form.bind(Map(packagingSealTypeField -> "<>"))

        boundForm.errors mustBe Seq(FormError(
          packagingSealTypeField,
          sealTypeInvalidErrorKey,
          Seq(XSS_REGEX)
        ))
      }

      "must error when the input is too long" in {
        val boundForm = form.bind(Map(packagingSealTypeField -> "1" * (maxLengthSealTypeField + 1)))

        boundForm.errors mustBe Seq(FormError(
          packagingSealTypeField,
          sealTypeLengthErrorKey,
          Seq(maxLengthSealTypeField)
        ))

      }
    }
    "packagingSealInformation field" - {
      "must error when there are invalid characters" in {
        val boundForm = form.bind(Map(
          packagingSealTypeField -> "test",
          packagingSealInformationField -> "<javascript>"
        ))

        boundForm.errors mustBe Seq(FormError(
          packagingSealInformationField,
          sealInformationInvalidErrorKey,
          Seq(XSS_REGEX)
        ))
      }

      "must error when the input is too long" in {
        val boundForm = form.bind(Map(
          packagingSealTypeField -> "test",
          packagingSealInformationField -> "1" * (maxLengthSealInformationField + 1)
        ))

        boundForm.errors mustBe Seq(FormError(
          packagingSealInformationField,
          sealInformationLengthErrorKey,
          Seq(maxLengthSealInformationField)
        ))
      }

      "must error when the input is does not contain an alphanumeric" in {
        val boundForm = form.bind(Map(
          packagingSealTypeField -> "test",
          packagingSealInformationField -> "???"
        ))

        boundForm.errors mustBe Seq(FormError(
          packagingSealInformationField,
          sealInformationAlphanumericErrorKey,
          Seq(ALPHANUMERIC_REGEX)
        ))
      }
    }
  }

  "Error Messages" - {

    Seq(ItemPackagingSealTypeMessages.English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when output for language code '${messagesForLanguage.lang.code}'" - {

        s"for the $packagingSealTypeField field" - {

          "have the correct error message for required" in {

            msgs(sealTypeRequiredErrorKey) mustBe
              messagesForLanguage.errorSealTypeRequired
          }

          "have the correct error message for length" in {

            msgs(sealTypeLengthErrorKey, maxLengthSealTypeField) mustBe
              messagesForLanguage.errorSealTypeLength
          }

          "have the correct error message for xss" in {

            msgs(sealTypeInvalidErrorKey) mustBe
              messagesForLanguage.errorSealTypeInvalid
          }
        }

        s"for the $packagingSealInformationField field" - {

          "have the correct error message for length" in {

            msgs(sealInformationLengthErrorKey, maxLengthSealInformationField) mustBe
              messagesForLanguage.errorSealInformationLength
          }

          "have the correct error message for xss" in {

            msgs(sealInformationInvalidErrorKey) mustBe
              messagesForLanguage.errorSealInformationInvalid
          }

          "have the correct error message for alphanumeric check" in {

            msgs(sealInformationAlphanumericErrorKey) mustBe
              messagesForLanguage.errorSealInformationAlphanumeric
          }
        }
      }
    }
  }
}
