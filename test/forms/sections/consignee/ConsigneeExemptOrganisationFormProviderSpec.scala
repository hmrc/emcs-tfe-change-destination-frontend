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

import fixtures.OrganisationDetailsFixtures
import forms.behaviours.StringFieldBehaviours
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import play.api.data.FormError

class ConsigneeExemptOrganisationFormProviderSpec extends StringFieldBehaviours with OrganisationDetailsFixtures {

  val form = new ConsigneeExemptOrganisationFormProvider()()

  val memberStateField = "memberState"
  val certificateSerialNumberField = "certificateSerialNumber"

  def formAnswersMap(updateField: Option[String] = None, updateAnswer: Option[String] = None): Map[String, String] = {

    val validAnswerMap = Map(
      memberStateField -> exemptOrganisationDetailsModel.memberState,
      certificateSerialNumberField -> exemptOrganisationDetailsModel.certificateSerialNumber
    )

    def update(fieldName: String, newAnswer: String) = validAnswerMap.map {
      case (`fieldName`, _) => (fieldName, newAnswer)
      case fieldNameAndAnswer => fieldNameAndAnswer
    }

    (updateField, updateAnswer) match {
      case (Some(field), Some(answer)) => update(field, answer)
      case _ => validAnswerMap
    }
  }

  "all fields must bind when valid data is entered" in {

    val actual = form.bind(formAnswersMap())
    actual.errors mustBe Seq()
    actual.value.value mustBe exemptOrganisationDetailsModel
  }

  s".$memberStateField" - {

    val requiredKey = s"consigneeExemptOrganisation.memberState.error.required"

    "must error if no value is supplied" in {

      val answer = ""

      val expectedResult = Seq(FormError(memberStateField, requiredKey, Seq()))

      val actualResult = form.bind(formAnswersMap(Some(memberStateField), Some(answer)))

      actualResult.errors mustBe expectedResult
    }

    "must error if no value is all emptry chars supplied" in {

      val answer = "     "

      val expectedResult = Seq(FormError(memberStateField, requiredKey, Seq()))

      val actualResult = form.bind(formAnswersMap(Some(memberStateField), Some(answer)))

      actualResult.errors mustBe expectedResult
    }
  }

  s".$certificateSerialNumberField" - {

    val fieldLength = 255

    val requiredKey = s"consigneeExemptOrganisation.certificateSerialNumber.error.required"
    val lengthKey = s"consigneeExemptOrganisation.certificateSerialNumber.error.length"
    val invalidKey = s"consigneeExemptOrganisation.certificateSerialNumber.error.xss"
    val charactersKey = s"consigneeExemptOrganisation.certificateSerialNumber.error.character"

    "must error if no value is supplied" in {

      val answer = ""

      val expectedResult = Seq(FormError(certificateSerialNumberField, requiredKey, Seq()))

      val actualResult = form.bind(formAnswersMap(Some(certificateSerialNumberField), Some(answer)))

      actualResult.errors mustBe expectedResult
    }

    "must error if no value is all emptry chars supplied" in {

      val answer = "     "

      val expectedResult = Seq(FormError(certificateSerialNumberField, requiredKey, Seq()))

      val actualResult = form.bind(formAnswersMap(Some(certificateSerialNumberField), Some(answer)))

      actualResult.errors mustBe expectedResult
    }

    "must error when value exceeds max length" in {

      val invalidLengthAnswer = "A" * fieldLength + 1

      val expectedResult = Seq(FormError(certificateSerialNumberField, lengthKey, Seq(fieldLength)))

      val actualResult = form.bind(formAnswersMap(Some(certificateSerialNumberField), Some(invalidLengthAnswer)))

      actualResult.errors mustBe expectedResult
    }

    "must bind successfully when value equals max length" in {

      val validLengthAnswer = "A" * fieldLength

      val expectedResult = exemptOrganisationDetailsModel.copy(certificateSerialNumber = validLengthAnswer)

      val actualResult = form.bind(formAnswersMap(Some(certificateSerialNumberField), Some(validLengthAnswer)))

      actualResult.errors mustBe Seq()
      actualResult.get mustBe expectedResult
    }

    "must error when value contains invalid characters" in {

      val invalidAnswer = "aa>>aa"

      val expectedResult = Seq(FormError(certificateSerialNumberField, invalidKey, Seq(XSS_REGEX)))

      val actualResult = form.bind(formAnswersMap(Some(certificateSerialNumberField), Some(invalidAnswer)))

      actualResult.errors mustBe expectedResult
    }

    "must error when value contains only non-alphanumerics" in {

      val nonAlphanumericAnswer = "."

      val expectedResult = Seq(FormError(certificateSerialNumberField, charactersKey, Seq(ALPHANUMERIC_REGEX)))

      val actualResult = form.bind(formAnswersMap(Some(certificateSerialNumberField), Some(nonAlphanumericAnswer)))

      actualResult.errors mustBe expectedResult
    }
  }
}
