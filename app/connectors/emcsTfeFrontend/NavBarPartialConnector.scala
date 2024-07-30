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

import config.AppConfig
import play.twirl.api.Html
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.Logging

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NavBarPartialConnector @Inject()(val http: HttpClient,
                                       config: AppConfig) extends PartialsHttpParser with Logging {

  def getNavBar(exciseRegistrationNumber: String)
               (implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Option[Html]] =
    http.GET(s"${config.emcsTfeFrontendBaseUrl}/emcs/partials/navigation/trader/$exciseRegistrationNumber").recover { _ =>
      logger.warn(s"[getNavBar] Failed to retrieve nav bar for ERN: $exciseRegistrationNumber")
      None
    }
}
