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

import base.SpecBase
import fixtures.messages.sections.items.ItemCommercialDescriptionMessages
import forms.sections.items.ItemCommercialDescriptionFormProvider
import models.GoodsType._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}

class ItemCommercialDescriptionHelperSpec extends SpecBase with GuiceOneAppPerSuite {

  lazy val helper = app.injector.instanceOf[ItemCommercialDescriptionHelper]
  lazy val form = app.injector.instanceOf[ItemCommercialDescriptionFormProvider]
  lazy val p = app.injector.instanceOf[views.html.components.p]
  lazy val bullets = app.injector.instanceOf[views.html.components.bullets]
  lazy val details = app.injector.instanceOf[views.html.components.details]

  "ItemCommercialDescriptionHelper" - {

    Seq(ItemCommercialDescriptionMessages.English).foreach { langMessages =>

      implicit val msgs: Messages = messages(Seq(langMessages.lang))

      s"when running for language code of '${langMessages.lang.code}'" - {

        "calling .content(goodsType : String)" - {

          "when the goodsType is beer" - {

            "must output the expected title" in {
              helper.content(Beer) mustBe
                details(langMessages.summary) {
                  HtmlFormat.fill(Seq(
                    p()(Html(langMessages.detailsBeerP)),
                    bullets(Seq(
                      p()(Html(langMessages.detailsBeerB1)),
                      p()(Html(langMessages.detailsBeerB2))
                    ))
                  ))
                }
            }
          }

          "when the goodsType is Wine" - {

            "must output the expected title" in {
              helper.content(Wine) mustBe
                details(langMessages.summary) {
                  HtmlFormat.fill(Seq(
                    p()(Html(langMessages.detailsWineP)),
                    bullets(Seq(
                      p()(Html(langMessages.detailsWineB1)),
                      p()(Html(langMessages.detailsWineB2)),
                      p()(Html(langMessages.detailsWineB3)),
                      p()(Html(langMessages.detailsWineB4)),
                      p()(Html(langMessages.detailsWineB5))
                    ))
                  ))
                }
            }

            "when the goodsType is Spirits" - {

              "must output the expected title" in {
                helper.content(Spirits) mustBe
                  details(langMessages.summary) {
                    HtmlFormat.fill(Seq(
                      p()(Html(langMessages.detailsEthylAlcoholP)),
                      bullets(Seq(
                        p()(Html(langMessages.detailsEthylAlcoholB1)),
                        p()(Html(langMessages.detailsEthylAlcoholB2)),
                        p()(Html(langMessages.detailsEthylAlcoholB3)),
                        p()(Html(langMessages.detailsEthylAlcoholB4))
                      ))
                    ))
                  }
              }
            }

            "when the goodsType is Tobacco" - {

              "must output the expected title" in {
                helper.content(Tobacco) mustBe
                  details(langMessages.summary) {
                    HtmlFormat.fill(Seq(
                      p()(Html(langMessages.detailsTobaccoP)),
                      bullets(Seq(
                        p()(Html(langMessages.detailsTobaccoB1)),
                        p()(Html(langMessages.detailsTobaccoB2)),
                        p()(Html(langMessages.detailsTobaccoB3))
                      ))
                    ))
                  }
              }
            }

            "when the goodsType is Intermediate" - {

              "must output the expected title" in {
                helper.content(Intermediate) mustBe Html("")
              }
            }

            "when the goodsType is Energy" - {

              "must output the expected title" in {
                helper.content(Energy) mustBe Html("")
              }
            }
          }
        }
      }
    }
  }
}
