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

package controllers.actions

import models.requests.{DataRequest, MovementRequest, UserRequest}
import play.api.mvc.{Action, ActionRefiner, AnyContent, Result}

import scala.concurrent.Future

trait AuthActionHelper {

  val auth: AuthAction
  val withMovement: MovementAction
  val getData: DataRetrievalAction
  val requireData: DataRequiredAction
  val userAllowList: UserAllowListAction

  private def authedDataRequest(ern: String, arc: String, movementRefiner: => ActionRefiner[UserRequest, MovementRequest]) =
    auth(ern, arc) andThen userAllowList andThen movementRefiner andThen getData andThen requireData


  private def authedDataRequestUpToDate(ern: String, arc: String) = authedDataRequest(ern, arc, withMovement.upToDate(arc))

  def authorisedDataRequestWithUpToDateMovement(ern: String, arc: String)(block: DataRequest[_] => Result): Action[AnyContent] =
    authedDataRequestUpToDate(ern, arc)(block)

  def authorisedDataRequestWithUpToDateMovementAsync(ern: String, arc: String)(block: DataRequest[_] => Future[Result]): Action[AnyContent] =
    authedDataRequestUpToDate(ern, arc).async(block)


  private def authedDataRequestFromCache(ern: String, arc: String) = authedDataRequest(ern, arc, withMovement.fromCache(arc))

  def authorisedDataRequestWithCachedMovement(ern: String, arc: String)(block: DataRequest[_] => Result): Action[AnyContent] =
    authedDataRequestFromCache(ern, arc)(block)

  def authorisedDataRequestWithCachedMovementAsync(ern: String, arc: String)(block: DataRequest[_] => Future[Result]): Action[AnyContent] =
    authedDataRequestFromCache(ern, arc).async(block)

}
