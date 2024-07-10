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
import forms.sections.guarantor.GuarantorRequiredFormProvider
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.guarantor.{GuarantorRequiredQuestionView, GuarantorRequiredView}
import views.{BaseSelectors, ViewBehaviours}

class GuarantorRequiredQuestionViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors


  s"GuarantorRequiredQuestionView must display the correct content" - {

    implicit val msgs: Messages = messages(Seq(English.lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

    lazy val view = app.injector.instanceOf[GuarantorRequiredQuestionView]
    lazy val formProvider = app.injector.instanceOf[GuarantorRequiredFormProvider]

    implicit val doc: Document = Jsoup.parse(view(formProvider(), testOnwardRoute).toString())

    behave like pageWithExpectedElementsAndMessages(Seq(
      Selectors.title -> English.titleNotRequired,
      Selectors.h1 -> English.headingNotRequired,
      Selectors.p(1) -> English.p1NotRequired,
      Selectors.link(1) -> English.findOutMore,
      Selectors.h2(2) -> English.subHeading1,
      Selectors.p(3) -> English.p2NotRequired,
      Selectors.bullet(1) -> English.bullet1,
      Selectors.bullet(2) -> English.bullet2,
      Selectors.h2(3) -> English.subHeading2,
      Selectors.button -> English.saveAndContinue,
      Selectors.saveAndExitLink -> English.returnToDraft
    ))
  }
}

