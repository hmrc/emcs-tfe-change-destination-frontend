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

package mocks.connectors

import connectors.emcsTfe.UserAnswersConnector
import models.UserAnswers
import models.response.ErrorResponse
import org.scalamock.handlers.{CallHandler3, CallHandler4}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockUserAnswersConnector extends MockFactory {

  lazy val mockUserAnswersConnector: UserAnswersConnector = mock[UserAnswersConnector]

  object MockUserAnswersConnector {

    def get(ern: String, arc: String): CallHandler4[String, String, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, Option[UserAnswers]]]] =
      (mockUserAnswersConnector.get(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, arc, *, *)

    def put(userAnswers: UserAnswers): CallHandler3[UserAnswers, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, UserAnswers]]] =
      (mockUserAnswersConnector.put(_: UserAnswers)(_: HeaderCarrier, _: ExecutionContext))
        .expects(userAnswers, *, *)

    def delete(ern: String, arc: String): CallHandler4[String, String, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, Boolean]]] =
      (mockUserAnswersConnector.delete(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, arc, *, *)
  }
}
