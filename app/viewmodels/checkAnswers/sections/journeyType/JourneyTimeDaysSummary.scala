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

package viewmodels.checkAnswers.sections.journeyType

import models.CheckMode
import models.requests.DataRequest
import pages.sections.journeyType.{JourneyTimeDaysPage, JourneyTimeHoursPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object JourneyTimeDaysSummary {

  def row(onReviewPage: Boolean)(implicit request: DataRequest[_], messages: Messages): Option[SummaryListRow] = {
    val answers = request.userAnswers
    val isHoursAnswerDefined = request.userAnswers.get(JourneyTimeHoursPage).isDefined
    val isDaysInLocalAnswers = answers.getFromUserAnswersOnly(JourneyTimeDaysPage).isDefined
    //Stops the CYA page returning an 801 value and the 'local' answer
    val optAnswer = if(onReviewPage || (!isHoursAnswerDefined && !isDaysInLocalAnswers)) JourneyTimeDaysPage.getValueFromIE801 else answers.getFromUserAnswersOnly(JourneyTimeDaysPage)
    optAnswer.map {
      answer =>
        SummaryListRowViewModel(
          key = "journeyTimeDays.checkYourAnswersLabel",
          value = ValueViewModel(messages("journeyTimeDays.checkYourAnswersValue", answer.toString)),
          actions = if (!onReviewPage) {Seq(
            ActionItemViewModel(
              "site.change",
              href = controllers.sections.journeyType.routes.JourneyTimeDaysController.onPageLoad(answers.ern, answers.arc, CheckMode).url,
              id = "journeyTimeDays"
            ).withVisuallyHiddenText(messages("journeyTimeDays.change.hidden"))
          )} else Seq()
        )
    }
  }
}
