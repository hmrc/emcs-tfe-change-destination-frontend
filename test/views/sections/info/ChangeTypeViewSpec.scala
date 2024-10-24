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

package views.sections.info

import base.SpecBase
import fixtures.messages.sections.info.ChangeTypeMessages
import forms.sections.info.ChangeTypeFormProvider
import models.requests.DataRequest
import models.sections.info.movementScenario.DestinationType
import models.sections.info.movementScenario.DestinationType.{Export, dutyPaidDestinationTypes}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.sections.info.ChangeTypeView
import views.{BaseSelectors, ViewBehaviours}

class ChangeTypeViewSpec extends SpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "Change Type view" - {

    Seq(ChangeTypeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" - {

        "when destination type is Duty Paid" - {

          DestinationType.dutyPaidDestinationTypes.foreach { destinationType =>

            s"when destination type is '$destinationType'" - {

              implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
              implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
                FakeRequest(),
                movementDetails = maxGetMovementResponse.copy(destinationType = destinationType)
              )

              lazy val view = app.injector.instanceOf[ChangeTypeView]
              val form = app.injector.instanceOf[ChangeTypeFormProvider].apply()

              implicit val doc: Document = Jsoup.parse(view(form, controllers.sections.info.routes.ChangeTypeController.onSubmit(request.ern, testArc)).toString())

              behave like pageWithExpectedElementsAndMessages(Seq(
                Selectors.title -> messagesForLanguage.title,
                Selectors.h1 -> messagesForLanguage.heading,
                Selectors.h2(1) -> messagesForLanguage.changeOfDestinationSection(testArc),
                Selectors.radioButton(1) -> messagesForLanguage.destinationRadio,
                Selectors.radioButton(2) -> messagesForLanguage.returnToSenderRadio,
                Selectors.button -> messagesForLanguage.continue
              ))
            }
          }
        }

        "when destination type is Export" - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
          implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
            FakeRequest(),
            movementDetails = maxGetMovementResponse.copy(destinationType = Export)
          )

          lazy val view = app.injector.instanceOf[ChangeTypeView]
          val form = app.injector.instanceOf[ChangeTypeFormProvider].apply()

          implicit val doc: Document = Jsoup.parse(view(form, controllers.sections.info.routes.ChangeTypeController.onSubmit(request.ern, testArc)).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.changeOfDestinationSection(testArc),
            Selectors.radioButton(1) -> messagesForLanguage.consigneeRadio,
            Selectors.radioButton(2) -> messagesForLanguage.exportOfficeRadio,
            Selectors.hint -> messagesForLanguage.exportOfficeHint,
            Selectors.button -> messagesForLanguage.continue
          ))
        }

        "when destination type is anything else" - {

          DestinationType.values
            .filterNot(_ == Export)
            .filterNot(dutyPaidDestinationTypes.contains).foreach { destinationType =>

            s"when destination type is '$destinationType'" - {

              implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
              implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(
                FakeRequest(),
                movementDetails = maxGetMovementResponse.copy(destinationType = destinationType)
              )

              lazy val view = app.injector.instanceOf[ChangeTypeView]
              val form = app.injector.instanceOf[ChangeTypeFormProvider].apply()

              implicit val doc: Document = Jsoup.parse(view(form, controllers.sections.info.routes.ChangeTypeController.onSubmit(request.ern, testArc)).toString())

              behave like pageWithExpectedElementsAndMessages(Seq(
                Selectors.title -> messagesForLanguage.title,
                Selectors.h1 -> messagesForLanguage.heading,
                Selectors.h2(1) -> messagesForLanguage.changeOfDestinationSection(testArc),
                Selectors.radioButton(1) -> messagesForLanguage.consigneeRadio,
                Selectors.radioButton(2) -> messagesForLanguage.destinationRadio,
                Selectors.button -> messagesForLanguage.continue
              ))
            }
          }
        }
      }
    }
  }
}
