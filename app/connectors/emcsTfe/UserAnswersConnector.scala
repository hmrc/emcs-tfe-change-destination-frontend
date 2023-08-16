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

package connectors.emcsTfe

import config.AppConfig
import models.{ErrorResponse, UserAnswers}
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersConnector @Inject()(val http: HttpClient,
                                     config: AppConfig) extends UserAnswersHttpParsers {

  override implicit val reads: Reads[UserAnswers] = UserAnswers.format

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def get(ern: String, arc: String)
         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Option[UserAnswers]]] =
    http.GET[Either[ErrorResponse, Option[UserAnswers]]](
      url = s"$baseUrl/user-answers/change-destination/$ern/$arc"
    )(GetUserAnswersReads, hc, ec)

  def put(userAnswers: UserAnswers)
         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, UserAnswers]] =
    http.PUT[UserAnswers, Either[ErrorResponse, UserAnswers]](
      url = s"$baseUrl/user-answers/change-destination/${userAnswers.ern}/${userAnswers.arc}",
      body = userAnswers
    )(UserAnswers.writes, PutUserAnswersReads, hc, ec)

  def delete(ern: String, arc: String)
            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] =
    http.DELETE[Either[ErrorResponse, Boolean]](
      url = s"$baseUrl/user-answers/change-destination/$ern/$arc"
    )(DeleteUserAnswersReads, hc, ec)

}
