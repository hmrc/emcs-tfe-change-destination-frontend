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

package models.audit

import models.WithName
import play.api.libs.json.{Json, Writes}

trait Auditable { _: WithName =>
  val auditDescription: String
}

object Auditable {
  def writes[A]: Writes[A] = Writes {
    case model: Auditable =>
      Json.obj(
        "code" -> model.toString,
        "description" -> model.auditDescription
      )
    case _ =>
      throw new IllegalArgumentException("Cannot write Auditable model for class that does not extend Auditable trait")
  }
}
