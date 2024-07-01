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

package mocks.services

import models.audit.AuditModel
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import services.AuditingService
import uk.gov.hmrc.http.HeaderCarrier

trait MockAuditingService extends MockFactory with BeforeAndAfterEach {

  lazy val mockAuditingService: AuditingService = mock[AuditingService]

  object MockAuditingService {
    def audit(auditModel: AuditModel): CallHandler2[AuditModel, HeaderCarrier, Unit] =
      (mockAuditingService.audit(_: AuditModel)(_: HeaderCarrier))
        .expects(auditModel, *)
  }
}