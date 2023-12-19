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

import connectors.emcsTfe.UserAnswersConnector
import models.UserAnswers
import models.response.UserAnswersException
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersService @Inject()(userAnswersConnector: UserAnswersConnector)(implicit ec: ExecutionContext) {

  def get(ern: String, arc: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] =
    userAnswersConnector.get(ern, arc).map {
      case Right(answers) => answers
      case Left(_) => throw UserAnswersException(s"Failed to retrieve UserAnswers from emcs-tfe for ern: '$ern' & arc: '$arc'")
    }

  def set(answers: UserAnswers)(implicit hc: HeaderCarrier): Future[UserAnswers] = {
    userAnswersConnector.put(answers).map {
      case Right(answers) => answers
      case Left(_) => throw UserAnswersException(s"Failed to store UserAnswers in emcs-tfe for ern: '${answers.ern}' & arc: '${answers.arc}'")
    }
  }

  def clear(answers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] =
    userAnswersConnector.delete(answers.ern, answers.arc).map {
      case Right(response) => response
      case Left(_) => throw UserAnswersException(s"Failed to delete UserAnswers from emcs-tfe for ern: '${answers.ern}' & arc: '${answers.arc}'")
    }
}
