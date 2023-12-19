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

package viewmodels.helpers

import models.GoodsType
import models.GoodsType.{Beer, Spirits}
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.{RadioItem, Radios}
import viewmodels.LegendSize
import viewmodels.govuk.all._

object ItemSmallIndependentProducerHelper {

  def yesMessageFor(goodsType: GoodsType)(implicit messages: Messages): String = {
    val key = goodsType match {
      case Beer => "beer"
      case Spirits => "spirits"
      case _ => "other"
    }
    messages(s"itemSmallIndependentProducer.yes", messages(s"itemSmallIndependentProducer.yes.$key"))
  }

  def radios(form: Form[_], goodsType: GoodsType)(implicit messages: Messages): Radios =
    RadiosViewModel.apply(
      form("value"),
      items = Seq(
        RadioItem(
          id = Some(form("value").id),
          value = Some("true"),
          content = Text(yesMessageFor(goodsType))
        ),
        RadioItem(
          id = Some(s"${form("value").id}-no"),
          value = Some("false"),
          content = Text(messages("site.no"))
        )
      ),
      LegendViewModel(Text(messages("itemSmallIndependentProducer.heading", goodsType.toSingularOutput()))).asPageHeading(LegendSize.Large)
    )

}
