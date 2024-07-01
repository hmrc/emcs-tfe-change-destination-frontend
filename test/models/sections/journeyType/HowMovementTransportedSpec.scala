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
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.i18n.Messages
import play.api.libs.json.{JsError, JsString, Json}
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

class HowMovementTransportedSpec extends AnyFreeSpec with Matchers with OptionValues {

  "HowMovementTransported" - {

    "have correct values" in {
      HowMovementTransported.values should contain allOf(
        HowMovementTransported.AirTransport,
        HowMovementTransported.FixedTransportInstallations,
        HowMovementTransported.InlandWaterwayTransport,
        HowMovementTransported.PostalConsignment,
        HowMovementTransported.RailTransport,
        HowMovementTransported.RoadTransport,
        HowMovementTransported.SeaTransport,
        HowMovementTransported.Other
      )
    }

    "have correct audit descriptions" in {
      HowMovementTransported.AirTransport.auditDescription shouldBe "AirTransport"
      HowMovementTransported.FixedTransportInstallations.auditDescription shouldBe "FixedTransportInstallations"
      HowMovementTransported.InlandWaterwayTransport.auditDescription shouldBe "InlandWaterwayTransport"
      HowMovementTransported.PostalConsignment.auditDescription shouldBe "PostalConsignment"
      HowMovementTransported.RailTransport.auditDescription shouldBe "RailTransport"
      HowMovementTransported.RoadTransport.auditDescription shouldBe "RoadTransport"
      HowMovementTransported.SeaTransport.auditDescription shouldBe "SeaTransport"
      HowMovementTransported.Other.auditDescription shouldBe "Other"
    }

    "generate correct options" in {
      implicit val messages: Messages = mock[Messages]
      when(messages.apply(anyString(), any())).thenReturn("test")

      val options = HowMovementTransported.options

      options should contain theSameElementsAs Seq(
        RadioItem(content = Text("test"), value = Some(HowMovementTransported.AirTransport.toString), id = Some("value_4")),
        RadioItem(content = Text("test"), value = Some(HowMovementTransported.FixedTransportInstallations.toString), id = Some("value_7")),
        RadioItem(content = Text("test"), value = Some(HowMovementTransported.InlandWaterwayTransport.toString), id = Some("value_8")),
        RadioItem(content = Text("test"), value = Some(HowMovementTransported.PostalConsignment.toString), id = Some("value_5")),
        RadioItem(content = Text("test"), value = Some(HowMovementTransported.RailTransport.toString), id = Some("value_2")),
        RadioItem(content = Text("test"), value = Some(HowMovementTransported.RoadTransport.toString), id = Some("value_3")),
        RadioItem(content = Text("test"), value = Some(HowMovementTransported.SeaTransport.toString), id = Some("value_1")),
        RadioItem(content = Text("test"), value = Some(HowMovementTransported.Other.toString), id = Some("value_0"))
      )
    }

    "have correct enumerable" in {
      val enumerable = HowMovementTransported.enumerable

      enumerable.withName(HowMovementTransported.AirTransport.toString) shouldBe Some(HowMovementTransported.AirTransport)
      enumerable.withName(HowMovementTransported.FixedTransportInstallations.toString) shouldBe Some(HowMovementTransported.FixedTransportInstallations)
      enumerable.withName(HowMovementTransported.InlandWaterwayTransport.toString) shouldBe Some(HowMovementTransported.InlandWaterwayTransport)
      enumerable.withName(HowMovementTransported.PostalConsignment.toString) shouldBe Some(HowMovementTransported.PostalConsignment)
      enumerable.withName(HowMovementTransported.RailTransport.toString) shouldBe Some(HowMovementTransported.RailTransport)
      enumerable.withName(HowMovementTransported.RoadTransport.toString) shouldBe Some(HowMovementTransported.RoadTransport)
      enumerable.withName(HowMovementTransported.SeaTransport.toString) shouldBe Some(HowMovementTransported.SeaTransport)
      enumerable.withName(HowMovementTransported.Other.toString) shouldBe Some(HowMovementTransported.Other)
      enumerable.withName("invalid") shouldBe None
    }

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
