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

import controllers.BaseNavigationController
import models.requests.DataRequest
import models.sections.guarantor.GuarantorArranger
import models.sections.guarantor.GuarantorArranger.{GoodsOwner, Transporter}
import pages.sections.guarantor.GuarantorArrangerPage
import play.api.mvc.Result

import scala.concurrent.Future

trait GuarantorBaseController extends BaseNavigationController {

  def withGuarantorArrangerAnswer(f: GuarantorArranger => Result)(implicit request: DataRequest[_]): Result = {
    withAnswer(
      page = GuarantorArrangerPage,
      redirectRoute = controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(request.ern, request.arc)
    ) {
      case guarantorArranger if guarantorArranger == GoodsOwner | guarantorArranger == Transporter =>
        f(guarantorArranger)
      case guarantorArranger =>
        logger.warn(s"[withGuarantorArrangerAnswer] Invalid answer of $guarantorArranger for this controller/page")
        Redirect(controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(request.ern, request.arc))
    }
  }


  def withGuarantorArrangerAnswer(f: GuarantorArranger => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    withAnswerAsync(
      page = GuarantorArrangerPage,
      redirectRoute = controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(request.ern, request.arc)
    ) {
      case guarantorArranger if guarantorArranger == GoodsOwner | guarantorArranger == Transporter =>
        f(guarantorArranger)
      case guarantorArranger =>
        logger.warn(s"[withGuarantorArrangerAnswer] Invalid answer of $guarantorArranger for this controller/page")
        Future.successful(Redirect(controllers.sections.guarantor.routes.GuarantorCheckAnswersController.onPageLoad(request.ern, request.arc)))
    }
  }

}
