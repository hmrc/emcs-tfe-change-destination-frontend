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

package forms.sections.transportArranger

import fixtures.BaseFixtures
import forms.ONLY_ALPHANUMERIC_REGEX
import forms.behaviours.StringFieldBehaviours
import models.VatNumberModel
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import models.sections.transportArranger.TransportArranger
import play.api.data.FormError

class TransportArrangerVatFormProviderSpec extends StringFieldBehaviours with BaseFixtures {

  val inputRequiredKey = "transportArrangerVat.error.input.required"
  val lengthKey = "transportArrangerVat.error.length"
  val alphanumericKey = "transportArrangerVat.error.alphanumeric"
  val maxLength = 14

  val form = new TransportArrangerVatFormProvider()(GoodsOwner)

  "when binding 'Yes'" - {

    "when VAT number contains invalid characters" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          TransportArrangerVatFormProvider.hasVatNumberField -> "true",
          TransportArrangerVatFormProvider.vatNumberField -> "<"
        ))

        boundForm.errors mustBe Seq(FormError(
          TransportArrangerVatFormProvider.vatNumberField,
          alphanumericKey,
          Seq(ONLY_ALPHANUMERIC_REGEX)
        ))
      }
    }

    "when VAT number is too long" - {

      "must error when binding the form" in {

        val boundForm = form.bind(Map(
          TransportArrangerVatFormProvider.hasVatNumberField -> "true",
          TransportArrangerVatFormProvider.vatNumberField -> "a" * (maxLength + 1)
        ))

        boundForm.errors mustBe Seq(FormError(
          TransportArrangerVatFormProvider.vatNumberField,
          lengthKey,
          Seq(maxLength)
        ))
      }
    }

    "when VAT number is valid" - {

      "must bind the form successfully when true with value (spaces exist but trim them out)" in {

        val boundForm = form.bind(Map(
          TransportArrangerVatFormProvider.hasVatNumberField -> "true",
          TransportArrangerVatFormProvider.vatNumberField -> "GB123 456-178"
        ))

        boundForm.value mustBe Some(VatNumberModel(hasVatNumber = true, Some("GB123456178")))
      }

      "must bind the form successfully when true with value" in {

        val boundForm = form.bind(Map(
          TransportArrangerVatFormProvider.hasVatNumberField -> "true",
          TransportArrangerVatFormProvider.vatNumberField -> testVatNumber
        ))

        boundForm.value mustBe Some(VatNumberModel(hasVatNumber = true, Some(testVatNumber)))
      }
    }
  }

  "when binding 'No'" - {

    "must bind the form successfully when false with value (should be transformed to None on bind)" in {

      val boundForm = form.bind(Map(
        TransportArrangerVatFormProvider.hasVatNumberField -> "false",
        TransportArrangerVatFormProvider.vatNumberField -> "brand"
      ))

      boundForm.value mustBe Some(VatNumberModel(hasVatNumber = false, None))
    }

    "must bind the form successfully when false with NO value" in {

      val boundForm = form.bind(Map(
        TransportArrangerVatFormProvider.hasVatNumberField -> "false"
      ))

      boundForm.value mustBe Some(VatNumberModel(hasVatNumber = false, None))
    }
  }


  Seq(GoodsOwner, Other).foreach { arrangerType =>

    def choiceRequiredKey(arrangerType: TransportArranger) = s"transportArrangerVat.error.radio.$arrangerType.required"

    s"when an option hasn't been selected for arranger type: $arrangerType" - {

      "must error when binding the form" in {

        val form = new TransportArrangerVatFormProvider()(arrangerType)

        val boundForm = form.bind(Map(
          TransportArrangerVatFormProvider.hasVatNumberField -> ""
        ))

        boundForm.errors mustBe Seq(FormError(
          TransportArrangerVatFormProvider.hasVatNumberField,
          choiceRequiredKey(arrangerType),
          Seq()
        ))
      }
    }
  }
}
