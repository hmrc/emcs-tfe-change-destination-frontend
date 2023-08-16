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

import connectors.emcsTfe.GetMovementConnector
import handlers.ErrorHandler
import models.requests.{MovementRequest, UserRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MovementActionImpl @Inject()(getMovementConnector: GetMovementConnector,
                                   errorHandler: ErrorHandler)
                                  (implicit ec: ExecutionContext) extends MovementAction {

  override def apply(arc: String, forceFetchNew: Boolean): ActionRefiner[UserRequest, MovementRequest] = new ActionRefiner[UserRequest, MovementRequest] {

    override def executionContext: ExecutionContext = ec

    override def refine[A](request: UserRequest[A]): Future[Either[Result, MovementRequest[A]]] = {

      implicit val hc = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      getMovementConnector.getMovement(request.ern, arc, forceFetchNew).map {
        case Left(_) =>
          Left(Redirect(controllers.error.routes.ErrorController.wrongArc()))
        case Right(movementDetails) =>
          Right(MovementRequest(request, arc, movementDetails))
      }
    }
  }
}

trait MovementAction {
  def apply(arc: String, forceFetchNew: Boolean): ActionRefiner[UserRequest, MovementRequest]
  def upToDate(arc: String): ActionRefiner[UserRequest, MovementRequest] = apply(arc, forceFetchNew = true)
  def fromCache(arc: String): ActionRefiner[UserRequest, MovementRequest] = apply(arc, forceFetchNew = false)
}
