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

package models.sections.guarantor

import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait GuarantorArranger

object GuarantorArranger extends Enumerable.Implicits {

  case object Consignor extends WithName("1") with GuarantorArranger

  case object Consignee extends WithName("4") with GuarantorArranger

  case object GoodsOwner extends WithName("3") with GuarantorArranger

  case object Transporter extends WithName("2") with GuarantorArranger

  case object NoGuarantorRequiredUkToEu extends WithName("5") with GuarantorArranger

  case object NoTransporterNeeded extends WithName("5") with GuarantorArranger

  case object NoGuarantorRequired extends WithName("0") with GuarantorArranger

  val displayValues: Seq[GuarantorArranger] = Seq(
    Consignor, Consignee, GoodsOwner, Transporter, NoTransporterNeeded
  )

  val values: Seq[GuarantorArranger] = displayValues ++ Seq(NoGuarantorRequiredUkToEu, NoGuarantorRequired)

  def options(implicit messages: Messages): Seq[RadioItem] = displayValues.map {
    value =>
      RadioItem(
        content = Text(messages(s"guarantorArranger.${value.toString}")),
        value = Some(value.toString),
        id = Some(s"value_${value.toString}")
      )
  }

  implicit val enumerable: Enumerable[GuarantorArranger] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
