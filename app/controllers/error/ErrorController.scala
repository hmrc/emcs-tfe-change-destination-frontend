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

package controllers.error

import config.AppConfig
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.auth.errors.{InactiveEnrolmentView, NoEnrolmentView, NotAnOrganisationView, NotOnPrivateBetaView, UnauthorisedView}

import javax.inject.Inject

class ErrorController @Inject()(
                                        val controllerComponents: MessagesControllerComponents,
                                        view: UnauthorisedView,
                                        notAnOrgView: NotAnOrganisationView,
                                        noEnrolmentView: NoEnrolmentView,
                                        inactiveEnrolmentView: InactiveEnrolmentView,
                                        notOnPrivateBetaView: NotOnPrivateBetaView
                                      )(implicit val config: AppConfig) extends FrontendBaseController with I18nSupport {

  def unauthorised(): Action[AnyContent] = Action { implicit request =>
    Ok(view())
  }

  def notAnOrganisation(): Action[AnyContent] = Action { implicit request =>
    Ok(notAnOrgView())
  }

  def noEnrolment(): Action[AnyContent] = Action { implicit request =>
    Ok(noEnrolmentView())
  }

  def inactiveEnrolment(): Action[AnyContent] = Action { implicit request =>
    Ok(inactiveEnrolmentView())
  }

  def notOnPrivateBeta(): Action[AnyContent] = Action { implicit request =>
    Ok(notOnPrivateBetaView())
  }

  def wrongArc(): Action[AnyContent] = Action { implicit request =>
    Ok(view())
  }
}
