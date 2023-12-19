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
import org.scalamock.handlers.{CallHandler1, CallHandler2}
import org.scalamock.scalatest.MockFactory
import services.PreDraftService

import scala.concurrent.Future

trait MockPreDraftService extends MockFactory {

  lazy val mockPreDraftService: PreDraftService = mock[PreDraftService]

  object MockPreDraftService {

    def get(ern: String, sessionId: String): CallHandler2[String, String, Future[Option[UserAnswers]]] =
      (mockPreDraftService.get(_: String, _: String))
        .expects(ern, sessionId)

    def set(userAnswers: UserAnswers): CallHandler1[UserAnswers, Future[Boolean]] =
      (mockPreDraftService.set(_: UserAnswers))
        .expects(where[UserAnswers](actualAnswers =>
          actualAnswers.ern == userAnswers.ern &&
            actualAnswers.data == userAnswers.data
        ))


    def clear(ern: String, sessionId: String): CallHandler2[String, String, Future[Boolean]] = {
      (mockPreDraftService.clear(_: String, _: String))
        .expects(ern, sessionId)
    }
  }
}
