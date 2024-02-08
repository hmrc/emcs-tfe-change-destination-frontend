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

package controllers.actions.predraft

import controllers.actions.{AuthAction, BetaAllowListAction, MovementAction}
import models.requests.{DataRequest, MovementRequest, UserRequest}
import play.api.mvc._

import scala.concurrent.Future

trait PreDraftAuthActionHelper {

  val auth: AuthAction
  val getPreDraftData: PreDraftDataRetrievalAction
  val requirePreDraftData: PreDraftDataRequiredAction
  val betaAllowList: BetaAllowListAction
  val withMovement: MovementAction

  private def authorised(ern: String,
                         arc: String,
                         movementRefiner: => ActionRefiner[UserRequest, MovementRequest]): ActionBuilder[MovementRequest, AnyContent] =
    auth(ern, arc) andThen betaAllowList andThen movementRefiner

  private def authedUpToDate(ern: String, arc: String) =
    authorised(ern, arc, withMovement.upToDate(arc))

  private def authorisedWithPreDraftDataUpToDateMovement(ern: String, arc: String): ActionBuilder[DataRequest, AnyContent] =
    authedUpToDate(ern, arc) andThen getPreDraftData() andThen requirePreDraftData

  def authorisedWithPreDraftDataUpToDateMovementAsync(ern: String, arc: String)(block: DataRequest[_] => Future[Result]): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovement(ern, arc).async(block)

  private def authedCache(ern: String, arc: String) =
    authorised(ern, arc, withMovement.fromCache(arc))

  private def authorisedWithPreDraftDataCachedMovement(ern: String, arc: String): ActionBuilder[DataRequest, AnyContent] =
    authedCache(ern, arc) andThen getPreDraftData() andThen requirePreDraftData

  def authorisedWithPreDraftDataCachedMovementAsync(ern: String, arc: String)(block: DataRequest[_] => Future[Result]): Action[AnyContent] =
    authorisedWithPreDraftDataUpToDateMovement(ern, arc).async(block)

}
