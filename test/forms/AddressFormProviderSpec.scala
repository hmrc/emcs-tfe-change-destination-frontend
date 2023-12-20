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

package forms

import fixtures.UserAddressFixtures
import forms.behaviours.FieldBehaviours
import models.UserAddress
import play.api.data.FormError

class AddressFormProviderSpec extends FieldBehaviours with UserAddressFixtures {

  case class TestParameters(fieldName: String, fieldLength: Int, isMandatory: Boolean)

  val form = new AddressFormProvider()()

  val testPropertyParameters = TestParameters(fieldName = "property", fieldLength = 11, isMandatory = false)
  val testStreetParameters = TestParameters(fieldName = "street", fieldLength = 65, isMandatory = true)
  val testTownParameters = TestParameters(fieldName = "town", fieldLength = 50, isMandatory = true)
  val testPostcodeParameters = TestParameters(fieldName = "postcode", fieldLength = 10, isMandatory = true)

  def formAnswersMap(updateField: Option[String] = None,
                     updateAnswer: Option[String] = None): Map[String, String] = {

    val validAnswers = Map(
      testPropertyParameters.fieldName -> userAddressModelMax.property.value,
      testStreetParameters.fieldName -> userAddressModelMax.street,
      testTownParameters.fieldName -> userAddressModelMax.town,
      testPostcodeParameters.fieldName -> userAddressModelMax.postcode
    )

    def update(fieldName: String, newAnswer: String) = validAnswers.map {
      case (`fieldName`, _) => (fieldName, newAnswer)
      case fieldNameAndAnswer => fieldNameAndAnswer
    }

    (updateField, updateAnswer) match {
      case (Some(field), Some(answer)) => update(field, answer)
      case _ => validAnswers
    }
  }

  def userAddressModel(updateField: Option[String] = None,
                       updateAnswer: Option[String] = None): UserAddress =
    (updateField, updateAnswer) match {
      case (Some(testPropertyParameters.fieldName), answer) => userAddressModelMax.copy(property = answer)
      case (Some(testStreetParameters.fieldName), Some(answer)) => userAddressModelMax.copy(street = answer)
      case (Some(testTownParameters.fieldName), Some(answer)) => userAddressModelMax.copy(town = answer)
      case (Some(testPostcodeParameters.fieldName), Some(answer)) => userAddressModelMax.copy(postcode = answer)
      case _ => userAddressModelMax
    }

  "all fields must bind when maximum valid data is entered" in {

    val actual = form.bind(formAnswersMap())
    actual.errors mustBe Seq()
    actual.value.value mustBe userAddressModelMax
  }

  "all fields must bind when minimum valid data is entered" in {

    val actual = form.bind(formAnswersMap(Some(testPropertyParameters.fieldName), Some("")))
    actual.errors mustBe Seq()
    actual.value.value mustBe userAddressModel(Some(testPropertyParameters.fieldName), None)
  }

  Seq(
    testPropertyParameters, testStreetParameters, testTownParameters, testPostcodeParameters
  ) foreach { testParameters =>

    val TestParameters(fieldName, fieldLength, isMandatory) = testParameters

    s".$fieldName" - {

      val requiredKey = s"address.$fieldName.error.required"
      val lengthKey = s"address.$fieldName.error.length"
      val invalidKey = s"address.$fieldName.error.invalid"
      val charactersKey = s"address.$fieldName.error.characters"

      if (isMandatory) {

        "must error if no value is supplied" in {

          val answer = ""

          val expectedResult = Seq(FormError(fieldName, requiredKey, Seq()))

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(answer)))

          actualResult.errors mustBe expectedResult
        }

        "must error if no value is all emptry chars supplied" in {

          val answer = "     "

          val expectedResult = Seq(FormError(fieldName, requiredKey, Seq()))

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(answer)))

          actualResult.errors mustBe expectedResult
        }

        "must bind successfully when value is supplied" in {

          val answer = "foo"

          val expectedResult = userAddressModel(Some(fieldName), Some("foo"))

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(answer)))

          actualResult.errors mustBe Seq()
          actualResult.get mustBe expectedResult
        }
      } else {

        "must bind successfully if no value is supplied" in {

          val answer = ""

          val expectedResult = userAddressModel(Some(fieldName), None)

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(answer)))

          actualResult.errors mustBe Seq()
          actualResult.get mustBe expectedResult
        }

        "must bind successfully when value is supplied" in {

          val answer = "foo"

          val expectedResult = userAddressModel(Some(fieldName), Some(answer))

          val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(answer)))

          actualResult.errors mustBe Seq()
          actualResult.get mustBe expectedResult
        }
      }

      "must error when value exceeds max length" in {

        val invalidLengthAnswer = "A" * fieldLength + 1

        val expectedResult = Seq(FormError(fieldName, lengthKey, Seq(fieldLength)))

        val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(invalidLengthAnswer)))

        actualResult.errors mustBe expectedResult
      }

      "must bind successfully when value equals max length" in {

        val validLengthAnswer = "A" * fieldLength

        val expectedResult = userAddressModel(Some(fieldName), Some("A" * fieldLength))

        val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(validLengthAnswer)))

        actualResult.errors mustBe Seq()
        actualResult.get mustBe expectedResult
      }

      "must error when value contains invalid characters" in {

        val invalidAnswer = "aa>>aa"

        val expectedResult = Seq(FormError(fieldName, invalidKey, Seq(XSS_REGEX)))

        val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(invalidAnswer)))

        actualResult.errors mustBe expectedResult
      }

      "must error when value contains only non-alphanumerics" in {

        val nonAlphanumericAnswer = "."

        val expectedResult = Seq(FormError(fieldName, charactersKey, Seq(ALPHANUMERIC_REGEX)))

        val actualResult = form.bind(formAnswersMap(Some(fieldName), Some(nonAlphanumericAnswer)))

        actualResult.errors mustBe expectedResult
      }
    }
  }

}
