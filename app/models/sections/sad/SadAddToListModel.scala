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

package models.sections.sad

import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait SadAddToListModel

object SadAddToListModel extends Enumerable.Implicits {

  case object Yes extends WithName("1") with SadAddToListModel

  case object NoMoreToCome extends WithName("2") with SadAddToListModel

  val values: Seq[SadAddToListModel] = Seq(
    Yes, NoMoreToCome
  )

  def options(implicit messages: Messages): Seq[RadioItem] = {
    def radioItem(value: SadAddToListModel, index: Int): RadioItem = RadioItem(
      content = Text(messages(s"sadAddToList.${value.toString}")),
      value = Some(value.toString),
      id = Some(s"value_$index")
    )

    Seq(
      radioItem(Yes, 0),
      radioItem(NoMoreToCome, 1)
    )
  }

  implicit val enumerable: Enumerable[SadAddToListModel] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
