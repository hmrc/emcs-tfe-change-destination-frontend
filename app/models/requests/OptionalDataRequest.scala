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

import models.{TraderKnownFacts, UserAnswers}
import play.api.mvc.WrappedRequest
import play.twirl.api.Html

case class OptionalDataRequest[A](request: MovementRequest[A],
                                  userAnswers: Option[UserAnswers],
                                  traderKnownFacts: Option[TraderKnownFacts]) extends WrappedRequest[A](request) with NavBarRequest {
  val internalId = request.internalId
  val ern = request.ern
  val arc = request.arc

  lazy val hasMultipleErns: Boolean = request.request.hasMultipleErns

  override val navBar: Option[Html] = request.navBar
}