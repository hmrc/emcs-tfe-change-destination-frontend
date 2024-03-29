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

package pages.sections.transportUnit

import models.Index
import models.requests.DataRequest
import models.sections.transportUnit.TransportSealTypeModel
import pages.QuestionPage
import play.api.libs.json.JsPath
import queries.TransportUnitsCount

import scala.util.Try

case class TransportSealTypePage(idx: Index) extends QuestionPage[TransportSealTypeModel] {
  override val toString: String = "transportSealType"
  override val path: JsPath = TransportUnitSection(idx).path \ toString

  override def getValueFromIE801(implicit request: DataRequest[_]): Option[TransportSealTypeModel] =
    ifIndexIsValid(TransportUnitsCount, idx)(valueIfIndexIsValid = Try {
      val transportDetails = request.movementDetails.transportDetails(idx.position)
      transportDetails.commercialSealIdentification.map {
        identification =>
          TransportSealTypeModel(identification, transportDetails.sealInformation)
      }
    }.getOrElse(None)) // In case the number of transport units in user answers exceeds the number of TU's in 801 (return None as out of bounds)
}
