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

package viewmodels.helpers

import models.UserType._
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import models.sections.info.DispatchPlace
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import models.sections.info.movementScenario.MovementScenario
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import utils.Logging

class DestinationTypeHelper extends Logging {

  def title(implicit request: DataRequest[_], messages: Messages): String = request.userTypeFromErn match {
    case GreatBritainWarehouseKeeper | NorthernIrelandWarehouseKeeper => messages("destinationType.title.movement")
    case GreatBritainRegisteredConsignor | NorthernIrelandRegisteredConsignor => messages("destinationType.title.import")
    case userType =>
      logger.error(s"[title] invalid UserType for CAM journey: $userType")
      throw InvalidUserTypeException(s"[DestinationTypeHelper][title] invalid UserType for CAM journey: $userType")
  }

  def heading(implicit request: DataRequest[_], messages: Messages): String = request.userTypeFromErn match {
    case GreatBritainWarehouseKeeper | NorthernIrelandWarehouseKeeper => messages("destinationType.heading.movement")
    case GreatBritainRegisteredConsignor | NorthernIrelandRegisteredConsignor => messages("destinationType.heading.import")
    case userType =>
      logger.error(s"[heading] invalid UserType for CAM journey: $userType")
      throw InvalidUserTypeException(s"[DestinationTypeHelper][heading] invalid UserType for CAM journey: $userType")
  }

  def options(dispatchPlace: DispatchPlace)(implicit request: DataRequest[_], messages: Messages): Seq[RadioItem] = {
    // Note: __RC can only do imports, __WK can only do exports
    request.userTypeFromErn match {
      case GreatBritainWarehouseKeeper | GreatBritainRegisteredConsignor => MovementScenario.valuesUk.map(radioOption)
      case NorthernIrelandWarehouseKeeper if dispatchPlace == GreatBritain => MovementScenario.valuesUk.map(radioOption)
      case NorthernIrelandWarehouseKeeper if dispatchPlace == NorthernIreland => MovementScenario.valuesEu.map(radioOption)
      case NorthernIrelandRegisteredConsignor => MovementScenario.valuesEu.map(radioOption)
      case userType =>
        logger.error(s"[options] invalid UserType for CAM journey: $userType")
        throw InvalidUserTypeException(s"[DestinationTypeHelper][options] invalid UserType for CAM journey: $userType")
    }
  }

  private[helpers] def radioOption(value: MovementScenario)(implicit messages: Messages): RadioItem = RadioItem(
    content = Text(messages(s"destinationType.${value.toString}")),
    value = Some(value.toString),
    id = Some(s"value_${value.toString}")
  )
}
