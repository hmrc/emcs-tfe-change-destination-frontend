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

package views.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorRequiredMessages.English
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.guarantor.GuarantorRequiredView
import views.{BaseSelectors, ViewBehaviours}

class GuarantorRequiredViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors


  s"newGuarantorIsRequired is true" - {

    implicit val msgs: Messages = messages(Seq(English.lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

    lazy val view = app.injector.instanceOf[GuarantorRequiredView]

    implicit val doc: Document = Jsoup.parse(view(testOnwardRoute, true).toString())

    behave like pageWithExpectedElementsAndMessages(Seq(
      Selectors.title -> English.titleRequiredUkToUk,
      Selectors.h1 -> English.headingRequiredUkToUk,
      Selectors.p(1) -> English.p1RequiredUkToUk,
      Selectors.p(2) -> English.p2Required,
      Selectors.link(1) -> English.findOutMore,
      Selectors.button -> English.enterDetails
    ))
  }

  s"newGuarantorIsRequired is false" - {

    "must show correct content and show the inset text" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

      lazy val view = app.injector.instanceOf[GuarantorRequiredView]

      implicit val doc: Document = Jsoup.parse(view(testOnwardRoute, false).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.titleRequiredNiToEu,
        Selectors.h1 -> English.headingRequiredNiToEu,
        Selectors.p(1) -> English.p1RequiredNiToEu,
        Selectors.p(2) -> English.p2Required,
        Selectors.link(1) -> English.findOutMore,
        Selectors.inset -> English.inset,
        Selectors.button -> English.enterDetails
      ))
    }
  }
}

