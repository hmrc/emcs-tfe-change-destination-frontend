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

package models

import models.response.emcsTfe.TraderModel
import play.api.libs.json.{Json, OFormat}
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent

case class UserAddress(
                        businessName: Option[String],
                        property: Option[String],
                        street: String,
                        town: String,
                        postcode: String
                      ) {

  def toCheckYourAnswersFormat: HtmlContent = HtmlContent(
    HtmlFormat.fill(
      Seq(
        Html(businessName.fold("")(_ + "<br>")),
        Html(property.fold("")(_ + " ") + street + "<br>"),
        Html(town + "<br>"),
        Html(postcode)
      )
    )
  )

}

object UserAddress {

  implicit lazy val format: OFormat[UserAddress] = Json.format[UserAddress]

  def userAddressFromTraderAddress(trader: TraderModel): UserAddress = UserAddress(
    businessName = trader.traderName,
    property = trader.address.flatMap(_.streetNumber),
    street = trader.address.flatMap(_.street).getOrElse(""),
    town = trader.address.flatMap(_.city).getOrElse(""),
    postcode = trader.address.flatMap(_.postcode).getOrElse("")
  )
}
