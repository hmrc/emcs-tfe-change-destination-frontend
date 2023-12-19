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

package models

sealed trait UserType

case object GreatBritainRegisteredConsignor extends UserType

case object NorthernIrelandRegisteredConsignor extends UserType

case object GreatBritainWarehouseKeeper extends UserType

case object NorthernIrelandWarehouseKeeper extends UserType

case object GreatBritainWarehouse extends UserType

case object NorthernIrelandWarehouse extends UserType

case object Unknown extends UserType

object UserType {

  private val ERN_PREFIX_LENGTH = 4

  def apply(ern: String): UserType = ern.take(ERN_PREFIX_LENGTH).toUpperCase match {
    case "GBRC" => GreatBritainRegisteredConsignor
    case "XIRC" => NorthernIrelandRegisteredConsignor
    case "GBWK" => GreatBritainWarehouseKeeper
    case "XIWK" => NorthernIrelandWarehouseKeeper
    case "XI00" => NorthernIrelandWarehouse
    case "GB00" => GreatBritainWarehouse
    case _ => Unknown
  }
}
