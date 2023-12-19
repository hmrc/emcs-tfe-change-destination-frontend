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
import models.GoodsType._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}

import javax.inject.Inject

class ItemCommercialDescriptionHelper @Inject()(p: views.html.components.p,
                                                details: views.html.components.details,
                                                bullets: views.html.components.bullets
                                           ) {

  def content(goodsType: GoodsType)(implicit messages: Messages): Html =

    goodsType match {
      case Beer =>
        details("itemCommercialDescription.summary") {
          HtmlFormat.fill(Seq(
            p()(Html(messages("itemCommercialDescription.details.beer.p"))),
            bullets(Seq(
              p()(Html(messages("itemCommercialDescription.details.beer.b1"))),
              p()(Html(messages("itemCommercialDescription.details.beer.b2")))
            ))
          ))
        }
      case Wine =>
        details("itemCommercialDescription.summary") {
          HtmlFormat.fill(Seq(
            p()(Html(messages("itemCommercialDescription.details.wine.p"))),
            bullets(Seq(
              p()(Html(messages("itemCommercialDescription.details.wine.b1"))),
              p()(Html(messages("itemCommercialDescription.details.wine.b2"))),
              p()(Html(messages("itemCommercialDescription.details.wine.b3"))),
              p()(Html(messages("itemCommercialDescription.details.wine.b4"))),
              p()(Html(messages("itemCommercialDescription.details.wine.b5")))
            ))
          ))
        }
      case Tobacco =>
        details("itemCommercialDescription.summary") {
          HtmlFormat.fill(Seq(
            p()(Html(messages("itemCommercialDescription.details.tobacco.p"))),
            bullets(Seq(
              p()(Html(messages("itemCommercialDescription.details.tobacco.b1"))),
              p()(Html(messages("itemCommercialDescription.details.tobacco.b2"))),
              p()(Html(messages("itemCommercialDescription.details.tobacco.b3")))
            ))
          ))
        }
      case Spirits =>
        details("itemCommercialDescription.summary") {
          HtmlFormat.fill(Seq(
            p()(Html(messages("itemCommercialDescription.details.ethylAlcohol.p"))),
            bullets(Seq(
              p()(Html(messages("itemCommercialDescription.details.ethylAlcohol.b1"))),
              p()(Html(messages("itemCommercialDescription.details.ethylAlcohol.b2"))),
              p()(Html(messages("itemCommercialDescription.details.ethylAlcohol.b3"))),
              p()(Html(messages("itemCommercialDescription.details.ethylAlcohol.b4")))
            ))
          ))
        }
      case _ => Html("")
    }
}
