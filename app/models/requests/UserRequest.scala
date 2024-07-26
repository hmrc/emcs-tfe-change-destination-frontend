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

package models.requests

import models.UserType
import models.UserType.{GreatBritainRegisteredConsignor, GreatBritainWarehouseKeeper, NorthernIrelandCertifiedConsignor, NorthernIrelandRegisteredConsignor, NorthernIrelandTemporaryCertifiedConsignor, NorthernIrelandWarehouseKeeper}
import play.api.mvc.{Request, WrappedRequest}
import play.twirl.api.Html

case class UserRequest[A](request: Request[A],
                          ern: String,
                          internalId: String,
                          credId: String,
                          sessionId: String,
                          hasMultipleErns: Boolean,
                          override val navBar: Option[Html] = None) extends WrappedRequest[A](request) with NavBarRequest {


  lazy val userTypeFromErn: UserType = UserType(ern)

  lazy val isWarehouseKeeper: Boolean = (userTypeFromErn == GreatBritainWarehouseKeeper) || (userTypeFromErn == NorthernIrelandWarehouseKeeper)
  lazy val isRegisteredConsignor: Boolean = (userTypeFromErn == GreatBritainRegisteredConsignor) || (userTypeFromErn == NorthernIrelandRegisteredConsignor)
  lazy val isCertifiedConsignor: Boolean = (userTypeFromErn == NorthernIrelandCertifiedConsignor) || (userTypeFromErn == NorthernIrelandTemporaryCertifiedConsignor)
}
