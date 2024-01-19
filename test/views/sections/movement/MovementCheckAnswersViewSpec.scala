/*
 * Copyright 2024 HM Revenue & Customs
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

package views.sections.movement

import base.SpecBase
import fixtures.messages.sections.movement.MovementCheckAnswersMessages.English
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.test.FakeRequest
import viewmodels.govuk.SummaryListFluency
import views.html.sections.movement.MovementCheckAnswersView
import views.{BaseSelectors, ViewBehaviours}

class MovementCheckAnswersViewSpec extends SpecBase with ViewBehaviours with SummaryListFluency {

  object Selectors extends BaseSelectors

  lazy val view: MovementCheckAnswersView = app.injector.instanceOf[MovementCheckAnswersView]

  "Movement Check Answers view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[_] = dataRequest(FakeRequest())

      implicit val doc: Document = Jsoup.parse(view(
        list = SummaryListViewModel(Seq.empty),
        onSubmitCall = testOnwardRoute
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.subHeadingCaptionSelector -> English.movementInformationSection,
        Selectors.button -> English.confirmAnswers
      ))
    }
  }
}
