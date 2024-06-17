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

package views.sections.destination

import base.SpecBase
import fixtures.messages.sections.destination.DestinationBusinessNameMessages
import forms.sections.destination.DestinationBusinessNameFormProvider
import models.NormalMode
import models.requests.DataRequest
import models.sections.info.movementScenario.MovementScenario.{DirectDelivery, UkTaxWarehouse}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.destination.DestinationBusinessNameView
import views.{BaseSelectors, ViewBehaviours}

class DestinationBusinessNameViewSpec extends SpecBase with ViewBehaviours {
  object Selectors extends BaseSelectors

  "Destination Business Name view" - {

    Seq(DestinationBusinessNameMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' and destination type is not 'DirectDelivery'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        lazy val view = app.injector.instanceOf[DestinationBusinessNameView]
        val form = app.injector.instanceOf[DestinationBusinessNameFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, UkTaxWarehouse.GB, controllers.sections.destination.routes.DestinationBusinessNameController.skipThisQuestion(testErn, testArc, NormalMode)).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.destinationSection,
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.returnToDraft
        ))
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' when destination type is 'Direct Delivery'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), emptyUserAnswers)

        lazy val view = app.injector.instanceOf[DestinationBusinessNameView]
        val form = app.injector.instanceOf[DestinationBusinessNameFormProvider].apply()

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, DirectDelivery, controllers.sections.destination.routes.DestinationBusinessNameController.skipThisQuestion(testErn, testArc, NormalMode)).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.destinationSection,
          Selectors.title -> messagesForLanguage.titleOptional,
          Selectors.h1 -> messagesForLanguage.headingOptional,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.link(1) -> messagesForLanguage.skipQuestion
        ))
      }
    }
  }
}

