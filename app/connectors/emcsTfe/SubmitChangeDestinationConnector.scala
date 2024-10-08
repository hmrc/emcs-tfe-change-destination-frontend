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
import models.requests.DataRequest
import models.response.{ErrorResponse, SubmitChangeDestinationResponse}
import models.submitChangeDestination.SubmitChangeDestinationModel
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitChangeDestinationConnector @Inject()(val http: HttpClient,
                                              config: AppConfig) extends EmcsTfeHttpParser[SubmitChangeDestinationResponse] {

  override implicit val reads: Reads[SubmitChangeDestinationResponse] = SubmitChangeDestinationResponse.reads

  lazy val baseUrl: String = config.emcsTfeBaseUrl
  def submit(submitChangeDestinationModel: SubmitChangeDestinationModel)
            (implicit request: DataRequest[_], hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, SubmitChangeDestinationResponse]] = {
    logger.debug(s"[submit][${request.ern}][${request.arc}] Submitting body: ${Json.toJson(submitChangeDestinationModel)}")
    post(s"$baseUrl/change-destination/${request.ern}/${request.arc}", submitChangeDestinationModel)
  }

}
