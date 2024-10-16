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

package views.sections.transportArranger

import base.SpecBase
import fixtures.messages.sections.transportArranger.TransportArrangerVatMessages
import forms.sections.transportArranger.TransportArrangerVatFormProvider
import models.requests.DataRequest
import models.sections.transportArranger.TransportArranger
import models.sections.transportArranger.TransportArranger.{GoodsOwner, Other}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Lang, Messages}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.transportArranger.TransportArrangerVatView
import views.{BaseSelectors, ViewBehaviours}

class TransportArrangerVatViewSpec extends SpecBase with ViewBehaviours {

  class Fixture(arranger: TransportArranger, lang: Lang) {

    implicit val msgs: Messages = messages(Seq(lang))
    implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

    lazy val view = app.injector.instanceOf[TransportArrangerVatView]
    val form = app.injector.instanceOf[TransportArrangerVatFormProvider].apply(arranger)

    implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, arranger).toString())
  }

  object Selectors extends BaseSelectors

  "TransportArrangerVatView" - {

    Seq(TransportArrangerVatMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when rendered for GoodsOwner" - new Fixture(GoodsOwner, messagesForLanguage.lang) {

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.goodsOwnerTitle,
            Selectors.h1 -> messagesForLanguage.goodsOwnerHeading,
            Selectors.hint -> messagesForLanguage.hint,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.label(TransportArrangerVatFormProvider.vatNumberField) -> messagesForLanguage.vatNumberLabel,
            //Note, this is radio button 2 but index is 3 due to hidden HTML conditional content for radio 1
            Selectors.radioButton(3) -> messagesForLanguage.no,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
          ))
        }

        "when rendered for Other" - new Fixture(Other, messagesForLanguage.lang) {

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.otherTitle,
            Selectors.h1 -> messagesForLanguage.otherHeading,
            Selectors.hint -> messagesForLanguage.hint,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.label(TransportArrangerVatFormProvider.vatNumberField) -> messagesForLanguage.vatNumberLabel,
            //Note, this is radio button 2 but index is 3 due to hidden HTML conditional content for radio 1
            Selectors.radioButton(3) -> messagesForLanguage.no,
            Selectors.button -> messagesForLanguage.saveAndContinue,
            Selectors.saveAndExitLink -> messagesForLanguage.returnToDraft
          ))
        }
      }
    }
  }
}

