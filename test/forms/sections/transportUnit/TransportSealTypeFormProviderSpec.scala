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

import fixtures.TransportUnitFixtures
import forms.behaviours.StringFieldBehaviours
import forms.{ALPHANUMERIC_REGEX, XSS_REGEX}
import play.api.data.FormError

class TransportSealTypeFormProviderSpec extends StringFieldBehaviours with TransportUnitFixtures {

  val sealTypeField = "value"
  val moreInfoField = "moreInfo"

  val maxLength = 350

  def formAnswersMap(sealType: String = transportSealTypeModelMax.sealType,
                     moreInfo: Option[String] = transportSealTypeModelMax.moreInfo): Map[String, String] =
    Map(
      sealTypeField -> sealType,
      moreInfoField -> moreInfo.getOrElse("")
    )

  val form = new TransportSealTypeFormProvider()()

  "TransportSealTypeFormProvider" - {

    "when all fields are valid" in {

      val data = formAnswersMap()

      val expectedResult = Some(transportSealTypeModelMax)
      val expectedErrors = Seq.empty

      val actualResult = form.bind(data)

      actualResult.errors mustBe expectedErrors
      actualResult.value mustBe expectedResult
    }

    "sealType field" - {

      val characterLimit = 35

      "should validate" - {

        "when max characters entered" in {

          val givenAnswer = "a" * characterLimit
          val data = formAnswersMap(sealType = givenAnswer)

          val expectedResult = Some(transportSealTypeModelMax.copy(sealType = givenAnswer))
          val expectedErrors = Seq.empty

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedErrors
          actualResult.value mustBe expectedResult
        }
      }

      "should error" - {

        "when not entered" in {

          val data = formAnswersMap(sealType = "")

          val expectedResult = Seq(FormError(sealTypeField, s"transportSealType.sealType.error.required"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when too many characters entered" in {

          val data = formAnswersMap(sealType = "a" * (characterLimit + 1))

          val expectedResult = Seq(FormError(sealTypeField, s"transportSealType.sealType.error.length", List(characterLimit)))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when invalid characters are entered" in {

          val data = formAnswersMap(sealType = "something >")

          val expectedResult = Seq(FormError(sealTypeField, s"transportSealType.sealType.error.invalid", List(XSS_REGEX)))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }
    }

    "moreInfo field" - {

      val characterLimit = 350

      "should validate" - {

        "when not entered" in {

          val givenAnswer = None
          val data = formAnswersMap(moreInfo = givenAnswer)

          val expectedResult = Some(transportSealTypeModelMax.copy(moreInfo = None))
          val expectedErrors = Seq.empty

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedErrors
          actualResult.value mustBe expectedResult
        }

        "when max characters entered" in {

          val givenAnswer = Some("a" * characterLimit)
          val data = formAnswersMap(moreInfo = givenAnswer)

          val expectedResult = Some(transportSealTypeModelMax.copy(moreInfo = givenAnswer))
          val expectedErrors = Seq.empty

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedErrors
          actualResult.value mustBe expectedResult
        }
      }

      "should error" - {

        "when too many characters entered" in {

          val data = formAnswersMap(moreInfo = Some("a" * (characterLimit + 1)))

          val expectedResult = Seq(FormError(moreInfoField, s"transportSealType.moreInfo.error.length", List(characterLimit)))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when invalid characters are entered" in {

          val data = formAnswersMap(moreInfo = Some("some more information >"))

          val expectedResult = Seq(FormError(moreInfoField, s"transportSealType.moreInfo.error.invalidCharacter", List(XSS_REGEX)))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "when no alphanumeric characters are entered" in {

          val data = formAnswersMap(moreInfo = Some("_)("))

          val expectedResult = Seq(FormError(moreInfoField, s"transportSealType.moreInfo.error.invalid", List(ALPHANUMERIC_REGEX)))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }
    }
  }
}
