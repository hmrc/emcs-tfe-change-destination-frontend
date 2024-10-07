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

package services

import config.Constants.{GB_PREFIX, NI_PREFIX}
import connectors.referenceData.GetMemberStatesConnector
import models.CountryModel
import models.response.MemberStatesException
import uk.gov.hmrc.govukfrontend.views.Aliases.SelectItem
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMemberStatesService @Inject()(connector: GetMemberStatesConnector)
                                      (implicit ec: ExecutionContext) {


  def getMemberStatesSelectItems()(implicit hc: HeaderCarrier): Future[Seq[SelectItem]] =
    getMemberStates().map(_.map { memberState =>
      SelectItem(
        value = Some(memberState.countryCode),
        text = s"${memberState.country} (${memberState.countryCode})"
      )
    })

  def getMemberStates()(implicit hc: HeaderCarrier): Future[Seq[CountryModel]] =
    connector.getMemberStates().map {
      case Left(_) => throw MemberStatesException("No member states retrieved")
      case Right(value) => value
    }

  def getEuMemberStates()(implicit hc: HeaderCarrier): Future[Seq[CountryModel]] =
    getMemberStates().map(_.filterNot(country => country.countryCode == GB_PREFIX || country.countryCode == NI_PREFIX))
}
