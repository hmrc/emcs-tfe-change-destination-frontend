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

package forms.sections.firstTransporter

import forms.ONLY_ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import models.VatNumberModel
import play.api.data.FormError

class FirstTransporterVatFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "firstTransporterVat.error.input.required"
  val lengthKey = "firstTransporterVat.error.length"
  val maxLength = 14

  val form = new FirstTransporterVatFormProvider()()

  "when binding 'Yes'" - {

    "when vat number contains invalid characters" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          FirstTransporterVatFormProvider.hasVatField -> "true",
          FirstTransporterVatFormProvider.vatNumberField -> "<"
        ))

        boundForm.errors mustBe Seq(FormError(
          FirstTransporterVatFormProvider.vatNumberField,
          FirstTransporterVatFormProvider.vatNumberInvalid,
          Seq(ONLY_ALPHANUMERIC_REGEX)
        ))
      }
    }

    "when vat number is not entered" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          FirstTransporterVatFormProvider.hasVatField -> "true",
          FirstTransporterVatFormProvider.vatNumberField -> ""
        ))

        boundForm.errors mustBe Seq(FormError(
          FirstTransporterVatFormProvider.vatNumberField,
          FirstTransporterVatFormProvider.vatRequired,
          Seq()
        ))
      }
    }

    "when vat number is too long" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          FirstTransporterVatFormProvider.hasVatField -> "true",
          FirstTransporterVatFormProvider.vatNumberField -> "a" * (FirstTransporterVatFormProvider.vatNumberMaxLength + 1)
        ))

        boundForm.errors mustBe Seq(FormError(
          FirstTransporterVatFormProvider.vatNumberField,
          FirstTransporterVatFormProvider.vatNumberLength,
          Seq(FirstTransporterVatFormProvider.vatNumberMaxLength)
        ))
      }
    }


    "when vat number is valid" - {

      "must bind the form successfully when true with value" in {

        val boundForm = form.bind(Map(
          FirstTransporterVatFormProvider.hasVatField -> "true",
          FirstTransporterVatFormProvider.vatNumberField -> "123456789"
        ))

        boundForm.value mustBe Some(VatNumberModel(hasVatNumber = true, Some("123456789")))
      }
    }
  }

  "when binding 'No'" - {

    "must bind the form successfully when false with value (should be transformed to None on bind)" in {

      val boundForm = form.bind(Map(
        FirstTransporterVatFormProvider.hasVatField -> "false",
        FirstTransporterVatFormProvider.vatNumberField -> "123456789"
      ))

      boundForm.value mustBe Some(VatNumberModel(hasVatNumber = false, None))
    }

    "must bind the form successfully when false with NO value" in {

      val boundForm = form.bind(Map(
        FirstTransporterVatFormProvider.hasVatField -> "false"
      ))

      boundForm.errors mustBe Seq()

      boundForm.value mustBe Some(VatNumberModel(hasVatNumber = false, None))
    }
  }

  "when no option is selected'" - {

    "must error with correct error message when binding the form" in {

      val boundForm = form.bind(Map(
        FirstTransporterVatFormProvider.hasVatField -> ""
      ))

      boundForm.errors mustBe Seq(FormError(
        FirstTransporterVatFormProvider.hasVatField,
        FirstTransporterVatFormProvider.radioRequired
      ))
    }
  }


}
