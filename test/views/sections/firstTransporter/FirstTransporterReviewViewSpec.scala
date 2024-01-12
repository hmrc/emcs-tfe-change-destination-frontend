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

package views.sections.firstTransporter

import base.SpecBase
import fixtures.messages.sections.firstTransporter.FirstTransporterReviewMessages
import forms.sections.firstTransporter.FirstTransporterReviewFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import views.html.sections.firstTransporter.FirstTransporterReviewView
import views.{BaseSelectors, ViewBehaviours}

class FirstTransporterReviewViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors
  lazy val view: FirstTransporterReviewView = app.injector.instanceOf[FirstTransporterReviewView]
  lazy val form: FirstTransporterReviewFormProvider = app.injector.instanceOf[FirstTransporterReviewFormProvider]

  Seq(FirstTransporterReviewMessages.English).foreach { messagesForLanguage =>

    s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

      implicit val doc: Document = Jsoup.parse(view(
        form(),
        SummaryList(Seq.empty),
        controllers.sections.firstTransporter.routes.FirstTransporterReviewController.onSubmit(testErn, testArc)
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> messagesForLanguage.title,
        Selectors.subHeadingCaptionSelector -> messagesForLanguage.firstTransporterSection,
        Selectors.h1 -> messagesForLanguage.heading,
        Selectors.legend -> messagesForLanguage.legend,
        Selectors.radioButton(1) -> messagesForLanguage.yes,
        Selectors.radioButton(2) -> messagesForLanguage.no,
        Selectors.button -> messagesForLanguage.saveAndContinue
      ))
    }
  }

}
