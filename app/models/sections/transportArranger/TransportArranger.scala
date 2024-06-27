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

package models.sections.transportArranger

import models.audit.Auditable
import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait TransportArranger

object TransportArranger extends Enumerable.Implicits {

  case object Consignor extends WithName("1") with TransportArranger with Auditable {
    override val auditDescription: String = "Consignor"
  }

  case object Consignee extends WithName("2") with TransportArranger with Auditable {
    override val auditDescription: String = "Consignee"
  }

  case object GoodsOwner extends WithName("3") with TransportArranger with Auditable {
    override val auditDescription: String = "GoodsOwner"
  }

  case object Other extends WithName("4") with TransportArranger with Auditable {
    override val auditDescription: String = "Other"
  }

  val values: Seq[TransportArranger] = Seq(
    Consignor, Consignee, GoodsOwner, Other
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, index) =>
      RadioItem(
        content = Text(messages(s"transportArranger.${value.toString}")),
        value = Some(value.toString),
        id = Some(s"value_$index")
      )
  }

  implicit val enumerable: Enumerable[TransportArranger] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
