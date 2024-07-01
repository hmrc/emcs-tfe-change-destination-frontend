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

package models.sections.guarantor

import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

class GuarantorArrangerSpec extends AnyFlatSpec with Matchers {

  "GuarantorArranger" should "have correct values" in {
    GuarantorArranger.values should contain allOf(
      GuarantorArranger.Consignor,
      GuarantorArranger.Consignee,
      GuarantorArranger.GoodsOwner,
      GuarantorArranger.Transporter,
      GuarantorArranger.NoGuarantorRequiredUkToEu,
      GuarantorArranger.NoGuarantorRequired
    )
  }

  it should "have correct audit descriptions" in {
    GuarantorArranger.Consignor.auditDescription shouldBe "Consignor"
    GuarantorArranger.Consignee.auditDescription shouldBe "Consignee"
    GuarantorArranger.GoodsOwner.auditDescription shouldBe "GoodsOwner"
    GuarantorArranger.Transporter.auditDescription shouldBe "Transporter"
    GuarantorArranger.NoGuarantorRequiredUkToEu.auditDescription shouldBe "NoGuarantorRequiredUkToEu"
    GuarantorArranger.NoGuarantorRequired.auditDescription shouldBe "NoGuarantorRequired"
  }

  it should "generate correct options" in {
    implicit val messages: Messages = mock[Messages]
    when(messages.apply(anyString(), any())).thenReturn("test")

    val options = GuarantorArranger.options

    options should contain theSameElementsAs Seq(
      RadioItem(content = Text("test"), value = Some(GuarantorArranger.Consignor.toString), id = Some("value_1")),
      RadioItem(content = Text("test"), value = Some(GuarantorArranger.Consignee.toString), id = Some("value_4")),
      RadioItem(content = Text("test"), value = Some(GuarantorArranger.GoodsOwner.toString), id = Some("value_3")),
      RadioItem(content = Text("test"), value = Some(GuarantorArranger.Transporter.toString), id = Some("value_2"))
    )
  }

  it should "have correct enumerable" in {
    val enumerable = GuarantorArranger.enumerable

    enumerable.withName(GuarantorArranger.Consignor.toString) shouldBe Some(GuarantorArranger.Consignor)
    enumerable.withName(GuarantorArranger.Consignee.toString) shouldBe Some(GuarantorArranger.Consignee)
    enumerable.withName(GuarantorArranger.GoodsOwner.toString) shouldBe Some(GuarantorArranger.GoodsOwner)
    enumerable.withName(GuarantorArranger.Transporter.toString) shouldBe Some(GuarantorArranger.Transporter)
    enumerable.withName(GuarantorArranger.NoGuarantorRequiredUkToEu.toString) shouldBe Some(GuarantorArranger.NoGuarantorRequiredUkToEu)
    enumerable.withName(GuarantorArranger.NoGuarantorRequired.toString) shouldBe Some(GuarantorArranger.NoGuarantorRequired)
    enumerable.withName("invalid") shouldBe None
  }

}