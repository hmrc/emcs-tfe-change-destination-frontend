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

package pages.sections.consignee

import models.requests.DataRequest
import models.sections.info.ChangeType.Consignee
import pages.QuestionPage
import pages.sections.info.ChangeTypePage
import play.api.libs.json.JsPath

case object ConsigneeExcisePage extends QuestionPage[String] {
  override val toString: String = "exciseRegistrationNumber"
  override val path: JsPath = ConsigneeSection.path \ toString

  override def getValueFromIE801(implicit request: DataRequest[_]): Option[String] =
    request.userAnswers.get(ChangeTypePage) match {
      case Some(Consignee) => None
      case _ => request.movementDetails.consigneeTrader.flatMap(_.traderExciseNumber)
    }
}
