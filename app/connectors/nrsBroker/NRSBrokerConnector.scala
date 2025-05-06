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

package connectors.nrsBroker

import config.AppConfig
import models.nrs.NRSPayload
import models.response.nrsBroker.NRSBrokerInsertPayloadResponse
import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NRSBrokerConnector @Inject()(http: HttpClientV2, config: AppConfig) extends NRSBrokerHttpParser {

  def submitPayload(nrsPayload: NRSPayload, ern: String)
                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, NRSBrokerInsertPayloadResponse]] =
    http
      .put(url"${config.nrsBrokerBaseUrl()}/trader/$ern/nrs/submission")
      .withBody(Json.toJson(nrsPayload))
      .execute[Either[ErrorResponse, NRSBrokerInsertPayloadResponse]]
      .recover {
        error =>
          logger.warn(s"[submitPayload] Unexpected error from NRS broker:" +
            s" ${error.getClass} ${error.getMessage.take(10000)}")
          Left(UnexpectedDownstreamResponseError)
      }
}
