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

package views

import base.ViewSpecBase
import config.AppConfig
import fixtures.messages.JourneyRecoveryMessages
import models.requests.UserRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.JourneyRecoveryStartAgainView

class JourneyRecoveryViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "JourneyRecovery view" - {

    Seq(JourneyRecoveryMessages.English, JourneyRecoveryMessages.Welsh).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        implicit val msgs: Messages = messages(app, messagesForLanguage.lang)
        implicit val request: UserRequest[AnyContentAsEmpty.type] = userRequest(FakeRequest())
        implicit val config: AppConfig = app.injector.instanceOf[AppConfig]

        val view = app.injector.instanceOf[JourneyRecoveryStartAgainView]

        implicit val doc: Document = Jsoup.parse(view().toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.title -> messagesForLanguage.title,
          Selectors.p(1) -> messagesForLanguage.p1,
          Selectors.p(2) -> messagesForLanguage.p2,
          Selectors.numbered(1) -> messagesForLanguage.numbered1,
          Selectors.numbered(2) -> messagesForLanguage.numbered2,
          Selectors.numbered(3) -> messagesForLanguage.numbered3,
          Selectors.p(3) -> messagesForLanguage.p3
        ))

        "have a link to view the movements" in {

          doc.select(Selectors.numbered(1)).select("a").attr("href") mustBe
            config.emcsMovementsUrl(testErn)
        }

        "have a link back to the EMCS home" in {

          doc.select(Selectors.p(3)).select("a:nth-of-type(1)").attr("href") mustBe
            config.emcsTfeHomeUrl(Some(testErn))
        }

        "have a link back to sign out" in {

          doc.select(Selectors.p(3)).select("a:nth-of-type(2)").attr("href") mustBe
            controllers.auth.routes.AuthController.signOut().url
        }
      }
    }
  }


}
