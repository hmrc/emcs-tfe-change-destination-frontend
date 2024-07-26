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

package views.templates

import base.SpecBase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.ViewBehaviours
import views.html.templates.Layout

class LayoutTemplateSpec extends SpecBase with ViewBehaviours {

  lazy val template: Layout = app.injector.instanceOf[Layout]
  val contentBlock: Html = Html("Main Content")

  "must render NavBar when supplied from request" - {

    Seq(
      userRequest(FakeRequest(), navBar = Some(Html("NavBar"))),
      movementRequest(FakeRequest(), navBar = Some(Html("NavBar"))),
      optionalDataRequest(FakeRequest(), navBar = Some(Html("NavBar"))),
      dataRequest(FakeRequest(), navBar = Some(Html("NavBar")))
    ).foreach(implicit request => {

      s"when the request is of type ${request.getClass.getSimpleName}" in {
        implicit val msgs = messages(request)
        val doc: Document = Jsoup.parse(template(pageTitle = "Title", maybeShowActiveTrader = None)(contentBlock).toString())
        doc.html().contains("NavBar") mustBe true
      }
    })
  }

  "must NOT render NavBar when NOT supplied from request" - {

    Seq(
      userRequest(FakeRequest()),
      movementRequest(FakeRequest()),
      dataRequest(FakeRequest()),
      optionalDataRequest(FakeRequest()),
      FakeRequest()
    ).foreach(implicit request => {

      s"when the request is of type ${request.getClass.getSimpleName}" in {
        implicit val msgs = messages(request)
        val doc: Document = Jsoup.parse(template(pageTitle = "Title", maybeShowActiveTrader = None)(contentBlock).toString())
        doc.html().contains("NavBar") mustBe false
      }
    })
  }
}
