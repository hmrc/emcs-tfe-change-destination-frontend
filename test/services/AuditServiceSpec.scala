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

import base.SpecBase
import models.audit.AuditModel
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.{JsValue, Json, Writes}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext

class AuditServiceSpec extends SpecBase with MockFactory {

  implicit val hc = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  val auditConnector: AuditConnector = mock[AuditConnector]
  val auditingService = new AuditingService(auditConnector)

  "The AuditService should" - {

    "audit an explicit event" in {

      val auditModel = new AuditModel {
        override val auditType: String = "submitSomething"
        override val detail: JsValue = Json.obj("detail" -> "some details")
      }

      (auditConnector.sendExplicitAudit(_: String, _: JsValue)(_: HeaderCarrier, _: ExecutionContext, _: Writes[JsValue]))
        .expects(auditModel.auditType, auditModel.detail, *, *, *)
        .returns(())

      auditingService.audit(auditModel)
    }
  }

}
