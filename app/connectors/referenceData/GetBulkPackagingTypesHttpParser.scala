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

package connectors.referenceData

import connectors.BaseConnectorUtils
import models.response.referenceData.BulkPackagingType
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import models.sections.items.ItemBulkPackagingCode
import play.api.http.Status.OK
import play.api.libs.json.{Reads, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait GetBulkPackagingTypesHttpParser extends BaseConnectorUtils[Seq[BulkPackagingType]] {

  implicit val reads: Reads[Seq[BulkPackagingType]] = BulkPackagingType.seqReads
  def http: HttpClient

  class GetBulkPackagingTypesReads extends HttpReads[Either[ErrorResponse, Seq[BulkPackagingType]]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, Seq[BulkPackagingType]] = {
      response.status match {
        case OK =>
          response.validateJson match {
            case Some(valid: Seq[BulkPackagingType]) if valid.isDefinedAt(0) => Right(valid)
            case _ =>
              logger.warn(s"[read] Bad JSON response from emcs-tfe-reference-data")
              Left(JsonValidationError)
          }
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe-reference-data: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  def post(url: String, packagingCodes: Seq[ItemBulkPackagingCode])
          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Seq[BulkPackagingType]]] =
    http.POST[Seq[ItemBulkPackagingCode], Either[ErrorResponse, Seq[BulkPackagingType]]](
      url, packagingCodes
    )(Writes.seq(ItemBulkPackagingCode.writes), new GetBulkPackagingTypesReads, hc, ec)

}
