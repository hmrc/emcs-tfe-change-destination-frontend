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

import models.audit.Auditable
import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait HowMovementTransported

object HowMovementTransported extends Enumerable.Implicits {

  case object AirTransport extends WithName("4") with HowMovementTransported with Auditable {
    override val auditDescription: String = "AirTransport"
  }

  case object FixedTransportInstallations extends WithName("7") with HowMovementTransported with Auditable {
    override val auditDescription: String = "FixedTransportInstallations"
  }

  case object InlandWaterwayTransport extends WithName("8") with HowMovementTransported with Auditable {
    override val auditDescription: String = "InlandWaterwayTransport"
  }

  case object PostalConsignment extends WithName("5") with HowMovementTransported with Auditable {
    override val auditDescription: String = "PostalConsignment"
  }

  case object RailTransport extends WithName("2") with HowMovementTransported with Auditable {
    override val auditDescription: String = "RailTransport"
  }

  case object RoadTransport extends WithName("3") with HowMovementTransported with Auditable {
    override val auditDescription: String = "RoadTransport"
  }

  case object SeaTransport extends WithName("1") with HowMovementTransported with Auditable {
    override val auditDescription: String = "SeaTransport"
  }

  case object Other extends WithName("0") with HowMovementTransported with Auditable {
    override val auditDescription: String = "Other"
  }

  val values: Seq[HowMovementTransported] = Seq(
    AirTransport,
    FixedTransportInstallations,
    InlandWaterwayTransport,
    PostalConsignment,
    RailTransport,
    RoadTransport,
    SeaTransport,
    Other
  )

  val transportModeToMaxDays: Map[HowMovementTransported, Int] = Map(
    AirTransport -> 20,
    FixedTransportInstallations -> 15,
    InlandWaterwayTransport -> 35,
    PostalConsignment -> 30,
    RailTransport -> 35,
    RoadTransport -> 35,
    SeaTransport -> 45,
    Other -> 45
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, _) =>
      RadioItem(
        content = Text(messages(s"howMovementTransported.${value.toString}")),
        value = Some(value.toString),
        id = Some(s"value_${value.toString}")
      )
  }

  implicit val enumerable: Enumerable[HowMovementTransported] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
