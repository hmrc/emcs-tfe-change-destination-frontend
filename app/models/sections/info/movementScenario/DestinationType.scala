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

package models.sections.info.movementScenario

import models.audit.Auditable
import models.{Enumerable, WithName}

sealed trait DestinationType {
  lazy val isDutyPaid: Boolean = DestinationType.dutyPaidDestinationTypes.contains(this)
}

object DestinationType extends Enumerable.Implicits {
  case object TaxWarehouse extends WithName("1") with DestinationType with Auditable {
    override val auditDescription: String = "TaxWarehouse"
  }

  case object RegisteredConsignee extends WithName("2") with DestinationType with Auditable {
    override val auditDescription: String = "RegisteredConsignee"
  }

  case object TemporaryRegisteredConsignee extends WithName("3") with DestinationType with Auditable {
    override val auditDescription: String = "TemporaryRegisteredConsignee"
  }

  case object DirectDelivery extends WithName("4") with DestinationType with Auditable {
    override val auditDescription: String = "DirectDelivery"
  }

  case object ExemptedOrganisation extends WithName("5") with DestinationType with Auditable {
    override val auditDescription: String = "ExemptedOrganisation"
  }

  case object Export extends WithName("6") with DestinationType with Auditable {
    override val auditDescription: String = "Export"
  }

  case object UnknownDestination extends WithName("8") with DestinationType with Auditable {
    override val auditDescription: String = "UnknownDestination"
  }

  case object CertifiedConsignee extends WithName("9") with DestinationType with Auditable {
    override val auditDescription: String = "CertifiedConsignee"
  }

  case object TemporaryCertifiedConsignee extends WithName("10") with DestinationType with Auditable {
    override val auditDescription: String = "TemporaryCertifiedConsignee"
  }

  case object ReturnToThePlaceOfDispatchOfTheConsignor extends WithName("11") with DestinationType with Auditable {
    override val auditDescription: String = "ReturnToThePlaceOfDispatchOfTheConsignor"
  }

  val dutyPaidDestinationTypes = Seq(CertifiedConsignee, TemporaryCertifiedConsignee, ReturnToThePlaceOfDispatchOfTheConsignor)

  val values: Seq[DestinationType] = Seq(
    TaxWarehouse,
    RegisteredConsignee,
    TemporaryRegisteredConsignee,
    DirectDelivery,
    ExemptedOrganisation,
    Export,
    UnknownDestination
  ) ++ dutyPaidDestinationTypes

  implicit val enumerable: Enumerable[DestinationType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
