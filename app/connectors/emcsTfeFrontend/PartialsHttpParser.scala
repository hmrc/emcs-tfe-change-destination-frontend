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

package connectors.emcsTfeFrontend

import play.api.http.Status.OK
import play.api.libs.json.Json
import uk.gov.hmrc.govukfrontend.views.viewmodels.servicenavigation.ServiceNavigationItem
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.Logging

trait PartialsHttpParser extends Logging {

  implicit object PartialReads extends HttpReads[Option[Seq[ServiceNavigationItem]]] {
    override def read(method: String, url: String, response: HttpResponse): Option[Seq[ServiceNavigationItem]] = {
      response.status match {
        case OK => Some(Json.parse(response.body).as[Seq[ServiceNavigationItem]])
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe-frontend: $status")
          None
      }
    }
  }
}
