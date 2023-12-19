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

import connectors.referenceData.GetWineOperationsConnector
import models.response.ReferenceDataException
import models.response.referenceData.WineOperations
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetWineOperationsService @Inject()(wineOperationsConnector: GetWineOperationsConnector)(implicit ec: ExecutionContext) {

  def getWineOperations()(implicit hc: HeaderCarrier): Future[Seq[WineOperations]] = {
    wineOperationsConnector.getWineOperations().map {
      case Left(_) => throw ReferenceDataException("Invalid response from wine operations endpoint")
      case Right(wineOperations) => wineOperations
    }
  }


}
