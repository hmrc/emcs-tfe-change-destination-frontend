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

package pages.sections.firstTransporter

import config.Constants.NONGBVAT
import models.VatNumberModel
import models.requests.DataRequest
import models.response.emcsTfe.TraderModel
import pages.QuestionPage
import play.api.libs.json.JsPath

case object FirstTransporterVatPage extends QuestionPage[VatNumberModel] {
  override val toString: String = "firstTransporterVat"
  override val path: JsPath = FirstTransporterSection.path \ toString

  override def getValueFromIE801(implicit request: DataRequest[_]): Option[VatNumberModel] =
    request.movementDetails.firstTransporterTrader.map {
      case TraderModel(_, _, _, Some(vatNumber), _) if vatNumber != NONGBVAT =>
        VatNumberModel(hasVatNumber = true, Some(vatNumber))
      case _ =>
        VatNumberModel(hasVatNumber = false, None)
    }
}
