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

package views.sections.movement

import base.SpecBase
import fixtures.messages.sections.movement.MovementReviewAnswersMessages.English
import forms.sections.movement.MovementReviewAnswersFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.test.FakeRequest
import utils.DateTimeUtils
import viewmodels.govuk.SummaryListFluency
import views.html.sections.movement.MovementReviewAnswersView
import views.{BaseSelectors, ViewBehaviours}

class MovementReviewAnswersViewSpec extends SpecBase with ViewBehaviours with DateTimeUtils with SummaryListFluency {

  object Selectors extends BaseSelectors

  "Movement Review Answers view" - {

    s"when being rendered in lang code of '${English.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(English.lang))
      implicit val request: DataRequest[_] = dataRequest(FakeRequest())

      lazy val view = app.injector.instanceOf[MovementReviewAnswersView]
      val form = app.injector.instanceOf[MovementReviewAnswersFormProvider].apply()

      implicit val doc: Document = Jsoup.parse(view(
        form = form,
        list = SummaryListViewModel(Seq.empty),
        onSubmitCall = testOnwardRoute
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.h2(1) -> English.movementDetailsSection,
        Selectors.legend -> English.question,
        Selectors.radioButton(1) -> English.yes,
        Selectors.radioButton(2) -> English.no,
        Selectors.button -> English.saveAndContinue
      ))
    }
  }
}
