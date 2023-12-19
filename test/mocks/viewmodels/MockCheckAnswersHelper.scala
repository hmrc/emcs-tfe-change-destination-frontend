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

package mocks.viewmodels

import org.scalamock.handlers.CallHandler1
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.helpers.CheckAnswersHelper

trait MockCheckAnswersHelper extends MockFactory {

  lazy val mockCheckAnswersHelper: CheckAnswersHelper = mock[CheckAnswersHelper]

  object MockCheckAnswersHelper {

    def summaryList(summaryRows: Seq[SummaryListRow]): CallHandler1[Seq[SummaryListRow], SummaryList] =
      (mockCheckAnswersHelper.summaryList(_: Seq[SummaryListRow])).expects(summaryRows)
  }
}
