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

import models.Index
import models.requests.DataRequest
import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.helpers.ItemsPackagingAddToListHelper

trait MockItemsPackagingAddToListHelper extends MockFactory {

  lazy val mockItemsPackagingAddToListHelper: ItemsPackagingAddToListHelper = mock[ItemsPackagingAddToListHelper]

  object MockItemsPackagingAddToListHelper {

    def allPackagesSummary(idx: Index)(result: Seq[SummaryList] = Seq.empty): CallHandler3[Index, DataRequest[_], Messages, Seq[SummaryList]] =
      (mockItemsPackagingAddToListHelper.allPackagesSummary(_: Index)(_: DataRequest[_], _: Messages))
        .expects(idx, *, *)
        .returns(result)
  }

}
