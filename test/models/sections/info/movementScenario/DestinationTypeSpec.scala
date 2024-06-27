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

package models.sections.info.movementScenario

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DestinationTypeSpec extends AnyFlatSpec with Matchers {

  "DestinationType" should "have correct values" in {
    DestinationType.values should contain allOf(
      DestinationType.TaxWarehouse,
      DestinationType.RegisteredConsignee,
      DestinationType.TemporaryRegisteredConsignee,
      DestinationType.DirectDelivery,
      DestinationType.ExemptedOrganisation,
      DestinationType.Export,
      DestinationType.UnknownDestination,
      DestinationType.CertifiedConsignee,
      DestinationType.TemporaryCertifiedConsignee,
      DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor
    )
  }

  it should "have correct audit descriptions" in {
    DestinationType.TaxWarehouse.auditDescription shouldBe "TaxWarehouse"
    DestinationType.RegisteredConsignee.auditDescription shouldBe "RegisteredConsignee"
    DestinationType.TemporaryRegisteredConsignee.auditDescription shouldBe "TemporaryRegisteredConsignee"
    DestinationType.DirectDelivery.auditDescription shouldBe "DirectDelivery"
    DestinationType.ExemptedOrganisation.auditDescription shouldBe "ExemptedOrganisation"
    DestinationType.Export.auditDescription shouldBe "Export"
    DestinationType.UnknownDestination.auditDescription shouldBe "UnknownDestination"
    DestinationType.CertifiedConsignee.auditDescription shouldBe "CertifiedConsignee"
    DestinationType.TemporaryCertifiedConsignee.auditDescription shouldBe "TemporaryCertifiedConsignee"
    DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor.auditDescription shouldBe "ReturnToThePlaceOfDispatchOfTheConsignor"
  }

  it should "correctly identify duty paid destination types" in {
    DestinationType.CertifiedConsignee.isDutyPaid shouldBe true
    DestinationType.TemporaryCertifiedConsignee.isDutyPaid shouldBe true
    DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor.isDutyPaid shouldBe true
    DestinationType.TaxWarehouse.isDutyPaid shouldBe false
    DestinationType.RegisteredConsignee.isDutyPaid shouldBe false
    DestinationType.TemporaryRegisteredConsignee.isDutyPaid shouldBe false
    DestinationType.DirectDelivery.isDutyPaid shouldBe false
    DestinationType.ExemptedOrganisation.isDutyPaid shouldBe false
    DestinationType.Export.isDutyPaid shouldBe false
    DestinationType.UnknownDestination.isDutyPaid shouldBe false
  }

  it should "have correct enumerable" in {
    val enumerable = DestinationType.enumerable

    enumerable.withName(DestinationType.TaxWarehouse.toString) shouldBe Some(DestinationType.TaxWarehouse)
    enumerable.withName(DestinationType.RegisteredConsignee.toString) shouldBe Some(DestinationType.RegisteredConsignee)
    enumerable.withName(DestinationType.TemporaryRegisteredConsignee.toString) shouldBe Some(DestinationType.TemporaryRegisteredConsignee)
    enumerable.withName(DestinationType.DirectDelivery.toString) shouldBe Some(DestinationType.DirectDelivery)
    enumerable.withName(DestinationType.ExemptedOrganisation.toString) shouldBe Some(DestinationType.ExemptedOrganisation)
    enumerable.withName(DestinationType.Export.toString) shouldBe Some(DestinationType.Export)
    enumerable.withName(DestinationType.UnknownDestination.toString) shouldBe Some(DestinationType.UnknownDestination)
    enumerable.withName(DestinationType.CertifiedConsignee.toString) shouldBe Some(DestinationType.CertifiedConsignee)
    enumerable.withName(DestinationType.TemporaryCertifiedConsignee.toString) shouldBe Some(DestinationType.TemporaryCertifiedConsignee)
    enumerable.withName(DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor.toString) shouldBe Some(DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor)
    enumerable.withName("invalid") shouldBe None
  }
}

