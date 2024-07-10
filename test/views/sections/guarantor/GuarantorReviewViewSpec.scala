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

package views.sections.guarantor

import base.SpecBase
import fixtures.messages.sections.guarantor.GuarantorReviewMessages
import forms.sections.guarantor.GuarantorReviewFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import views.html.sections.guarantor.GuarantorReviewView
import views.{BaseSelectors, ViewBehaviours}

class GuarantorReviewViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors
  lazy val view: GuarantorReviewView = app.injector.instanceOf[GuarantorReviewView]
  lazy val form: GuarantorReviewFormProvider = app.injector.instanceOf[GuarantorReviewFormProvider]

  Seq(GuarantorReviewMessages.English).foreach { messagesForLanguage =>

    s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
      implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

      implicit val doc: Document = Jsoup.parse(view(
        form(),
        SummaryList(Seq.empty),
        controllers.sections.guarantor.routes.GuarantorReviewController.onSubmit(testErn, testArc)
      ).toString())

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> messagesForLanguage.title,
        Selectors.subHeadingCaptionSelector -> messagesForLanguage.guarantorSection,
        Selectors.h1 -> messagesForLanguage.heading,
        Selectors.legend -> messagesForLanguage.legend,
        Selectors.radioButton(1) -> messagesForLanguage.yes,
        Selectors.radioButton(2) -> messagesForLanguage.no,
        Selectors.button -> messagesForLanguage.saveAndContinue
      ))
    }
  }

}
