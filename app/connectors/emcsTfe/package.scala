/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors

import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import play.api.LoggerLike

import scala.concurrent.{ExecutionContext, Future}

package object emcsTfe {

  def withExceptionRecovery[A](method: String)(f: => Future[Either[ErrorResponse, A]])
                              (implicit ec: ExecutionContext, logger: LoggerLike): Future[Either[ErrorResponse, A]] =
    f recover {
      case e: Throwable =>
        logger.warn(s"[$method] Unexpected exception of type ${e.getClass.getSimpleName} was thrown")
        Left(UnexpectedDownstreamResponseError)
    }
}
