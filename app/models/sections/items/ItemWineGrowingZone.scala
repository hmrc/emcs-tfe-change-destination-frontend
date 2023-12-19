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

package models.sections.items

import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait ItemWineGrowingZone

object ItemWineGrowingZone extends Enumerable.Implicits {

  case object A      extends WithName("1") with ItemWineGrowingZone
  case object B      extends WithName("2") with ItemWineGrowingZone
  case object CI     extends WithName("3") with ItemWineGrowingZone
  case object CII    extends WithName("4") with ItemWineGrowingZone
  case object CIII_A extends WithName("5") with ItemWineGrowingZone
  case object CIII_B extends WithName("6") with ItemWineGrowingZone

  val values: Seq[ItemWineGrowingZone] = Seq(A, B, CI, CII, CIII_A, CIII_B)

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, index) =>
      RadioItem(
        content = Text(messages(s"itemWineGrowingZone.${value.toString}")),
        value   = Some(value.toString),
        id      = Some(s"value_$index")
      )
  }

  implicit val enumerable: Enumerable[ItemWineGrowingZone] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
