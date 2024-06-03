/*
 * Copyright 2024 HM Revenue & Customs
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

import forms.behaviours.OptionFieldBehaviours
import models.sections.consignee.ConsigneeExportInformationType.{YesEoriNumber, YesVatNumber}
import play.api.data.FormError

class ConsigneeExportInformationFormProviderSpec extends OptionFieldBehaviours {

  "ConsigneeExportInformationFormProvider" - {

    val form = new ConsigneeExportInformationFormProvider().apply()

    "when a value is not provided" - {

      "must error with the expected message key" in {
        val boundForm = form.bind(Map("exportType" -> ""))
        boundForm.errors.headOption mustBe Some(FormError("exportType", "consigneeExportInformation.consigneeExportType.error.required", Seq()))
      }
    }

    "when choosing VAT Number" - {

      val vatNumberMaxLength: Int = 14

      "when a value is not provided" in {
        val boundForm = form.bind(
          Map(
            "exportType" -> YesVatNumber.toString,
            "vatNumber" -> ""
          )
        )

        boundForm.errors.headOption mustBe Some(FormError("vatNumber", "consigneeExportInformation.vatNumber.error.required", Seq()))
      }

      "when a value is too long" in {
        val boundForm = form.bind(
          Map(
            "exportType" -> YesVatNumber.toString,
            "vatNumber" -> "A" * (vatNumberMaxLength + 1)
          )
        )

        boundForm.errors.headOption mustBe Some(FormError("vatNumber", "consigneeExportInformation.vatNumber.error.length", Seq(vatNumberMaxLength)))
      }
    }

    "when choosing EORI Number" - {

      val eoriNumberMaxLength: Int = 17

      "when a value is not provided" in {
        val boundForm = form.bind(
          Map(
            "exportType" -> YesEoriNumber.toString,
            "eoriNumber" -> ""
          )
        )

        boundForm.errors.headOption mustBe Some(FormError("eoriNumber", "consigneeExportInformation.eoriNumber.error.required", Seq()))
      }

      "when a value is too long" in {
        val boundForm = form.bind(
          Map(
            "exportType" -> YesEoriNumber.toString,
            "eoriNumber" -> "A" * (eoriNumberMaxLength + 1)
          )
        )

        boundForm.errors.headOption mustBe Some(FormError("eoriNumber", "consigneeExportInformation.eoriNumber.error.length", Seq(eoriNumberMaxLength)))
      }
    }

  }
}
