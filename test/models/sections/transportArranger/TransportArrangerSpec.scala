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

package models.sections.transportArranger

import models.sections.info.movementScenario.MovementScenario
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

class TransportArrangerSpec extends AnyFlatSpec with Matchers {

  "TransportArranger" should "have correct values" in {
    TransportArranger.allValues shouldBe Seq(
      TransportArranger.Consignor,
      TransportArranger.Consignee,
      TransportArranger.GoodsOwner,
      TransportArranger.Other
    )
  }

  it should "have correct display values" in {
    TransportArranger.displayValues(MovementScenario.UnknownDestination) shouldBe Seq(
      TransportArranger.Consignor,
      TransportArranger.GoodsOwner,
      TransportArranger.Other
    )
    MovementScenario.values.filterNot(_ == MovementScenario.UnknownDestination).foreach { scenario =>
      TransportArranger.displayValues(scenario) shouldBe Seq(
        TransportArranger.Consignor,
        TransportArranger.Consignee,
        TransportArranger.GoodsOwner,
        TransportArranger.Other
      )
    }
  }

  it should "have correct audit descriptions" in {
    TransportArranger.Consignor.auditDescription shouldBe "Consignor"
    TransportArranger.Consignee.auditDescription shouldBe "Consignee"
    TransportArranger.GoodsOwner.auditDescription shouldBe "GoodsOwner"
    TransportArranger.Other.auditDescription shouldBe "Other"
  }

  it should "generate correct options" in {
    implicit val messages: Messages = mock[Messages]
    when(messages.apply(anyString(),any())).thenReturn("test")

    val options = TransportArranger.options(MovementScenario.UkTaxWarehouse.GB)

    options should contain theSameElementsAs Seq(
      RadioItem(content = Text("test"), value = Some(TransportArranger.Consignor.toString), id = Some("value_0")),
      RadioItem(content = Text("test"), value = Some(TransportArranger.Consignee.toString), id = Some("value_1")),
      RadioItem(content = Text("test"), value = Some(TransportArranger.GoodsOwner.toString), id = Some("value_2")),
      RadioItem(content = Text("test"), value = Some(TransportArranger.Other.toString), id = Some("value_3"))
    )
  }

  it should "have correct enumerable" in {
    val enumerable = TransportArranger.enumerable

    enumerable.withName(TransportArranger.Consignor.toString) shouldBe Some(TransportArranger.Consignor)
    enumerable.withName(TransportArranger.Consignee.toString) shouldBe Some(TransportArranger.Consignee)
    enumerable.withName(TransportArranger.GoodsOwner.toString) shouldBe Some(TransportArranger.GoodsOwner)
    enumerable.withName(TransportArranger.Other.toString) shouldBe Some(TransportArranger.Other)
    enumerable.withName("invalid") shouldBe None
  }
}