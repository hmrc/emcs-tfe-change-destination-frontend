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

package pages.sections.info

import models.requests.DataRequest
import models.sections.info.DispatchPlace
import pages.QuestionPage
import play.api.libs.json.JsPath

case object DispatchPlacePage extends QuestionPage[DispatchPlace] {
  override val toString: String = "dispatchPlace"
  override val path: JsPath = InfoSection.path \ toString

  override def getValueFromIE801(implicit request: DataRequest[_]): Option[DispatchPlace] = {
    val placeIdentifierChars = 2

    request.movementDetails.competentAuthorityDispatchOfficeReferenceNumber.map(_.take(placeIdentifierChars)) match {
      case Some(DispatchPlace.GreatBritain.toString) => Some(DispatchPlace.GreatBritain)
      case Some(DispatchPlace.NorthernIreland.toString) => Some(DispatchPlace.NorthernIreland)
      case _ => None
    }
  }
}
