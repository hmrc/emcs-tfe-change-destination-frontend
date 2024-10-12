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

package forms

import forms.mappings.Mappings
import models.UserAddress
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

import javax.inject.Inject

class AddressFormProvider @Inject() extends Mappings {

  val businessNameMax = 182
  val propertyMax = 11
  val streetMax = 65
  val townMax = 50
  val postcodeMax = 10

  def apply(): Form[UserAddress] =
    Form(mapping(
      "businessName" -> text(s"trader.businessName.error.required")
        .verifying(maxLength(businessNameMax, s"trader.businessName.error.length"))
        .verifying(regexpUnlessEmpty(XSS_REGEX, s"trader.businessName.error.invalid"))
        .transform[Option[String]](Some(_), _.get),

      "property" -> optional(text()
        .verifying(
          firstError(
            maxLength(propertyMax, s"trader.property.error.length"),
            regexp(ALPHANUMERIC_REGEX, s"trader.property.error.characters"),
            regexpUnlessEmpty(XSS_REGEX, s"trader.property.error.invalid")
          )
        )),
      "street" -> text(s"trader.street.error.required")
        .verifying(
          firstError(
            maxLength(streetMax, s"trader.street.error.length"),
            regexp(ALPHANUMERIC_REGEX, s"trader.street.error.characters"),
            regexpUnlessEmpty(XSS_REGEX, s"trader.street.error.invalid")
          )
        ),
      "town" -> text(s"trader.town.error.required")
        .verifying(
          firstError(
            maxLength(townMax, s"trader.town.error.length"),
            regexp(ALPHANUMERIC_REGEX, s"trader.town.error.characters"),
            regexpUnlessEmpty(XSS_REGEX, s"trader.town.error.invalid")
          )
        ),
      "postcode" -> text(s"trader.postcode.error.required")
        .verifying(
          firstError(
            maxLength(postcodeMax, s"trader.postcode.error.length"),
            regexp(ALPHANUMERIC_REGEX, s"trader.postcode.error.characters"),
            regexpUnlessEmpty(XSS_REGEX, s"trader.postcode.error.invalid")
          )
        )
    )(UserAddress.apply)(UserAddress.unapply))
}
