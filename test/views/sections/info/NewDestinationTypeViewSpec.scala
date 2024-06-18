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

package views.sections.info

import base.SpecBase
import fixtures.messages.sections.info.NewDestinationTypeMessages
import forms.sections.info.NewDestinationTypeFormProvider
import models.requests.DataRequest
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.info.NewDestinationTypeView
import views.{BaseSelectors, ViewBehaviours}

class NewDestinationTypeViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "New Destination Type view" - {

    Seq(NewDestinationTypeMessages.English).foreach { messagesForLanguage =>

      Seq("GBWK", "XIWK").foreach {
        ern =>
          s"for ERN starting with $ern, and dispatch place GB" - {
            implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), ern = s"${ern}123")

            lazy val view = app.injector.instanceOf[NewDestinationTypeView]
            val form = app.injector.instanceOf[NewDestinationTypeFormProvider].apply()

            implicit val doc: Document = Jsoup.parse(
              view(GreatBritain, form, controllers.sections.info.routes.NewDestinationTypeController.onPreDraftSubmit(request.ern, testArc)).toString()
            )

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.titleMovement,
              Selectors.h1 -> messagesForLanguage.headingMovement,
              Selectors.h2(1) -> messagesForLanguage.changeOfDestinationSection(testArc),
              Selectors.button -> messagesForLanguage.continue,
              Selectors.radioButton(1) -> messagesForLanguage.exportWithCustomsDeclarationLodgedInTheUk,
              Selectors.radioButton(2) -> messagesForLanguage.gbTaxWarehouse
            ))
          }
      }

      Seq(
        ("XIWK", messagesForLanguage.headingMovement, messagesForLanguage.titleMovement),
        ("XIRC", messagesForLanguage.headingImport, messagesForLanguage.titleImport)
      ).foreach {
        ernWithExpectedHeadingAndTitle =>
          val (ern, expectedHeading, expectedTitle) = ernWithExpectedHeadingAndTitle

          s"for ERN starting with $ern, dispatch place NI" - {
            implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
            implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), ern = s"${ern}123")

            lazy val view = app.injector.instanceOf[NewDestinationTypeView]
            val form = app.injector.instanceOf[NewDestinationTypeFormProvider].apply()

            implicit val doc: Document = Jsoup.parse(
              view(NorthernIreland, form, controllers.sections.info.routes.NewDestinationTypeController.onPreDraftSubmit(request.ern, testArc)).toString()
            )

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> expectedTitle,
              Selectors.h1 -> expectedHeading,
              Selectors.h2(1) -> messagesForLanguage.changeOfDestinationSection(testArc),
              Selectors.button -> messagesForLanguage.continue,
              Selectors.radioButton(1) -> messagesForLanguage.directDelivery,
              Selectors.radioButton(2) -> messagesForLanguage.exemptedOrganisation,
              Selectors.radioButton(3) -> messagesForLanguage.exportWithCustomsDeclarationLodgedInTheEu,
              Selectors.radioButton(4) -> messagesForLanguage.exportWithCustomsDeclarationLodgedInTheUk,
              Selectors.radioButton(5) -> messagesForLanguage.registeredConsignee,
              Selectors.radioButton(6) -> messagesForLanguage.euTaxWarehouse,
              Selectors.radioButton(7) -> messagesForLanguage.gbTaxWarehouse,
              Selectors.radioButton(8) -> messagesForLanguage.niTaxWarehouse,
              Selectors.radioButton(9) -> messagesForLanguage.temporaryRegisteredConsignee
            ))
          }
      }
    }
  }
}
