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

package controllers.action

import controllers.actions.MovementAction
import models.requests.{MovementRequest, UserRequest}
import models.response.emcsTfe.GetMovementResponse
import play.api.mvc.{ActionRefiner, Result}

import scala.concurrent.{ExecutionContext, Future}

class FakeMovementAction(movementData: GetMovementResponse) extends MovementAction {

  override def apply(arc: String, forceFetchNew: Boolean): ActionRefiner[UserRequest, MovementRequest] = new ActionRefiner[UserRequest, MovementRequest] {

    override def refine[A](request: UserRequest[A]): Future[Either[Result, MovementRequest[A]]] =
      Future.successful(Right(MovementRequest(request, arc, movementData)))

    override protected def executionContext: ExecutionContext =
      scala.concurrent.ExecutionContext.Implicits.global
  }
}
