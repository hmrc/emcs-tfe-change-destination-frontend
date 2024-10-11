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

import models.sections.guarantor.GuarantorArranger.{Consignee, Consignor, GoodsOwner, Transporter}
import models.sections.info.movementScenario.MovementScenario
import models.sections.info.movementScenario.MovementScenario.{CertifiedConsignee, TemporaryCertifiedConsignee, UkTaxWarehouse}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.i18n.Messages
import play.api.test.Helpers.stubMessages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

class GuarantorArrangerSpec extends AnyFlatSpec with Matchers {

  implicit lazy val msgs: Messages = stubMessages()
  val scenariosWithConsignee: Seq[MovementScenario] =
    UkTaxWarehouse.toList ++ Seq(CertifiedConsignee, TemporaryCertifiedConsignee)

  "GuarantorArranger" should "have correct values" in {
    GuarantorArranger.allValues shouldBe Seq(
      GuarantorArranger.Consignor,
      GuarantorArranger.Consignee,
      GuarantorArranger.GoodsOwner,
      GuarantorArranger.Transporter,
      GuarantorArranger.NoGuarantorRequiredUkToEu,
      GuarantorArranger.NoGuarantorRequired
    )
  }

  it should "have correct display values" in {
    GuarantorArranger.displayValues(MovementScenario.UnknownDestination) shouldBe Seq(
      Consignor,
      GoodsOwner,
      Transporter
    )
    MovementScenario.values.filterNot(_ == MovementScenario.UnknownDestination).foreach { scenario =>
      GuarantorArranger.displayValues(scenario) shouldBe Seq(
        Consignor,
        Consignee,
        GoodsOwner,
        Transporter
      )
    }
  }

  it should "have correct audit descriptions" in {
    GuarantorArranger.Consignor.auditDescription shouldBe "Consignor"
    GuarantorArranger.Consignee.auditDescription shouldBe "Consignee"
    GuarantorArranger.GoodsOwner.auditDescription shouldBe "GoodsOwner"
    GuarantorArranger.Transporter.auditDescription shouldBe "Transporter"
    GuarantorArranger.NoGuarantorRequiredUkToEu.auditDescription shouldBe "NoGuarantorRequiredUkToEu"
    GuarantorArranger.NoGuarantorRequired.auditDescription shouldBe "NoGuarantorRequired"
  }

  it should "generate correct options when scenario is one which includes the consignee option" in {

    scenariosWithConsignee.foreach { scenario =>
      GuarantorArranger.options(scenario) shouldBe Seq(Consignor, Consignee, GoodsOwner, Transporter).map { value =>
        RadioItem(
          content = Text(msgs(s"guarantorArranger.${value.toString}")),
          value = Some(value.toString),
          id = Some(s"value_${value.toString}")
        )
      }
    }
  }

  it should "generate correct options when scenario is one which should NOT include the consignee option" in {
    MovementScenario.values.filterNot(scenariosWithConsignee.contains).foreach { scenario =>
      GuarantorArranger.options(scenario) shouldBe Seq(Consignor, GoodsOwner, Transporter).map { value =>
        RadioItem(
          content = Text(msgs(s"guarantorArranger.${value.toString}")),
          value = Some(value.toString),
          id = Some(s"value_${value.toString}")
        )
      }
    }
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