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

import forms.XSS_REGEX

import javax.inject.Inject
import forms.mappings.Mappings
import models.GoodsType
import models.sections.items.ItemMaturationPeriodAgeModel
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text => playText}
import play.api.i18n.Messages

class ItemMaturationPeriodAgeFormProvider @Inject() extends Mappings {

  import ItemMaturationPeriodAgeFormProvider._

  def apply(goodsType: GoodsType)(implicit messages: Messages): Form[ItemMaturationPeriodAgeModel] =
    Form(
      mapping(
        hasMaturationPeriodAgeField -> boolean(radioRequired, args = Seq(messages(goodsType.toSingularOutput()))),
        maturationPeriodAgeField -> optional(
          playText()
            .verifying(maxLength(maturationPeriodAgeMaxLength, maturationPeriodAgeLength))
            .verifying(regexp(XSS_REGEX, maturationPeriodAgeInvalid))
        )
      )(ItemMaturationPeriodAgeModel.apply)(ItemMaturationPeriodAgeModel.unapply)
        .transform[ItemMaturationPeriodAgeModel](
          model => if(!model.hasMaturationPeriodAge) model.copy(maturationPeriodAge = None) else model, identity
        )
    )
}

object ItemMaturationPeriodAgeFormProvider {
  val hasMaturationPeriodAgeField: String = "hasMaturationPeriodAge"
  val maturationPeriodAgeField: String = "maturationPeriodAge"

  val radioRequired = "itemMaturationPeriodAge.error.radio.required"
  val maturationPeriodAgeRequired = "itemMaturationPeriodAge.error.maturationPeriodAge.required"
  val maturationPeriodAgeInvalid = "itemMaturationPeriodAge.error.maturationPeriodAge.invalid"
  val maturationPeriodAgeLength = "itemMaturationPeriodAge.error.maturationPeriodAge.length"

  val maturationPeriodAgeMaxLength: Int = 350
}
