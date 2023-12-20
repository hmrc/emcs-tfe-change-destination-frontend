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

package models.submitChangeDestination

import models.UserAddress
import play.api.libs.json.{Format, Json}

case class AddressModel(streetNumber: Option[String],
                        street: Option[String],
                        postcode: Option[String],
                        city: Option[String])

object AddressModel {

  def fromUserAddress(userAddress: UserAddress): AddressModel = AddressModel(
    streetNumber = userAddress.property,
    street = Some(userAddress.street),
    postcode = Some(userAddress.postcode),
    city = Some(userAddress.town)
  )

  implicit val fmt: Format[AddressModel] = Json.format
}