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

package models.sections.journeyType

import models.sections.journeyType.HowMovementTransported._
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsError, JsString, Json}

class HowMovementTransportedSpec extends AnyFreeSpec with Matchers with OptionValues {

  "HowMovementTransported" - {

    "must deserialise valid values" in {
      val howMovementTransported = HowMovementTransported.values.head

      JsString(howMovementTransported.toString).validate[HowMovementTransported].asOpt.value mustEqual howMovementTransported
    }

    "must fail to deserialise invalid values" in {
      val invalidValue = "beans"

      JsString(invalidValue).validate[HowMovementTransported] mustEqual JsError("error.invalid")
    }

    "must serialise" in {
      val howMovementTransported = HowMovementTransported.values.head

      Json.toJson(howMovementTransported) mustEqual JsString(howMovementTransported.toString)
    }

    ".transportModeToMaxDays" - {

      Seq(
        AirTransport -> 20,
        FixedTransportInstallations -> 15,
        InlandWaterwayTransport -> 35,
        PostalConsignment -> 30,
        RailTransport -> 35,
        RoadTransport -> 35,
        SeaTransport -> 45,
        Other -> 45
      ).foreach { transportModeToMaxDays =>

        s"must return ${transportModeToMaxDays._2} when called for ${transportModeToMaxDays._1}" in {

          HowMovementTransported.transportModeToMaxDays(transportModeToMaxDays._1) mustBe transportModeToMaxDays._2
        }
      }

    }
  }
}
