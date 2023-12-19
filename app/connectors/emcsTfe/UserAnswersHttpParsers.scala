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

import connectors.BaseConnectorUtils
import models.UserAnswers
import models.response.{BadRequestError, ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status.{BAD_REQUEST, NO_CONTENT, OK}
import uk.gov.hmrc.http.{HttpClient, HttpReads, HttpResponse}

trait UserAnswersHttpParsers extends BaseConnectorUtils[UserAnswers] {

  def http: HttpClient

  object GetUserAnswersReads extends HttpReads[Either[ErrorResponse, Option[UserAnswers]]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, Option[UserAnswers]] =
      response.status match {
        case NO_CONTENT => Right(None)
        case OK => response.validateJson match {
          case Some(valid) => Right(Some(valid))
          case None =>
            logger.warn(s"[read] Bad JSON response from emcs-tfe")
            Left(JsonValidationError)
        }
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe: $status")
          Left(UnexpectedDownstreamResponseError)
      }
  }

  object PutUserAnswersReads extends HttpReads[Either[ErrorResponse, UserAnswers]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, UserAnswers] =
      response.status match {
        case OK => response.validateJson match {
          case Some(valid) => Right(valid)
          case None =>
            logger.warn(s"[read] Bad JSON response from emcs-tfe")
            Left(JsonValidationError)
        }
        case BAD_REQUEST =>
          logger.error(s"[read] Bad Request response received from emcs-tfe")
          Left(BadRequestError(response.body))
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe: $status")
          Left(UnexpectedDownstreamResponseError)
      }
  }

  object DeleteUserAnswersReads extends HttpReads[Either[ErrorResponse, Boolean]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, Boolean] =
      response.status match {
        case NO_CONTENT => Right(true)
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe: $status")
          Left(UnexpectedDownstreamResponseError)
      }
  }
}
