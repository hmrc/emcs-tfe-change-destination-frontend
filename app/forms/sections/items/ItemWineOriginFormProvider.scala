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

package forms.sections.items

import javax.inject.Inject
import forms.mappings.Mappings
import models.CountryModel
import play.api.data.Form

class ItemWineOriginFormProvider @Inject() extends Mappings {

  def apply(countries: Seq[CountryModel]): Form[CountryModel] =
    Form(
      "country" -> text("itemWineOrigin.error.required")
        .verifying("itemWineOrigin.error.required", enteredCountryCode => countries.exists(_.countryCode == enteredCountryCode))
        .transform[CountryModel](enteredCountryCode => countries.find(_.countryCode == enteredCountryCode).get, _.countryCode)
    )
}
