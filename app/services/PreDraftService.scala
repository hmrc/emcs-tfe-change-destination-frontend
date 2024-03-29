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

package services

import models.UserAnswers
import repositories.SessionRepository

import javax.inject.Inject
import scala.concurrent.Future

class PreDraftService @Inject()(sessionRepository: SessionRepository) {

  def get(ern: String, arc: String): Future[Option[UserAnswers]] =
    sessionRepository.get(ern, arc)

  def set(answers: UserAnswers): Future[Boolean] = {
    sessionRepository.set(answers)
  }

  def clear(ern: String, arc: String): Future[Boolean] =
    sessionRepository.clear(ern, arc)
}
