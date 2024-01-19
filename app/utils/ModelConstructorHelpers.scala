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

package utils

import models.requests.DataRequest
import models.response.MissingMandatoryPage
import models.sections.ReviewAnswer.ChangeAnswers
import pages.{QuestionPage, ReviewPage}
import play.api.libs.json.Reads

trait ModelConstructorHelpers extends Logging {

  def mandatoryPage[A](page: QuestionPage[A])(implicit request: DataRequest[_], rds: Reads[A]): A = request.userAnswers.get(page) match {
    case Some(a) => a
    case None =>
      logger.error(s"Missing mandatory UserAnswer for page: '$page'")
      throw MissingMandatoryPage(s"Missing mandatory UserAnswer for page: '$page'")
  }

  def whenSectionChanged[A](reviewPage: ReviewPage)(f: => A)(implicit request: DataRequest[_]): Option[A] =
    Option.when(request.userAnswers.get(reviewPage).contains(ChangeAnswers))(f)
}
