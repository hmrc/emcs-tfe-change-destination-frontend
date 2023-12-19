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

package models.sections.transportUnit

import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait TransportUnitsAddToListModel

object TransportUnitsAddToListModel extends Enumerable.Implicits {

  case object Yes extends WithName("1") with TransportUnitsAddToListModel

  case object MoreToCome extends WithName("2") with TransportUnitsAddToListModel

  case object NoMoreToCome extends WithName("3") with TransportUnitsAddToListModel

  val values: Seq[TransportUnitsAddToListModel] = Seq(
    Yes, MoreToCome, NoMoreToCome
  )

  def options(implicit messages: Messages): Seq[RadioItem] = {
    def radioItem(value: TransportUnitsAddToListModel, index: Int): RadioItem = RadioItem(
      content = Text(messages(s"transportUnitsAddToList.${value.toString}")),
      value = Some(value.toString),
      id = Some(s"value_$index")
    )

    val orDivider = RadioItem(
      divider = Some(messages(s"site.divider"))
    )

    Seq(
      radioItem(Yes, 0),
      radioItem(NoMoreToCome, 1),
      orDivider,
      radioItem(MoreToCome, 2)
    )
  }

  implicit val enumerable: Enumerable[TransportUnitsAddToListModel] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
