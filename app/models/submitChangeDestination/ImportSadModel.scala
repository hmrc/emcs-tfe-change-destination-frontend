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

package models.submitChangeDestination

import models.Index
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import pages.sections.sad.ImportNumberPage
import play.api.libs.json.{Json, OFormat}
import queries.SadCount
import utils.{Logging, ModelConstructorHelpers}

case class ImportSadModel(importSadNumber: String)

object ImportSadModel extends ModelConstructorHelpers with Logging {

  def apply(implicit request: DataRequest[_]): Seq[ImportSadModel] = {
    request.userAnswers.get(SadCount) match {
      case Some(0) | None =>
        logger.error("SadSection should contain at least one item")
        throw MissingMandatoryPage("SadSection should contain at least one item")
      case Some(value) =>
        (0 until value)
          .map(Index(_))
          .map {
            idx =>
              ImportSadModel(mandatoryPage(ImportNumberPage(idx)))
          }
    }
  }

  implicit val fmt: OFormat[ImportSadModel] = Json.format
}
