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

package views.sections.transportUnit

import base.SpecBase
import fixtures.messages.sections.transportUnit.TransportUnitGiveMoreInformationChoiceMessages
import forms.sections.transportUnit.TransportUnitGiveMoreInformationChoiceFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.transportUnit.TransportUnitType._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.transportUnit.TransportUnitGiveMoreInformationChoiceView
import views.{BaseSelectors, ViewBehaviours}

class TransportUnitGiveMoreInformationChoiceViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "TransportUnitGiveMoreInformationChoiceView" - {

    Seq(TransportUnitGiveMoreInformationChoiceMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' - for transport unit type: Container" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        lazy val view = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceView]
        val form = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceFormProvider].apply(Container)

        implicit val doc: Document = Jsoup.parse(
          view(
            form = form,
            idx = testIndex1,
            mode = NormalMode,
            transportUnitType = Container
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title("container"),
          Selectors.h1 -> messagesForLanguage.heading("container"),
          Selectors.hint -> messagesForLanguage.hintText,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' - for transport unit type: Fixed transport installation" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

       lazy val view = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceView]
        val form = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceFormProvider].apply(FixedTransport)

        implicit val doc: Document = Jsoup.parse(
          view(
            form = form,
            idx = testIndex1,
            mode = NormalMode,
            transportUnitType = FixedTransport
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title("fixed transport installation"),
          Selectors.h1 -> messagesForLanguage.heading("fixed transport installation"),
          Selectors.hint -> messagesForLanguage.hintText,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' - for transport unit type: Tractor" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

       lazy val view = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceView]
        val form = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceFormProvider].apply(Tractor)

        implicit val doc: Document = Jsoup.parse(
          view(
            form = form,
            idx = testIndex1,
            mode = NormalMode,
            transportUnitType = Tractor
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title("tractor"),
          Selectors.h1 -> messagesForLanguage.heading("tractor"),
          Selectors.hint -> messagesForLanguage.hintText,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' - for transport unit type: Trailer" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

       lazy val view = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceView]
        val form = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceFormProvider].apply(Trailer)

        implicit val doc: Document = Jsoup.parse(
          view(
            form = form,
            idx = testIndex1,
            mode = NormalMode,
            transportUnitType = Trailer
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title("trailer"),
          Selectors.h1 -> messagesForLanguage.heading("trailer"),
          Selectors.hint -> messagesForLanguage.hintText,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' - for transport unit type: Vehicle" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

       lazy val view = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceView]
        val form = app.injector.instanceOf[TransportUnitGiveMoreInformationChoiceFormProvider].apply(Vehicle)

        implicit val doc: Document = Jsoup.parse(
          view(
            form = form,
            mode = NormalMode,
            idx = testIndex1,
            transportUnitType = Vehicle
          ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title("vehicle"),
          Selectors.h1 -> messagesForLanguage.heading("vehicle"),
          Selectors.hint -> messagesForLanguage.hintText,
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }
    }
  }
}

