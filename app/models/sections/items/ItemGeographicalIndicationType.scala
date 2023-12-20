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
import uk.gov.hmrc.govukfrontend.views.Aliases.{RadioItem, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint

sealed trait ItemGeographicalIndicationType

case object ItemGeographicalIndicationType extends Enumerable.Implicits {

  case object ProtectedDesignationOfOrigin extends WithName("PDO") with ItemGeographicalIndicationType

  case object ProtectedGeographicalIndication extends WithName("PGI") with ItemGeographicalIndicationType

  case object GeographicalIndication extends WithName("GI") with ItemGeographicalIndicationType

  case object NoGeographicalIndication extends WithName("None") with ItemGeographicalIndicationType

  val values: Seq[ItemGeographicalIndicationType] = Seq(
    ProtectedDesignationOfOrigin, ProtectedGeographicalIndication, GeographicalIndication, NoGeographicalIndication
  )

  def options(implicit messages: Messages): Seq[RadioItem] = {
    def radioItem(value: ItemGeographicalIndicationType, hasHint: Boolean): RadioItem = RadioItem(
      content = Text(messages(s"itemGeographicalIndicationChoice.${value.toString}")),
      value = Some(value.toString),
      hint = if (hasHint) Some(Hint(content = Text(messages(s"itemGeographicalIndicationChoice.${value.toString}.hint")))) else None,
      id = Some(s"value_$value")
    )

    val orDivider = RadioItem(
      divider = Some(messages(s"site.divider"))
    )

    Seq(
      radioItem(ProtectedDesignationOfOrigin, hasHint = true),
      radioItem(ProtectedGeographicalIndication, hasHint = true),
      radioItem(GeographicalIndication, hasHint = true),
      orDivider,
      radioItem(NoGeographicalIndication, hasHint = false)
    )
  }

  implicit val enumerable: Enumerable[ItemGeographicalIndicationType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
