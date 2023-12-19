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

package controllers

import models._
import models.requests.DataRequest
import pages.QuestionPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.{Format, Reads}
import play.api.mvc.{Call, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Logging

import scala.concurrent.{ExecutionContext, Future}

trait BaseController extends FrontendBaseController with I18nSupport with Enumerable.Implicits with Logging {

  implicit lazy val ec: ExecutionContext = controllerComponents.executionContext

  def fillForm[A](page: QuestionPage[A], form: Form[A])
                 (implicit request: DataRequest[_], format: Format[A]): Form[A] =
    request.userAnswers.get(page).fold(form)(form.fill)

  def withAnswer[A](
                     page: QuestionPage[A],
                     redirectRoute: Call = routes.JourneyRecoveryController.onPageLoad()
                   )(f: A => Result)(implicit request: DataRequest[_], rds: Reads[A]): Result =
    request.userAnswers.get(page) match {
      case Some(value) => f(value)
      case None =>
        logger.warn(s"[withAnswerAsync] Could not retrieved required answer for page: $page")
        Redirect(redirectRoute)
    }

  def withAnswerAsync[A](
                          page: QuestionPage[A],
                          redirectRoute: Call = routes.JourneyRecoveryController.onPageLoad()
                        )(f: A => Future[Result])(implicit request: DataRequest[_], rds: Reads[A]): Future[Result] =
    request.userAnswers.get(page) match {
      case Some(value) => f(value)
      case None =>
        logger.warn(s"[withAnswerAsync] Could not retrieved required answer for page: $page")
        Future.successful(Redirect(redirectRoute))
    }
}
