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

package controllers.sections.importInformation

import controllers.BaseController
import controllers.actions._
import models.NormalMode
import pages.sections.importInformation.ImportInformationSection
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}

import javax.inject.Inject

class ImportInformationIndexController @Inject()(
                                                  override val auth: AuthAction,
                                                  override val getData: DataRetrievalAction,
                                                  override val requireData: DataRequiredAction,
                                                  override val userAllowList: UserAllowListAction,
                                                  val controllerComponents: MessagesControllerComponents
                                                ) extends BaseController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequest(ern, arc) { implicit request =>
      if (ImportInformationSection.isCompleted) {
        Redirect(controllers.sections.importInformation.routes.CheckYourAnswersImportController.onPageLoad(ern, arc))
      } else {
        Redirect(controllers.sections.importInformation.routes.ImportCustomsOfficeCodeController.onPageLoad(ern, arc, NormalMode))
      }
    }

}
