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

package models.sections.info

import models.requests.DataRequest
import models.sections.info.movementScenario.DestinationType.{Export, dutyPaidDestinationTypes}
import models.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait ChangeType

object ChangeType extends Enumerable.Implicits {

  case object Consignee extends WithName("consignee") with ChangeType
  case object Destination extends WithName("destination") with ChangeType
  case object ExportOffice extends WithName("exportOffice") with ChangeType
  case object Return extends WithName("return") with ChangeType

  val allValues = Seq(Consignee, Destination, ExportOffice, Return)

  def values(implicit request: DataRequest[_]): Seq[ChangeType] = {
    val destinationType = request.movementDetails.destinationType
    val visibleOptions = Seq(
      if(destinationType.isDutyPaid) Some(Return) else Some(Consignee),
      if(destinationType == Export) Some(ExportOffice) else Some(Destination)
    ).flatten

    allValues.filter(visibleOptions.contains)
  }

  def options(implicit messages: Messages, request: DataRequest[_]): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, index) =>
      RadioItem(
        content = Text(messages(s"changeType.${value.toString}")),
        value   = Some(value.toString),
        id      = Some(s"value_$index")
      )
  }

  implicit val enumerable: Enumerable[ChangeType] =
    Enumerable(allValues.map(v => v.toString -> v): _*)
}
