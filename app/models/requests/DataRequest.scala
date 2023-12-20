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

import models._
import models.sections.info.DispatchPlace
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import pages.sections.info.DispatchPlacePage
import play.api.mvc.WrappedRequest
import utils.Logging

case class DataRequest[A](request: UserRequest[A],
                          arc: String,
                          userAnswers: UserAnswers,
                          traderKnownFacts: TraderKnownFacts) extends WrappedRequest[A](request) with Logging {

  lazy val internalId: String = request.internalId
  lazy val ern: String = request.ern

  lazy val isNorthernIrelandErn: Boolean = request.isNorthernIrelandErn

  lazy val userTypeFromErn: UserType = request.userTypeFromErn

  lazy val isWarehouseKeeper: Boolean = request.isWarehouseKeeper
  lazy val isRegisteredConsignor: Boolean = request.isRegisteredConsignor

  def dispatchPlace: Option[DispatchPlace] = userAnswers.get(DispatchPlacePage) match {
    case Some(dp) if dp == GreatBritain => Some(GreatBritain)
    case Some(dp) if dp == NorthernIreland => Some(NorthernIreland)
    case None if !isNorthernIrelandErn => Some(GreatBritain)
    case value =>
      logger.warn(s"[dispatchPlace] Invalid value for DISPATCH_PLACE: $value")
      None
  }
}
