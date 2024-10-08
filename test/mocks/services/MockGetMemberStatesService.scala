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

package mocks.services

import models.CountryModel
import org.scalamock.handlers.CallHandler1
import org.scalamock.scalatest.MockFactory
import services.GetMemberStatesService
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockGetMemberStatesService extends MockFactory {

  lazy val mockGetMemberStatesService: GetMemberStatesService = mock[GetMemberStatesService]

  object MockGetMemberStatesService {

    def getMemberStatesSelectItems(): CallHandler1[HeaderCarrier, Future[Seq[SelectItem]]] =
      (mockGetMemberStatesService.getMemberStatesSelectItems()(_: HeaderCarrier)).expects(*)

    def getMemberStates(): CallHandler1[HeaderCarrier, Future[Seq[CountryModel]]] =
      (mockGetMemberStatesService.getMemberStates()(_: HeaderCarrier)).expects(*)

    def getEuMemberStates(): CallHandler1[HeaderCarrier, Future[Seq[CountryModel]]] =
      (mockGetMemberStatesService.getEuMemberStates()(_: HeaderCarrier)).expects(*)
  }
}
