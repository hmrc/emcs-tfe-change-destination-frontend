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

package pages.sections.journeyType

import models.requests.DataRequest
import models.sections.journeyType.JourneyTime
import pages.QuestionPage
import play.api.libs.json.{JsError, JsPath, JsResultException, JsString, JsSuccess}

case object JourneyTimeHoursPage extends QuestionPage[Int] {
  override val toString: String = "journeyTimeHours"
  override val path: JsPath = JourneyTypeSection.path \ toString

  override def getValueFromIE801(implicit request: DataRequest[_]): Option[Int] =
    JourneyTime.reads.reads(JsString(request.movementDetails.journeyTime)) match {
      case JsSuccess(JourneyTime.Days(_), _) => None
      case JsSuccess(JourneyTime.Hours(time), _) => Some(time.toInt)
      case JsError(errors) => throw JsResultException(errors)
    }
}
