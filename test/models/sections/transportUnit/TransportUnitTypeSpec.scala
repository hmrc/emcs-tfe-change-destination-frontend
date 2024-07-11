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

package models.sections.transportUnit

import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

class TransportUnitTypeSpec extends AnyFlatSpec with Matchers {

  "TransportUnitType" should "have correct values" in {
    TransportUnitType.values shouldBe Seq(
      TransportUnitType.Container,
      TransportUnitType.FixedTransport,
      TransportUnitType.Tractor,
      TransportUnitType.Trailer,
      TransportUnitType.Vehicle
    )
  }

  it should "have correct audit descriptions" in {
    TransportUnitType.Container.auditDescription shouldBe "Container"
    TransportUnitType.Vehicle.auditDescription shouldBe "Vehicle"
    TransportUnitType.Trailer.auditDescription shouldBe "Trailer"
    TransportUnitType.Tractor.auditDescription shouldBe "Tractor"
    TransportUnitType.FixedTransport.auditDescription shouldBe "FixedTransport"
  }

  it should "generate correct options" in {
    implicit val messages: Messages = mock[Messages]
    when(messages.apply(anyString(), any())).thenReturn("test")

    val options = TransportUnitType.options

    options should contain theSameElementsAs Seq(
      RadioItem(content = Text("test"), value = Some(TransportUnitType.Container.toString), id = Some("value_1")),
      RadioItem(content = Text("test"), value = Some(TransportUnitType.Vehicle.toString), id = Some("value_2")),
      RadioItem(content = Text("test"), value = Some(TransportUnitType.Trailer.toString), id = Some("value_3")),
      RadioItem(content = Text("test"), value = Some(TransportUnitType.Tractor.toString), id = Some("value_4")),
      RadioItem(content = Text("test"), value = Some(TransportUnitType.FixedTransport.toString), id = Some("value_5"))
    )
  }

  it should "parse correct TransportUnitType" in {
    TransportUnitType.parseTransportUnitType(TransportUnitType.Container.toString) shouldBe Some(TransportUnitType.Container)
    TransportUnitType.parseTransportUnitType(TransportUnitType.Vehicle.toString) shouldBe Some(TransportUnitType.Vehicle)
    TransportUnitType.parseTransportUnitType(TransportUnitType.Trailer.toString) shouldBe Some(TransportUnitType.Trailer)
    TransportUnitType.parseTransportUnitType(TransportUnitType.Tractor.toString) shouldBe Some(TransportUnitType.Tractor)
    TransportUnitType.parseTransportUnitType(TransportUnitType.FixedTransport.toString) shouldBe Some(TransportUnitType.FixedTransport)
    TransportUnitType.parseTransportUnitType("invalid") shouldBe None
  }

  it should "have correct enumerable" in {
    val enumerable = TransportUnitType.enumerable

    enumerable.withName(TransportUnitType.Container.toString) shouldBe Some(TransportUnitType.Container)
    enumerable.withName(TransportUnitType.Vehicle.toString) shouldBe Some(TransportUnitType.Vehicle)
    enumerable.withName(TransportUnitType.Trailer.toString) shouldBe Some(TransportUnitType.Trailer)
    enumerable.withName(TransportUnitType.Tractor.toString) shouldBe Some(TransportUnitType.Tractor)
    enumerable.withName(TransportUnitType.FixedTransport.toString) shouldBe Some(TransportUnitType.FixedTransport)
    enumerable.withName("invalid") shouldBe None
  }
}
