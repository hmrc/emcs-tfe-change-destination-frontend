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

import models.requests.{MovementRequest, OptionalDataRequest}
import play.api.mvc.ActionTransformer
import services.{GetTraderKnownFactsService, PreDraftService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PreDraftDataRetrievalActionImpl @Inject()(val preDraftService: PreDraftService,
                                                val getTraderKnownFactsService: GetTraderKnownFactsService)
                                               (implicit val ec: ExecutionContext) extends PreDraftDataRetrievalAction {

  def apply(): ActionTransformer[MovementRequest, OptionalDataRequest] = new ActionTransformer[MovementRequest, OptionalDataRequest] {

    override val executionContext = ec

    override protected def transform[A](request: MovementRequest[A]): Future[OptionalDataRequest[A]] = {

      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      for {
        userAnswers <- preDraftService.get(request.ern, request.arc)
        traderKnownFacts <- getTraderKnownFactsService.getTraderKnownFacts(request.ern)
      } yield {
        OptionalDataRequest(request, userAnswers, traderKnownFacts)
      }
    }
  }
}

trait PreDraftDataRetrievalAction {
  def apply(): ActionTransformer[MovementRequest, OptionalDataRequest]
}
