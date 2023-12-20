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

package mocks.services

import models.requests.CnCodeInformationItem
import models.response.referenceData.CnCodeInformation
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import services.GetCnCodeInformationService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockGetCnCodeInformationService extends MockFactory {

  lazy val mockGetCnCodeInformationService: GetCnCodeInformationService = mock[GetCnCodeInformationService]

  object MockGetCnCodeInformationService {

    type Output[A] = CallHandler2[Seq[A], HeaderCarrier, Future[Seq[(A, CnCodeInformation)]]]

    def getCnCodeInformationWithMovementItems(items: Seq[CnCodeInformationItem]): Output[CnCodeInformationItem] =
      (mockGetCnCodeInformationService.getCnCodeInformation(_: Seq[CnCodeInformationItem])(_: HeaderCarrier))
        .expects(where {
          (_items: Seq[CnCodeInformationItem], _) =>
            (_items.map(_.cnCode) == items.map(_.cnCode)) && (_items.map(_.productCode) == items.map(_.productCode))
        })
  }
}
