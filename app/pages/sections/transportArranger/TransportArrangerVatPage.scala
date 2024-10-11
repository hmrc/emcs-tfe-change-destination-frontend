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

package pages.sections.transportArranger

import config.Constants.NONGBVAT
import models.VatNumberModel
import models.requests.DataRequest
import pages.QuestionPage
import play.api.libs.json.JsPath

case object TransportArrangerVatPage extends QuestionPage[VatNumberModel] {
  override val toString: String = "vat"
  override val path: JsPath = TransportArrangerSection.path \ toString

  override def getValueFromIE801(implicit request: DataRequest[_]): Option[VatNumberModel] =
    request.movementDetails.transportArrangerTrader.flatMap(_.vatNumber) match {
      case Some(number) if number != NONGBVAT && !number.isBlank =>
        Some(VatNumberModel(hasVatNumber = true, Some(number)))
      case _ =>
        Some(VatNumberModel(hasVatNumber = false, None))
    }
}
