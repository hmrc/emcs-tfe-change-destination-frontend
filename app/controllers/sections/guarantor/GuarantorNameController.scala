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

package controllers.sections.guarantor

import controllers.actions._
import forms.sections.guarantor.GuarantorNameFormProvider
import models.Mode
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import navigation.GuarantorNavigator
import pages.sections.guarantor.GuarantorNamePage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.UserAnswersService
import views.html.sections.guarantor.GuarantorNameView

import javax.inject.Inject
import scala.concurrent.Future

class GuarantorNameController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         override val userAnswersService: UserAnswersService,
                                         override val navigator: GuarantorNavigator,
                                         override val auth: AuthAction,
                                         override val getData: DataRetrievalAction,
                                         override val requireData: DataRequiredAction,
                                         override val withMovement: MovementAction,
                                         formProvider: GuarantorNameFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: GuarantorNameView
                                       ) extends GuarantorBaseController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovement(ern, arc) { implicit request =>
      withGuarantorArrangerAnswer { guarantorArranger =>
        renderView(Ok, fillForm(GuarantorNamePage, formProvider()), guarantorArranger, mode)
      }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      withGuarantorArrangerAnswer { guarantorArranger =>
        formProvider().bindFromRequest().fold(
          formWithErrors =>
            Future.successful(renderView(BadRequest, formWithErrors, guarantorArranger, mode)),
          value =>
            saveAndRedirect(GuarantorNamePage, value, mode)
        )
      }
    }

  private def renderView(status: Status, form: Form[_], guarantorArranger: GuarantorArranger, mode: Mode)(implicit request: DataRequest[_]): Result = {
    status(view(
      form = form,
      guarantorArranger = guarantorArranger,
      mode = mode
    ))
  }
}
