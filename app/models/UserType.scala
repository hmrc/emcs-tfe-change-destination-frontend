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

sealed trait UserType {
  def isNorthernIrelandErn: Boolean
  def isGreatBritainErn: Boolean
}

object UserType {

  case object GreatBritainRegisteredConsignor extends UserType {
    override def isNorthernIrelandErn: Boolean = false

    override def isGreatBritainErn: Boolean = true
  }

  case object NorthernIrelandRegisteredConsignor extends UserType {
    override def isNorthernIrelandErn: Boolean = true

    override def isGreatBritainErn: Boolean = false
  }

  case object NorthernIrelandCertifiedConsignor extends UserType {
    override def isNorthernIrelandErn: Boolean = true

    override def isGreatBritainErn: Boolean = false
  }

  case object NorthernIrelandTemporaryCertifiedConsignor extends UserType {
    override def isNorthernIrelandErn: Boolean = true

    override def isGreatBritainErn: Boolean = false
  }

  case object GreatBritainWarehouseKeeper extends UserType {
    override def isNorthernIrelandErn: Boolean = false

    override def isGreatBritainErn: Boolean = true
  }

  case object NorthernIrelandWarehouseKeeper extends UserType {
    override def isNorthernIrelandErn: Boolean = true

    override def isGreatBritainErn: Boolean = false
  }

  case object GreatBritainWarehouse extends UserType {
    override def isNorthernIrelandErn: Boolean = false

    override def isGreatBritainErn: Boolean = true
  }

  case object NorthernIrelandWarehouse extends UserType {
    override def isNorthernIrelandErn: Boolean = true

    override def isGreatBritainErn: Boolean = false
  }

  case object Unknown extends UserType {
    override def isNorthernIrelandErn: Boolean = false

    override def isGreatBritainErn: Boolean = false
  }

  private val ERN_PREFIX_LENGTH = 4

  def apply(ern: String): UserType =
    ern.take(ERN_PREFIX_LENGTH).toUpperCase match {
      case "GBRC" => GreatBritainRegisteredConsignor
      case "XIRC" => NorthernIrelandRegisteredConsignor
      case "XIPA" => NorthernIrelandCertifiedConsignor
      case "XIPC" => NorthernIrelandTemporaryCertifiedConsignor
      case "GBWK" => GreatBritainWarehouseKeeper
      case "XIWK" => NorthernIrelandWarehouseKeeper
      case "XI00" => NorthernIrelandWarehouse
      case "GB00" => GreatBritainWarehouse
      case _ => Unknown
    }
}
