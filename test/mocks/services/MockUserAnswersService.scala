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

package mocks.services

import models.UserAnswers
import org.scalamock.handlers.{CallHandler2, CallHandler3}
import org.scalamock.scalatest.MockFactory
import services.UserAnswersService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockUserAnswersService extends MockFactory {

  lazy val mockUserAnswersService: UserAnswersService = mock[UserAnswersService]

  object MockUserAnswersService {

    def get(ern: String, arc: String): CallHandler3[String, String, HeaderCarrier, Future[Option[UserAnswers]]] =
      (mockUserAnswersService.get(_: String, _: String)(_: HeaderCarrier))
        .expects(ern, arc, *)

    def set(userAnswers: UserAnswers): CallHandler2[UserAnswers, HeaderCarrier, Future[UserAnswers]] =
      (mockUserAnswersService.set(_: UserAnswers)(_: HeaderCarrier))
        .expects(where {
          (answersBeingSet: UserAnswers, _: HeaderCarrier) => answersBeingSet.data == userAnswers.data
        })

    def set(): CallHandler2[UserAnswers, HeaderCarrier, Future[UserAnswers]] =
      (mockUserAnswersService.set(_: UserAnswers)(_: HeaderCarrier))
        .expects(*, *)

    def clear(userAnswers: UserAnswers): CallHandler2[UserAnswers, HeaderCarrier, Future[Boolean]] =
      (mockUserAnswersService.clear(_: UserAnswers)(_: HeaderCarrier))
        .expects(userAnswers, *)
  }
}
