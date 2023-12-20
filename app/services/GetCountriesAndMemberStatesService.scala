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

import connectors.referenceData.GetCountriesAndMemberStatesConnector
import models.CountryModel
import models.response.CountriesAndMemberStatesException
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetCountriesAndMemberStatesService @Inject()(connector: GetCountriesAndMemberStatesConnector,
                                                   getMemberStatesService: GetMemberStatesService)
                                                  (implicit ec: ExecutionContext) {


  def getCountryCodesAndMemberStates()(implicit hc: HeaderCarrier): Future[Seq[CountryModel]] = {
    connector.getCountryCodesAndMemberStates().map {
      case Left(_) => throw CountriesAndMemberStatesException("No countries retrieved")
      case Right(value) => value
    }
  }

  def removeEUMemberStates(countriesIncludingMemberStates: Seq[CountryModel])(implicit hc: HeaderCarrier): Future[Seq[CountryModel]] = {
    getMemberStatesService.getMemberStates().map {
      memberStates => {
        val gb: CountryModel = countriesIncludingMemberStates.find(_.countryCode == "GB").get
        countriesIncludingMemberStates.diff(memberStates).filterNot(_.countryCode == "GR") ++ Seq(gb)
      }
    }
  }
}
